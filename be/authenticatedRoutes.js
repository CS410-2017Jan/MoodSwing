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

router.get('/self/test', function(req, res) {
	User.find({}, function(err, users) {
		res.json(users)
	})
})



/*
---------------------------------------------------------
 Export Module
---------------------------------------------------------
*/

module.exports = router



