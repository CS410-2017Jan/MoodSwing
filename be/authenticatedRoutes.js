const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User   = require('./app/models/user');
const JournalEntry   = require('./app/models/journalentry');
const config = require('./config');
const multer = require('multer');
const mongoose = require('mongoose');
const gm = require('gm');
const _ = require('lodash');
const HttpStatus = require('http-status-codes');

const KB = 1024;
const MB = KB*KB;
const EMOTIONLIST = _.keys(User.schema.obj.emotionCount.default);

/*
---------------------------------------------------------
 Pre-processing Token Check
---------------------------------------------------------
*/

router.use(/.*\/self\/.*|^\/users\/self$|^\/entries\/\w*\/comments$|^\/comments\/\w*$|^\/users\/\w*\/(un)?follow$/,
	function(req, res, next) {
		let token = req.body.token || req.headers['x-access-token'];

		if (token) {
			jwt.verify(token, config.secret, function(err, decoded) {
				if (err) {
					return res.status(HttpStatus.FORBIDDEN).json({ success: false, message: 'Failed to authenticate token.' });
				}

				req.username = decoded.username;
				next();
			});
		} else {
			return res.status(HttpStatus.FORBIDDEN).send({
				success: false,
				message: 'No token provided.'
			});
		}
});

/*
---------------------------------------------------------
User Information
---------------------------------------------------------
*/

var upload = multer({ storage: multer.memoryStorage({}) });

router.post('/users/self/picture', upload.single('profilePicture'), (req, res) => {

  let username = req.username;

  if (!req.file) {
  	return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'Did not attach image' });
  }

	if (req.file.size > 16*MB) {
		return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'File too large' });
	}

	switch (req.file.mimetype) {
		case 'image/bpm':
		case 'image/gif':
		case 'image/jpeg':
		case 'image/png':
		case 'image/tiff':
			break;
		default:
			return res.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).json({ success: false, message: 'File type not supported' });
	}

	gm(req.file.buffer, 'thumbnail.jpg')
		.resize(200, 250)
		.toBuffer('JPEG',function (err, thumbnailBuffer) {

		  if (err || !thumbnailBuffer) {
				return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'Cannot create thumbnail'});
		  }

			User.findOne({
			  username: username
			}, function(err, user) {
				if (err || !user) {
					return res.status(HttpStatus.BAD_REQUEST).json({success: false, message: 'User not found'});
				}

				user.profilePicture = {data: req.file.buffer, contentType: req.file.mimetype};
				user.thumbnail = {data: thumbnailBuffer, contentType: 'image/jpeg'};

				user.save()
			    .then(function (doc) {
			      return res.status(HttpStatus.CREATED).json({ success: true });
			    })
			    .catch(function(err) {
			      return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
			    });
			});
		});
});

router.put('/users/self', (req, res) => {
	let username = req.username;
	let oldPassword = req.body.oldPassword;
	let newPassword = req.body.newPassword;
	let newDisplayName = req.body.newDisplayName;

	if (!newPassword && !newDisplayName) {
		return res.status(HttpStatus.BAD_REQUEST).send({ success: false , message: 'Malformed request'});
	}

  User.findOne({
    username: username,
    password: oldPassword
  }, function(err, user) {
		if (err || !user) {
			return res.status(HttpStatus.UNAUTHORIZED).send({ success: false , message: 'Error: Incorrect password'});
		}

		let changedParameters = '';

		if (newPassword) {
			user.password = newPassword;
			changedParameters += 'password ';
		}

		if (newDisplayName) {
			user.displayName = newDisplayName;
			changedParameters += 'displayName';
		}

		user.save()
	    .then(function (doc) {
	      return res.status(HttpStatus.OK).json({ success: true, message: "Changed user information: " + changedParameters});
	    })
	    .catch(function(err) {
	      return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: "Error saving changes"});
	    });
	});
});


/*
---------------------------------------------------------
Journal Entries
---------------------------------------------------------
*/

router.post('/users/self/captures', upload.single('image'), function(req, res) {
	let username = req.username;
	let text = req.body.text;
	let captureDate = req.body.captureDate;
	let dominantEmotion = req.body.emotion;
	let file = req.file;

	let newCapture = {text: text, emotion: dominantEmotion};

	if (file) {
		if (file.size > 16*MB) {
			return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'File too large' });
		}

		switch (file.mimetype) {
			case 'image/bpm':
			case 'image/gif':
			case 'image/jpeg':
			case 'image/png':
			case 'image/tiff':
				break;
			default:
				return res.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).json({ success: false, message: 'File type not supported' });
		}

		newCapture.image = {data: req.file.buffer, contentType: req.file.mimetype};

		gm(req.file.buffer, 'thumbnail.jpg')
			.resize(200, 250)
			.toBuffer('JPEG',function (err, thumbnailBuffer) {
				if (err || !thumbnailBuffer) {
					return status(HttpStatus.BAD_REQUEST).json({success: false, message: 'Could not make thumbnail'});
				}
				newCapture.thumbnail = {data: thumbnailBuffer, contentType: 'image/jpeg'};
				makeEntry(req, res, newCapture);
			});
	} else {
		makeEntry(req, res, newCapture);
	}
});

