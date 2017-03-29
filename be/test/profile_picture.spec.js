const chai = require('chai');
const chaiHttp = require('chai-http');
const server = require('../server');
const User = require('../app/models/user');
const expect = chai.expect;
const randomstring = require("randomstring");
const HttpStatus = require('http-status-codes');
const _ = require('lodash');
const TestUtils = require('./TestUtils');
const fs = require('fs');
const supertest = require('supertest');
const request = supertest('localhost:3000');

chai.use(chaiHttp);

var newUsername;
var password;
var token;

describe('Profile picture', function() {
	this.timeout(10000);

  before((done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';

		TestUtils.createUser(newUsername, password).then(function(returnedToken) {
			token = returnedToken;
			done();
		})
	});

	it('should not set a profile picture without attaching image', (done) => {
		request.post('/users/self/picture')
			.set({'x-access-token': token})
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.BAD_REQUEST);
				done();
		});
	});

	it('should set a profile picture', (done) => {
		request.post('/users/self/picture')
			.set({'x-access-token': token})
			.attach('profilePicture', __dirname + '/test_files/fox.png')
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.CREATED);
				done();
		});
	});

	it('should get the same profile picture', (done) => {
		request.get('/users/' + newUsername +'/picture')
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.headers['content-type']).to.equal('image/png');
				expect(res.headers['content-length']).to.equal('196988');
				done();
		});
	});

	it('should get a thumbnail of the profile picture smaller than half of its original size', (done) => {
		request.get('/users/' + newUsername +'/thumbnail')
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.headers['content-type']).to.equal('image/jpeg');
				expect(parseInt(res.headers['content-length'])).to.be.below(196988/2);
				done();
		});
	});
});





