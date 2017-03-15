const express = require('express')
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const JournalEntry   = require('./app/models/journalentry')
const config = require('./config')
const multer = require('multer')
const mongoose = require('mongoose')
const _ = require('lodash')

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
User Information
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


/*
---------------------------------------------------------
Journal Entries
---------------------------------------------------------
*/

router.post('/users/self/captures', upload.single('image'), function(req, res) {
	let username = req.username
	let text = req.body.text
	let captureDate = req.body.captureDate
	let file = req.file

	let newCapture = {text: text, _id: mongoose.Types.ObjectId()};

	if (file) {
		if (file.size > 16*MB) {
			return res.status(400).json({ success: false, message: 'File too large' })
		}

		switch (file.mimetype) {
			case 'image/bpm':
			case 'image/gif':
			case 'image/jpeg':
			case 'image/png':
			case 'image/tiff':
				break
			default:
				return res.status(415).json({ success: false, message: 'File type not supported' })
		}

		newCapture.image = {data: req.file.buffer, contentType: req.file.mimetype}
	}

  JournalEntry.findOne({
		username: username,
		entryDate: captureDate
  }, function(err, entry) {

		if (entry) {
			entry.captures.push(newCapture)

			entry.save()
				.then(function(doc) {
					return res.status(200).json({ success: true, message: 'Added to existing date'})
				})
		} else {

			let newEntry = new JournalEntry({
			  username: username,
			  entryDate: captureDate,
			  captures: [newCapture]
			})

			newEntry.save()
			  .then(function (doc) {
			    return res.status(200).json({ success: true, message: 'Created new journal entry'})
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
	res.redirect('/users/' + username + '/entries')
})

router.delete('/users/self/entries/:entryId', function(req, res) {
	let username = req.username
	let entryId = req.params.entryId

	JournalEntry.findOne({
		_id: entryId
  }, function(err, entry) {
		if (err) throw err
		if (!entry) {
      return res.status(400).json({ success: false, message: 'Entry not found'})
    }

    entry.remove()
      .then(function () {
        return res.status(200).json({ success: true})
      })
      .catch(function(err) {
        return res.status(400).json({ success: false })
      })
    })
})

router.delete('/users/self/captures/:captureId', function(req, res) {
	let username = req.username
	let captureId = req.params.captureId

	JournalEntry.findOne({
		"captures._id": mongoose.Types.ObjectId(captureId)
	}, function(err, entry) {
		if (err || !entry) {
			return res.status(400).json({ success: false })
		}

		if (entry.captures.length === 1) {
			entry.remove()
				.then(function () {
					return res.status(200).json({ success: true, message: "Deleted entire entry"})
				})
				.catch(function(err) {
					return res.status(400).json({ success: false })
				})
		} else {
			entry.update({'$pull': {'captures': {'_id': mongoose.Types.ObjectId(captureId)}}})
				.then(function() {
					return res.status(200).json({ success: true })
				})
				.catch(function(err) {
					return res.status(400).json({ success: false })
				})
			}
	})
})

router.put('/users/self/entries/:entryId', function(req, res) {
	let entryId = req.params.entryId
	let title = req.body.title

	JournalEntry.findByIdAndUpdate(entryId, {
		$set: { title: title }
	}, function (err, entry) {
		if (err) {
			return res.status(400).json({ success: false })
		}

		return res.status(200).json({ success: true })
	})
})


/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router



