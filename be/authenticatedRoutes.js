const express = require('express')
const router = express.Router()
const jwt = require('jsonwebtoken')
const User   = require('./app/models/user')
const config = require('./config')

/*
---------------------------------------------------------
 Pre-processing Token Check
---------------------------------------------------------
*/

router.use(function(req, res, next) {
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
router.get('/hi', function(req, res) {
	console.log(req.user) //how you get user information
	res.json({ message: 'Welcome to the coolest API on earth!' })
})

router.get('/users', function(req, res) {
	User.find({}, function(err, users) {
		res.json(users)
	})
})

router.get('/check', function(req, res) {
	res.json(req.decoded)
})


/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router



