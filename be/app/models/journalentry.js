var mongoose = require('mongoose')
var Schema = mongoose.Schema
const Capture   = require('./journalentry')

mongoose.Promise = Promise

module.exports = mongoose.model('JournalEntry', new Schema({
	username: String,
	entryDate: String,
	dateCreated: { type: Date, default: Date.now },
	title: {type: String, default: ""},
	captures: [Capture]
}, {
	versionKey: '1'
}))