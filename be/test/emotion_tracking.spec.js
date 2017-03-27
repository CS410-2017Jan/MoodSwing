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

var newUsername;
var password;
var token;
var text;
var captureDate1;
var captureDate2;
var emotion;

describe('Increment and Decrement Emotion Count', () => {
  before('make an account', (done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		text = 'anytext';
		captureDate1 = '1/1/2000';
		captureDate2 = '2/1/2000';
		emotion = 'RAGE';

		TestUtils.createUser(newUsername, password).then(function(returnedToken) {
			token = returnedToken;
			done();
		})
	});

	it('should create two text captures with the same emotion', (done) => {
		TestUtils.createCapture(token, text, captureDate1, emotion)
			.then(function() {
				return TestUtils.createCapture(token, text, captureDate2, emotion);
			})
			.then(function(){
				done();
			});
	});

	it('should increment the emotion count properly', (done) => {
		chai.request(server)
			.get('/users/' + newUsername)
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body.sortedEmotions).to.be.an('array');
				expect(res.body.sortedEmotions[0]).to.be.an('array');
				expect(res.body.sortedEmotions[0][0]).to.equal(emotion);
				expect(res.body.sortedEmotions[0][1]).to.equal('2');
				done();
			});
	});

	it('should decrement the emotion count on delete', (done) => {
		chai.request(server)
			.get('/users/' + newUsername + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				let captureId = res.body[0].captures[0]['_id'];

				chai.request(server)
					.delete('/users/self/captures/' + captureId)
					.set({'x-access-token': token})
					.end((err, res) => {
						expect(res).to.have.status(HttpStatus.OK);
						chai.request(server)
							.get('/users/' + newUsername)
							.end((err, res) => {
								expect(res).to.have.status(HttpStatus.OK);
								expect(res.body.sortedEmotions[0][0]).to.equal(emotion);
								expect(res.body.sortedEmotions[0][1]).to.equal('1');
								done();
						})
					});
			});
	});
});



