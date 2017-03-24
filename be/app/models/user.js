var mongoose = require('mongoose')
var Schema = mongoose.Schema

mongoose.Promise = Promise

module.exports = mongoose.model('User', new Schema({
	username: String,
	password: String,
	displayName: String,
	following: [String],
	followers: [String],
	notifications: [String],
	profilePicture: { data: Buffer, contentType: String },
	thumbnail: { data: Buffer, contentType: String }
}, {
	versionKey: '1'
}))