const express = require('express')
const app = express()
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const config = require('./config')

/*
---------------------------------------------------------
 Test public /public/hello
---------------------------------------------------------
*/

router.get('/hello', (req, res) => {
  res.send('Hello World')
})


/*
---------------------------------------------------------
 Authentication
---------------------------------------------------------
*/

router.post('/users', function(req, res) {
  let username = req.body.username
  let password = req.body.password

  let newUser = new User({
    username: username,
    password: password
  })

  newUser.save()
		.then(function (doc) {
			console.log(doc)
			res.json({ success: true })
		})
		.catch(function(err) {
			res.json({ success: false })
		})
})

// http header should be Authentication: Bearer <token>
router.post('/users/login', function(req, res) {

  User.findOne({
    username: req.body.username
  }, function(err, user) {
    if (err) throw err

    if (!user) {
      res.json({ success: false, message: 'Error: User not found.' })
    } else if (user) {

      if (user.password != req.body.password) {
        res.json({ success: false, message: 'Error: Wrong password.' })
      } else {
        var token = jwt.sign(user, config.secret, {
          expiresIn: 86400  // 24 hours
        })

        res.json({
          success: true,
          message: 'Enjoy your token!',
          token: token
        })
      }
    }
  })
})


 /*
---------------------------------------------------------
 Module export
---------------------------------------------------------
 */

 module.exports = router