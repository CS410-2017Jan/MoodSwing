var mongoose = require('mongoose');
var Schema = mongoose.Schema;

mongoose.Promise = Promise;

let emotionCount = {
	'RELAXED': 0,
	'SMILEY': 0,
	'LAUGHING': 0,
	'WINK': 0,
	'SMIRK': 0,
	'KISSING': 0,
	'STUCK_OUT_TONGUE': 0,
	'STUCK_OUT_TONGUE_WINKING_EYE': 0,
	'DISAPPOINTED': 0,
	'RAGE': 0,
	'SCREAM': 0,
	'FLUSHED': 0
};

module.exports = mongoose.model('User', new Schema({
	username: String,
	password: String,
	displayName: String,
	following: [String],
	followers: [String],
	notifications: [String],
	emotionCount: {type: Schema.Types.Mixed, default: emotionCount},
	profilePicture: { data: Buffer, contentType: String },
	thumbnail: { data: Buffer, contentType: String }
}, {
	versionKey: '1'
}));