var mongoose = require('mongoose')
var Schema = mongoose.Schema

mongoose.Promise = Promise

module.exports = mongoose.model('Capture', new Schema({
	text: String
}, {
	versionKey: '1'
}))