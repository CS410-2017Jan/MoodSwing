const express = require('express')
const app = express()
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const JournalEntry   = require('./app/models/journalentry')
const config = require('./config')
const _ = require('lodash')

/*
---------------------------------------------------------
 Test public /hello
---------------------------------------------------------
*/

router.get('/hello', (req, res) => {
  res.status(200).send('Hello World')
})

/*
---------------------------------------------------------
 Authentication
---------------------------------------------------------
*/

router.post('/users', function(req, res) {
  let username = req.body.username
  let password = req.body.password
  let displayName = req.body.displayName || ''

  if (!username || !password) {
    return res.status(400).json({ success: false, message: 'Malformed http body'})
  }

  User.findOne({
    username: req.body.username
  }, function(err, user) {
    if (err) throw err

    if (user) {
      return res.status(409).json({ success: false, message: 'Username already exists'})
    }

    let newUser = new User({
      username: username,
      password: password,
      displayName: displayName
    })

    newUser.save()
      .then(function (doc) {
        let token = createToken(doc)
        return res.status(201).json({ success: true, token: token})
      })
      .catch(function(err) {
        return res.status(400).json({ success: false })
      })
  })
})

router.post('/users/login', function(req, res) {

  User.findOne({
    username: req.body.username
  }, function(err, user) {
    if (err) throw err

    if (!user) {
      return res.status(400).json({ success: false, message: 'Error: User not found.' })
    }

    if (user.password != req.body.password) {
      return res.status(400).json({ success: false, message: 'Error: Wrong password.' })
    }

    let token = createToken(user)

    return res.status(200).json({
      success: true,
      message: 'Enjoy your token!',
      token: token
    })
  })
})

function createToken(user) {
  return jwt.sign({username: user.username}, config.secret, {
    expiresIn: 86400  // 24 hours
  })
}

/*
---------------------------------------------------------
User information
---------------------------------------------------------
 */

router.get('/users/:username', function(req, res) {
  let username = req.params.username

  User.findOne({
    username: username
  }, '_id username displayName followers following', //TODO: Return profile picture if FE wants it
  function(err, userInfo) {
    if (!userInfo || err) {
      return res.status(400).json({success: false, message: 'User not found'})
    }

    return res.status(200).json(userInfo)
  })
})


 /*
---------------------------------------------------------
Captures
---------------------------------------------------------
 */

router.get('/users/:username/entries', function(req, res) {
  let username = req.params.username

  JournalEntry.find({
    username: username
  }, function(err, journalEntries) {
    if (err) throw err

    if (!journalEntries) {
      return res.status(400).json({ success: false, message: 'No entries found'})
    }

    let sortedEntries = journalEntries.sort(function(a, b){
      a = a.entryDate.split('/');
      b = b.entryDate.split('/');
      return a[2] - b[2] || a[1] - b[1] || a[0] - b[0];
    }).reverse();

    return res.status(200).json(sortedEntries)
  })
})

router.get('/users', function(req, res) {
  User.find({}, {'username': 1, 'displayName': 1, '_id': 0}, function(err, results) {
    let userList = results.sort(function(user1, user2) {
      if (user1.username.toLowerCase() < user2.username.toLowerCase())
        return -1;
      if (user1.username.toLowerCase() > user2.username.toLowerCase())
        return 1;
      return 0;
    })
    return res.status(200).json(userList)
  })
})


 /*
---------------------------------------------------------
 Module export
---------------------------------------------------------
 */

 module.exports = router