function makeEntry(req, res, newCapture) {
	let username = req.username;
	let text = req.body.text;
	let captureDate = req.body.captureDate;
	let dominantEmotion = req.body.emotion || '';

	let date = captureDate.split('/');

	if (date[0][0] == "0") {
		date[0] = date[0][1];
	}
	if (date[1][0] == "0") {
		date[1] = date[1][1];
	}

	captureDate = date.join('/');

  JournalEntry.findOne({
		username: username,
		entryDate: captureDate
  }, function(err, entry) {
		if (entry) {
			entry.captures.push(newCapture);

			entry.save()
				.then(function(doc) {
					incrementEmotionCount(username, dominantEmotion);
					notifyFollowers(username, entry._id);
					return res.status(HttpStatus.CREATED).json({ success: true, message: 'Added to existing date'});
				})
				.catch(function(err) {
					console.log(err);
					return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
			  });
		} else {

			let newEntry = new JournalEntry({
			  username: username,
			  entryDate: captureDate,
			  captures: [newCapture]
			});

			newEntry.save()
				.then(function (createdEntry) {
					incrementEmotionCount(username, dominantEmotion);
					notifyFollowers(username, createdEntry._id);
					return res.status(HttpStatus.CREATED).json({ success: true, message: 'Created new journal entry'});
			  })
			  .catch(function(err) {
					console.log(err);
					return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
			  });
		}
  });
}

function notifyFollowers(username, entryId) {
	User.findOne({
		username: username
	}, 'followers', function(err, userInfo) {
		if (err || !userInfo) {
			console.log("error notifying followers");
			return;
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
		});
	});
}

function incrementEmotionCount(username, emotion, amount=1) {

	if (!_.includes(EMOTIONLIST, emotion)) {
		return;
	}

	let incrementable = {};
	let key = 'emotionCount.' + emotion;
	incrementable[key] = amount;

	User.findOneAndUpdate({
		username: username
	}, {$inc: incrementable }, function(err, data) {
		if (err) {
			console.log(err);
		}
	});
}

router.delete('/users/self/entries/:entryId', function(req, res) {
	let username = req.username;
	let entryId = req.params.entryId;

	JournalEntry.findById(entryId, function(err, entry) {
		if (err || !entry) {
      return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'Entry not found'});
    }

    entry.remove()
      .then(function () {
				_.forEach(entry.captures, function(capture) {
					incrementEmotionCount(username, capture.emotion, -1);
				});
        return res.status(HttpStatus.OK).json({ success: true});
      })
      .catch(function(err) {
        return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
      });
    });
});

router.delete('/users/self/captures/:captureId', function(req, res) {
	let username = req.username;
	let captureId = req.params.captureId;

	if (!mongoose.Types.ObjectId.isValid(captureId)) {
		return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
	}

	JournalEntry.findOne({
		"captures._id": mongoose.Types.ObjectId(captureId)
	}, function(err, entry) {
		if (err || !entry) {
			return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
		}

		if (entry.captures.length === 1) {
			entry.remove()
				.then(function () {
					findCaptureByIdAndDecrementEmotion(username, entry, captureId);
					return res.status(HttpStatus.OK).json({ success: true, message: "Deleted entire entry"});
				})
				.catch(function(err) {
					return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
				});
		} else {
			entry.update({'$pull': {'captures': {'_id': mongoose.Types.ObjectId(captureId)}}})
				.then(function() {
					findCaptureByIdAndDecrementEmotion(username, entry, captureId);
					return res.status(HttpStatus.OK).json({ success: true });
				})
				.catch(function(err) {
					return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
				});
			}
	});
});

function findCaptureByIdAndDecrementEmotion(username, entry, captureId) {
	_.forEach(entry.captures, function(capture) {
		if (capture._id == captureId) {
			incrementEmotionCount(username, capture.emotion, -1);
		}
	});
}

