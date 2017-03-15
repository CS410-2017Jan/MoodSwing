var mongoose = require('mongoose')
var Schema = mongoose.Schema

mongoose.Promise = Promise

module.exports = mongoose.model('Capture', new Schema({
	_id: {type: ObjectId, required: true},
	text: String,
	image: { data: Buffer, contentType: String }
}, {
	versionKey: '1'
}))