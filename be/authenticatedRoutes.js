const express = require('express')
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const JournalEntry   = require('./app/models/journalentry')
const config = require('./config')
const multer = require('multer')

const KB = 1024
const MB = KB*KB

/*
---------------------------------------------------------
 Pre-processing Token Check
---------------------------------------------------------
*/

router.use(/.*\/self\/.*/, function(req, res, next) {
	var token = req.body.token || req.headers['x-access-token']

	if (token) {
		jwt.verify(token, config.secret, function(err, decoded) {
			if (err) {
				return res.json({ success: false, message: 'Failed to authenticate token.' })
			}

			req.username = decoded.username
			next()
		})

	} else {
		return res.status(403).send({
			success: false,
			message: 'No token provided.'
		})
	}
})

/*
---------------------------------------------------------
 Authenticated Routes
---------------------------------------------------------
*/
router.get('/self/hi', function(req, res) {
	console.log(req.username) //how you get user information
	res.json({ message: 'Welcome to the coolest API on earth!' })
})


var upload = multer({ storage: multer.memoryStorage({}) })

router.post('/users/self/picture', upload.single('profilePicture'), (req, res) => {

	if (req.file.size > 16*MB) {
		return res.status(400).json({ success: false, message: 'File too large' })
	}

	switch (req.file.mimetype) {
		case 'image/bpm':
		case 'image/gif':
		case 'image/jpeg':
		case 'image/png':
		case 'image/tiff':
			break
		default:
			return res.status(415).json({ success: false, message: 'File type not supported' })
	}

  let username = req.username

	User.findOne({
	  username: username
	}, function(err, user) {
		if (err) throw err

		if (!user) {
			return res.status(400).json({success: false, message: 'User not found'})
		}

		user.profilePicture = {data: req.file.buffer, contentType: req.file.mimetype}

		user.save()
	    .then(function (doc) {
	      return res.status(200).json({ success: true })
	    })
	    .catch(function(err) {
	      return res.status(400).json({ success: false })
	    })
	})
})

router.get('/users/self/picture', (req, res) => {

	let username = req.username

  User.findOne({
    username: username
  }, function(err, user) {

		if (err || !user) {
			return res.status(404).send({ success: false })
		}

		if (!user.profilePicture) {
			return res.status(404).send({ success: false, message: 'User does not have a profile picture'})
		}

    let imageBuffer = user.profilePicture.data
    let imageType = user.profilePicture.contentType

    let img = new Buffer(imageBuffer, 'base64')
    res.writeHead(200, {
      'Content-Type': imageType,
      'Content-Length': img.length
    })
    res.status(200).end(img)
  })
})



router.post('/users/self/captures', function(req, res) {
  let username = req.username
  let text = req.body.text
  let captureDate = req.body.captureDate

  JournalEntry.findOne({
		username: username,
		entryDate: captureDate
  }, function(err, entry) {

		if (entry) {
			entry.captures.push({text: text})
			entry.save()
				.then(function(doc) {
					return res.status(200).json({ success: true, message: "Added to existing date"})
				})
		} else {

			let newEntry = new JournalEntry({
			  username: username,
			  entryDate: captureDate,
			  captures: [{text: text}]
			})

			newEntry.save()
			  .then(function (doc) {
			    return res.status(200).json({ success: true })
			  })
			  .catch(function(err) {
			    console.log(err)
			    return res.status(400).json({ success: false })
			  })
		}
  })
})

router.get('/users/self/entries', function(req, res) {
	let username = req.username
	res.redirect('/users/' + username + '/captures')
})


/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router