router.put('/users/self/entries/:entryId', function(req, res) {
	let entryId = req.params.entryId;
	let title = req.body.title;

	JournalEntry.findByIdAndUpdate(entryId, {
		$set: { title: title }
	}, function (err, entry) {
		if (err) {
			return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
		}

		return res.status(HttpStatus.OK).json({ success: true });
	});
});

router.put('/users/self/captures/:captureId', function(req, res) {
	let captureId = req.params.captureId;
	let text = req.body.text;
	let username = req.username;

	if (!mongoose.Types.ObjectId.isValid(captureId)) {
		return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
	}

	JournalEntry.findOneAndUpdate({
		"username": username,
		"captures._id": mongoose.Types.ObjectId(captureId)
	}, {
		"captures.$.text": text
	}, function(err, entry) {
		if (err || !entry) {
			return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
		}

		return res.status(HttpStatus.OK).json({ success: true });
	});
});

/*
---------------------------------------------------------
Comments
---------------------------------------------------------
*/

router.post('/entries/:entryId/comments', function(req, res) {
	let commenter = req.username;
	let text = req.body.text;
	let entryId = req.params.entryId;

	let newComment = {commenter: commenter, text: text};

  JournalEntry.findById(entryId, function(err, entry) {
		if (err || !entry) {
			return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
		}

		entry.comments.push(newComment);

		entry.save()
		  .then(function (doc) {
		    return res.status(HttpStatus.CREATED).json({ success: true, message: 'Comment created'});
		  })
		  .catch(function(err) {
		    return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
		  });
	});
});

router.delete('/comments/:commentId', function(req, res) {
	let commenter = req.username;
	let commentId = req.params.commentId;

	if (!mongoose.Types.ObjectId.isValid(commentId)) {
		return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
	}

  JournalEntry.findOne({
		"comments._id": mongoose.Types.ObjectId(commentId),
		"comments.commenter": commenter
  }, function(err, entry) {
		if (err || !entry) {
			return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
		}

		entry.update({'$pull': {'comments': {'_id': mongoose.Types.ObjectId(commentId)}}})
			.then(function() {
				return res.status(HttpStatus.OK).json({ success: true });
			})
			.catch(function(err) {
				return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
			});
	});
});

/*
---------------------------------------------------------
Following
---------------------------------------------------------
*/

router.post('/users/:username/follow', function(req, res) {
	let toFollowUsername = req.params.username;
	let currentUsername = req.username;

	User.findOne({
	  username: currentUsername
	}, function(err, currentUser) {
		if (err || !currentUser) {
			return res.status(HttpStatus.BAD_REQUEST).json({success: false});
		}

		User.findOne({
		  username: toFollowUsername
		}, function(err, userToFollow) {
			if (err || !userToFollow) {
				return res.status(HttpStatus.BAD_REQUEST).json({success: false, message: 'User not found'});
			}

			currentUser.following.push(toFollowUsername);
			userToFollow.followers.push(currentUsername);

			currentUser.save()
				.then(function(doc) {
					userToFollow.save();
				})
				.then(function (doc) {
					return res.status(HttpStatus.OK).json({ success: true });
			  })
			  .catch(function(err) {
					return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
			  });
		});
	});
});

router.post('/users/:username/unfollow', function(req, res) {
	let toUnfollowUsername = req.params.username;
	let currentUsername = req.username;

	User.findOneAndUpdate({
		username: currentUsername
	}, {$pull: {following: toUnfollowUsername}}, function(err, data){
    if (err) {
      return res.status(HttpStatus.BAD_REQUEST).json({success: false});
    }

    User.findOneAndUpdate({
			username: toUnfollowUsername
		}, {$pull: {followers: currentUsername}}, function(err, data){
	    if (err) {
	      return res.status(HttpStatus.BAD_REQUEST).json({success: false});
	    }

	    return res.status(HttpStatus.OK).json({ success: true });
	  });
  });
});

/*
---------------------------------------------------------
 Notifications
---------------------------------------------------------
*/

router.get('/users/self/notifications', function(req, res) {
	let username = req.username;

	User.findOne({
	  username: username
	}, 'notifications', function(err, userInfo) {

		if (err || !userInfo) {
			return res.status(HttpStatus.BAD_REQUEST).json({success: false, message: 'Notifications not found'});
		}

		JournalEntry.find({
			'_id': { $in: userInfo.notifications}
		}, {
			'captures.image': 0,
			'captures.thumbnail': 0
		}, function(err, docs){
			if (err || !docs) {
				return res.status(HttpStatus.BAD_REQUEST).json({success: false, message: 'Server error'});
			}

			return res.status(HttpStatus.OK).json(docs.reverse());
		});
	});
});

/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router;
