const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User   = require('./app/models/user');
const JournalEntry   = require('./app/models/journalentry');
const config = require('./config');
const mongoose = require('mongoose');
const _ = require('lodash');
const HttpStatus = require('http-status-codes');

/*
---------------------------------------------------------
 Authentication
---------------------------------------------------------
*/

router.post('/users', function(req, res) {
  let username = req.body.username;
  let password = req.body.password;
  let displayName = req.body.displayName || '';

  if (!username || !password) {
    return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'Malformed http body'});
  }

  User.findOne({
    username: req.body.username
  }, function(err, user) {
    if (err || user) {
      return res.status(HttpStatus.CONFLICT).json({ success: false, message: 'Username already exists'});
    }

    let newUser = new User({
      username: username,
      password: password,
      displayName: displayName
    });

    newUser.save()
      .then(function (doc) {
        let token = createToken(doc);
        return res.status(HttpStatus.CREATED).json({ success: true, token: token});
      })
      .catch(function(err) {
        return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
      });
  });
});

router.post('/users/login', function(req, res) {

  User.findOne({
    username: req.body.username
  }, function(err, user) {
    if (err || !user) {
      return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: 'Error: User not found.' });
    }

    if (user.password != req.body.password) {
      return res.status(HttpStatus.UNAUTHORIZED).json({ success: false, message: 'Error: Wrong password.' });
    }

    let token = createToken(user);

    return res.status(HttpStatus.OK).json({
      success: true,
      message: 'Enjoy your token!',
      token: token
    });
  });
});

function createToken(user) {
  return jwt.sign({username: user.username}, config.secret, {
    expiresIn: 86400  // 24 hours
  });
}

router.get('/users/:username/picture', (req, res) => {

  let username = req.params.username;

  User.findOne({
    username: username
  }, 'profilePicture', function(err, user) {

    if (err || !user) {
      return res.status(HttpStatus.NOT_FOUND).send({ success: false });
    }

    if (!user.profilePicture.data) {
      res.writeHead(HttpStatus.OK, {
        'Content-Type': "image/jpeg",
        'Content-Length': 0
      });
      return res.end();
    }

    let imageBuffer = user.profilePicture.data;
    let imageType = user.profilePicture.contentType;

    let img = new Buffer(imageBuffer, 'base64');
    res.writeHead(HttpStatus.OK, {
      'Content-Type': imageType,
      'Content-Length': img.length
    });
    res.status(HttpStatus.OK).end(img);
  });
});

router.get('/users/:username/thumbnail', (req, res) => {

  let username = req.params.username;

  User.findOne({
    username: username
  }, 'thumbnail' ,function(err, user) {

    if (err || !user) {
      return res.status(HttpStatus.NOT_FOUND).send({ success: false });
    }

    if (!user.thumbnail.data) {
      res.writeHead(HttpStatus.OK, {
        'Content-Type': "image/jpeg",
        'Content-Length': 0
      });
      return res.end();
    }

    let imageBuffer = user.thumbnail.data;
    let imageType = user.thumbnail.contentType;

    let img = new Buffer(imageBuffer, 'base64');
    res.writeHead(HttpStatus.OK, {
      'Content-Type': imageType,
      'Content-Length': img.length
    });
    res.status(HttpStatus.OK).end(img);
  });
});

/*
---------------------------------------------------------
User information
---------------------------------------------------------
 */

router.get('/users/:username', function(req, res) {
  let username = req.params.username;

  User.findOne({
    username: username
  }, '_id username displayName followers following emotionCount',
  function(err, userInfo) {
    if (!userInfo || err) {
      return res.status(HttpStatus.NOT_FOUND).json({success: false, message: 'User not found'});
    }

    userInfo = userInfo.toObject();

    if (userInfo.emotionCount && _.keys(userInfo.emotionCount).length > 1) {
      userInfo.sortedEmotions = _.toPairs(userInfo.emotionCount).sort(function(a, b) {
        return b[1] - a[1];
      }).slice(0,2);

      userInfo.sortedEmotions[0][1] = '' + userInfo.sortedEmotions[0][1];
      userInfo.sortedEmotions[1][1] = '' + userInfo.sortedEmotions[1][1];
    }

    userInfo.emotionCount = undefined;

    return res.status(HttpStatus.OK).json(userInfo);
  });
});


 /*
---------------------------------------------------------
Captures
---------------------------------------------------------
 */

