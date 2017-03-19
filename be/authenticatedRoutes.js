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

router.use(/.*\/self\/.*|^\/users\/self$|^\/entries\/\w*\/comments$|^\/entries\/\w*\/comments\/\w*$|^\/users\/\w*\/(un)?follow$/,
	function(req, res, next) {
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

router.put('/users/self', (req, res) => {
	let username = req.username
	let oldPassword = req.body.oldPassword
	let newPassword = req.body.newPassword
	let newDisplayName = req.body.newDisplayName

	if (!newPassword && !newDisplayName) {
		return res.status(400).send({ success: false , message: 'Malformed request'})
	}

  User.findOne({
    username: username,
    password: oldPassword
  }, function(err, user) {

		if (err || !user) {
			return res.status(404).send({ success: false , message: 'Error: Incorrect password'})
		}

		let changedParameters = ''

		if (newPassword) {
			user.password = newPassword
			changedParameters += 'password '
		}

		if (newDisplayName) {
			user.displayName = newDisplayName
			changedParameters += 'displayName'
		}

		user.save()
	    .then(function (doc) {
	      return res.status(200).json({ success: true, message: "Changed user information: " + changedParameters})
	    })
	    .catch(function(err) {
	      return res.status(400).json({ success: false, message: "Error saving changes"})
	    })
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

	let newCapture = {text: text};

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
					notifyFollowers(username, entry._id)
					return res.status(200).json({ success: true, message: 'Added to existing date'})
				})
		} else {

			let newEntry = new JournalEntry({
			  username: username,
			  entryDate: captureDate,
			  captures: [newCapture]
			})

			newEntry.save()
			  .then(function (createdEntry) {
					notifyFollowers(username, createdEntry._id)
					return res.status(200).json({ success: true, message: 'Created new journal entry'})
			  })
			  .catch(function(err) {
					console.log(err)
					return res.status(400).json({ success: false })
			  })
		}
  })
})

function notifyFollowers(username, entryId) {
	User.findOne({
		username: username
	}, 'followers', function(err, userInfo) {
		if (err || !userInfo) {
			console.log("error notifying followers")
			return
		}

		User.updateMany({
			'username': { $in: userInfo.followers },
			'notifications': { $nin: [entryId] }
		}, {
			$push: {
				notifications: {
					$each: [entryId.toString()],
					$slice: -10
				}
			}
		}, function(err, stats){
			console.log("Notified " + stats.nModified + " people")
		})
	})
}

router.get('/users/self/entries', function(req, res) {
	let username = req.username
	res.redirect('/users/' + username + '/entries')
})

router.delete('/users/self/entries/:entryId', function(req, res) {
	let username = req.username
	let entryId = req.params.entryId

	JournalEntry.findById(entryId, function(err, entry) {
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

router.put('/users/self/captures/:captureId', function(req, res) {
	let captureId = req.params.captureId
	let text = req.body.text
	let username = req.username

	JournalEntry.findOneAndUpdate({
		"username": username,
		"captures._id": mongoose.Types.ObjectId(captureId)
	}, {
		"captures.$.text": text
	}, function(err, entry) {
		if (err || !entry) {
			return res.status(400).json({ success: false })
		}

		return res.status(200).json({ success: true })
	})
})

/*
---------------------------------------------------------
Comments
---------------------------------------------------------
*/

router.post('/entries/:entryId/comments', function(req, res) {
	let commenter = req.username
	let text = req.body.text
	let entryId = req.params.entryId

	let newComment = {commenter: commenter, text: text}

	console.log(newComment)

  JournalEntry.findById(entryId, function(err, entry) {
		if (err || !entry) {
			return res.status(400).json({ success: false })
		}

		entry.comments.push(newComment)

		entry.save()
		  .then(function (doc) {
		    return res.status(200).json({ success: true, message: 'Comment created'})
		  })
		  .catch(function(err) {
		    return res.status(400).json({ success: false })
		  })
	})
})

router.delete('/entries/:entryId/comments/:commentId', function(req, res) {
	let commenter = req.username
	let entryId = req.params.entryId
	let commentId = req.params.commentId

	console.log(commenter)

  JournalEntry.findOne({
		"comments._id": mongoose.Types.ObjectId(commentId),
		"comments.commenter": commenter
  }, function(err, entry) {
		if (err || !entry) {
			return res.status(400).json({ success: false })
		}

		entry.update({'$pull': {'comments': {'_id': mongoose.Types.ObjectId(commentId)}}})
			.then(function() {
				return res.status(200).json({ success: true })
			})
			.catch(function(err) {
				return res.status(400).json({ success: false })
			})
	})
})

/*
---------------------------------------------------------
Following
---------------------------------------------------------
*/

router.post('/users/:username/follow', function(req, res) {
	let toFollowUsername = req.params.username
	let currentUsername = req.username

	User.findOne({
	  username: currentUsername
	}, function(err, currentUser) {

		if (err || !currentUser) {
			return res.status(400).json({success: false})
		}

		User.findOne({
		  username: toFollowUsername
		}, function(err, userToFollow) {

			if (err || !userToFollow) {
				return res.status(400).json({success: false, message: 'User not found'})
			}

			currentUser.following.push(toFollowUsername)
			userToFollow.followers.push(currentUsername)

			currentUser.save()
				.then(function(doc) {
					userToFollow.save()
				})
				.then(function (doc) {
					return res.status(200).json({ success: true })
			  })
			  .catch(function(err) {
					return res.status(400).json({ success: false })
			  })
		})
	})
})

router.post('/users/:username/unfollow', function(req, res) {
	let toUnfollowUsername = req.params.username
	let currentUsername = req.username

	User.findOneAndUpdate({
		username: currentUsername
	}, {$pull: {following: toUnfollowUsername}}, function(err, data){
    if (err) {
      return res.status(400).json({success: false})
    }

    User.findOneAndUpdate({
			username: toUnfollowUsername
		}, {$pull: {followers: currentUsername}}, function(err, data){
	    if (err) {
	      return res.status(400).json({success: false})
	    }

	    return res.status(200).json({ success: true })
	  })
  })
})

/*
---------------------------------------------------------
 Notifications
---------------------------------------------------------
*/

router.get('/users/self/notifications', function(req, res) {
	let username = req.username

	User.findOne({
	  username: username
	}, 'notifications', function(err, userInfo) {

		if (err || !userInfo) {
			return res.status(400).json({success: false, message: 'Notifications not found'})
		}

		JournalEntry.find({
    '_id': { $in: userInfo.notifications}
		}, function(err, docs){

			if (err || !docs) {
				return res.status(400).json({success: false, message: 'Server error'})
			}

			return res.status(200).json(docs.reverse())
		})
	})
})

/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router



