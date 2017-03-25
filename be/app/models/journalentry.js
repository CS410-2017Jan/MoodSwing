var mongoose = require('mongoose')
var Schema = mongoose.Schema

mongoose.Promise = Promise

var Comment = new Schema({
	dateCreated: { type: Date, default: Date.now },
	commenter: String,
	text: String
}, {
	versionKey: '1'
})

var Capture = new Schema({
	text: String,
	image: { data: Buffer, contentType: String },
	thumbnail: { data: Buffer, contentType: String },
	emotion: String
}, {
	versionKey: '1'
})

var JournalEntry = new Schema({
	username: String,
	entryDate: String,
	dateCreated: { type: Date, default: Date.now },
	title: {type: String, default: ""},
	captures: [Capture],
	comments: [Comment]
}, {
	versionKey: '1'
})

module.exports = mongoose.model('JournalEntry', JournalEntry)