router.get('/users/:username/entries', function(req, res) {
  let username = req.params.username;

  JournalEntry.find({
    username: username
  }, {
    'captures.image': 0,
    'captures.thumbnail': 0
  }, function(err, journalEntries) {
    if (err || !journalEntries) {
      return res.status(HttpStatus.OK).json([]);
    }

    let sortedEntries = journalEntries.sort(function(a, b){
      a = a.entryDate.split('/');
      b = b.entryDate.split('/');
      return a[2] - b[2] || a[1] - b[1] || a[0] - b[0];
    }).reverse();

    return res.status(HttpStatus.OK).json(sortedEntries);
  });
});

router.get('/captures/:captureId/image', (req, res) => {

  let captureId = req.params.captureId;

  JournalEntry.findOne({
    "captures._id": mongoose.Types.ObjectId(captureId)
  }, {
    'captures.$': 1
  }, function(err, entry) {
    if (err || !entry) {
      return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
    }

    if (!entry.captures[0] || !entry.captures[0].image || !entry.captures[0].image.data) {
      res.writeHead(HttpStatus.OK, {
        'Content-Type': "image/jpeg",
        'Content-Length': 0
      });
      return res.end();
    }

    let capture = entry.captures[0];
    let imageBuffer = capture.image.data;
    let imageType = capture.image.contentType;

    let img = new Buffer(imageBuffer, 'base64');
    res.writeHead(HttpStatus.OK, {
      'Content-Type': imageType,
      'Content-Length': img.length
    });
    res.status(HttpStatus.OK).end(img);
  });
});

router.get('/captures/:captureId/thumbnail', (req, res) => {

  let captureId = req.params.captureId;

  JournalEntry.findOne({
    "captures._id": mongoose.Types.ObjectId(captureId)
  }, {
    'captures.$': 1
  }, function(err, entry) {
    if (err || !entry) {
      return res.status(HttpStatus.BAD_REQUEST).json({ success: false });
    }

    if (!entry.captures[0] || !entry.captures[0].thumbnail || !entry.captures[0].thumbnail.data) {
      res.writeHead(HttpStatus.OK, {
        'Content-Type': "image/jpeg",
        'Content-Length': 0
      });
      return res.end();
    }

    let capture = entry.captures[0];
    let imageBuffer = capture.thumbnail.data;
    let imageType = capture.thumbnail.contentType;

    let img = new Buffer(imageBuffer, 'base64');
    res.writeHead(HttpStatus.OK, {
      'Content-Type': imageType,
      'Content-Length': img.length
    });
    res.status(HttpStatus.OK).end(img);
  });
});

router.get('/users', function(req, res) {
  User.find({}, {'username': 1, 'displayName': 1, '_id': 0}, function(err, results) {
    let userList = results.sort(function(user1, user2) {
      if (user1.username.toLowerCase() < user2.username.toLowerCase())
        return -1;
      if (user1.username.toLowerCase() > user2.username.toLowerCase())
        return 1;
      return 0;
    });
    return res.status(HttpStatus.OK).json(userList);
  });
});

router.get('/entries/:entryId', function(req, res) {
  let entryId = req.params.entryId;
  JournalEntry.findById(entryId, {"captures.image": 0}, function(err, entry) {
    entry = entry.toObject();

    if (err || !entry) {
      return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: "No entry found"});
    }

    let commenters = _.map(entry.comments, 'commenter');

    User.find({
      username: { $in: commenters }
      }, 'username displayName', function(err, commentersUserInfo) {
        if (err || !commentersUserInfo) {
          return res.status(HttpStatus.BAD_REQUEST).json({ success: false, message: "Commenter error"});
        }

        let usernameDisplayNameHash = {};
        _.forEach(commentersUserInfo, function(commenterUserInfo) {
          usernameDisplayNameHash[commenterUserInfo.username] = commenterUserInfo.displayName;
        });

        _.forEach(entry.comments, function(comment) {
          comment.displayName = usernameDisplayNameHash[comment.commenter];
        });

        return res.status(HttpStatus.OK).json(entry);
      }
    );
  });
});


 /*
---------------------------------------------------------
 Module export
---------------------------------------------------------
 */

 module.exports = router;