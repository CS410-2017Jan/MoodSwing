const express = require('express')
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const JournalEntry   = require('./app/models/journalentry')
const config = require('./config')

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

			req.user = decoded['_doc']
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
	console.log(req.user) //how you get user information
	res.json({ message: 'Welcome to the coolest API on earth!' })
})


router.post('/users/self/captures', function(req, res) {
  let username = req.user.username
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
					return res.json({ success: true, message: "Added to existing date"})
				})
		} else {

			let newEntry = new JournalEntry({
			  username: username,
			  entryDate: captureDate,
			  captures: [{text: text}]
			})

			newEntry.save()
			  .then(function (doc) {
			    return res.json({ success: true })
			  })
			  .catch(function(err) {
			    console.log(err)
			    return res.json({ success: false })
			  })
		}
  })
})


/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router



