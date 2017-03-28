const chai = require('chai');
const chaiHttp = require('chai-http');
const server = require('../server');
const User = require('../app/models/user');
const expect = chai.expect;
const randomstring = require("randomstring");
const HttpStatus = require('http-status-codes');
const TestUtils = require('./TestUtils');
const _ = require('lodash');

chai.use(chaiHttp);

var usernameA;
var usernameB;
var password;
var tokenA;
var tokenB;
var entryText;
var commentText;
var captureDate;
var entryId;
var commentId;

describe('Comment on a journal entry', function() {
	this.timeout(7000);

  before('Create two users', (done) => {
    usernameA = 'automatedtest' + randomstring.generate(7);
    usernameB = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		entryText = 'anytext';
		commentText = 'anycomment';
		captureDate = '1/3/2000';

		TestUtils.createUser(usernameA, password)
			.then(function(returnedtokenA) {
				tokenA = returnedtokenA;
				return TestUtils.createUser(usernameB, password);
			})
			.then(function(returnedtokenB){
				tokenB = returnedtokenB;
				return TestUtils.createCapture(tokenA, entryText, captureDate);
			})
			.then(TestUtils.DelayPromise(2000))
			.then(function(){
				chai.request(server)
					.get('/users/' + usernameA + '/entries')
					.end((err, res) => {
						expect(res).to.have.status(HttpStatus.OK);
						expect(res.body).to.be.an('array');
						expect(res.body[0]['_id']).to.be.a('string');
						entryId = res.body[0]['_id'];
						done();
					});
			})
	});

	it('should let user B comment on user As entry', (done) => {
		console.log(tokenB);
		chai.request(server)
			.post('/entries/' + entryId + '/comments')
			.set({'x-access-token': tokenB})
			.send({text: commentText})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				done();
			});
	});

	it('should see user Bs comment on user As entry', (done) => {
		chai.request(server)
			.get('/users/' + usernameA + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body[0].comments).to.be.an('array');
				expect(res.body[0].comments[0]).to.have.property('commenter').and.equal(usernameB);
				expect(res.body[0].comments[0]).to.have.property('text').and.equal(commentText);
				expect(res.body[0].comments[0]).to.have.property('_id');
				commentId = res.body[0].comments[0]['_id'];
				done();
			});
	});

	it('should delete comment', (done) => {
		chai.request(server)
			.delete('/comments/' + commentId)
			.set({'x-access-token': tokenB})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				done();
			});
	});

	it('should see no comments on user As entry', (done) => {
		chai.request(server)
			.get('/users/' + usernameA + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body[0].comments).to.be.an('array');
				expect(res.body[0].comments).to.have.lengthOf(0);
				done();
			});
	});
});


