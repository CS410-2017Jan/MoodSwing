var mongoose = require('mongoose')
var Schema = mongoose.Schema

mongoose.Promise = Promise

module.exports = mongoose.model('User', new Schema({
	username: String,
	password: String,
	displayName: String
}, {
	versionKey: '1'
}))