const express = require('express')
const app = express()
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const JournalEntry   = require('./app/models/journalentry')
const config = require('./config')

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
Captures
---------------------------------------------------------
 */

router.get('/users/:username/captures', function(req, res) {
  let username = req.params.username

  JournalEntry.find({
    username: username
  }, function(err, journalEntries) {
    if (err) throw err

    if (!journalEntries) {
      return res.status(400).json({ success: false, message: 'No entries found'})
    }

    let sortedEntries = journalEntries.sort(function(a, b) {
      let aDate = new Date(a.entryDate)
      let bDate = new Date(b.entryDate)
      return aDate < bDate
    })

    return res.status(200).json(sortedEntries)
  })
})


 /*
---------------------------------------------------------
 Module export
---------------------------------------------------------
 */

 module.exports = router