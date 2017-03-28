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
var text;
var captureDate;

describe('Following and Notifications', function() {
	this.timeout(7000);

  before('Create two users', (done) => {
    usernameA = 'automatedtest' + randomstring.generate(7);
    usernameB = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		text = 'anytext';
		captureDate = '1/3/2000';

		TestUtils.createUser(usernameA, password)
			.then(function(returnedtokenA) {
				tokenA = returnedtokenA;
				return TestUtils.createUser(usernameB, password);
			})
			.then(function(returnedtokenB){
				tokenB = returnedtokenB;
			})
			.then(TestUtils.DelayPromise(2000))
			.then(function(){
				done();
		});
	});

	it('should have user A follow user B', (done) => {
		chai.request(server)
			.post('/users/'+ usernameB +'/follow')
			.set({'x-access-token': tokenA})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				done();
			});
	});

	it('should have user B on user As following list', (done) => {
		chai.request(server)
			.get('/users/' + usernameA)
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.have.property('following').and.be.an('array');
				expect(res.body.following).to.include(usernameB);
				done();
			});
	});

	it('should have user A on user Bs followers list', (done) => {
		chai.request(server)
			.get('/users/' + usernameB)
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.have.property('followers').and.be.an('array');
				expect(res.body.followers).to.include(usernameA);
				done();
			});
	});

	it('should create a text capture for user B and have it appear on user As notifications', (done) => {
		TestUtils.createCapture(tokenB, text, captureDate)
			.then(TestUtils.DelayPromise(2000))
			.then(function() {
				chai.request(server)
					.get('/users/self/notifications')
					.set({'x-access-token': tokenA})
						.end((err, res) => {
							expect(res).to.have.status(HttpStatus.OK);
							expect(res.body).to.be.an('array');
							expect(res.body).to.have.lengthOf(1);
							expect(res.body[0].username).to.equal(usernameB);
							expect(res.body[0].entryDate).to.equal(captureDate);
							done();
						});
			});
	});
});



