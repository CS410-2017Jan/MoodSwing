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
var text;
var captureDate;
var captureId;

describe('Profile picture', function() {
	this.timeout(10000);

  before((done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		text = 'anytext';
		captureDate = '3/3/2003';


		TestUtils.createUser(newUsername, password).then(function(returnedToken) {
			token = returnedToken;
			done();
		})
	});

	it('should allow the option of setting a picture with a capture', (done) => {
		request.post('/users/self/captures')
			.set({'x-access-token': token})
			.field('text', text)
			.field('captureDate', captureDate)
			.attach('image', __dirname + '/test_files/fox.png')
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.CREATED);
				done();
		});
	});

	it('should get the same picture by capture ID', (done) => {
		request.get('/users/' + newUsername +'/entries')
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body).to.have.lengthOf(1);
				expect(res.body[0].captures).to.be.an('array');
				expect(res.body[0].captures).to.have.lengthOf(1);
				captureId = res.body[0].captures[0]['_id'];

				request.get('/captures/' + captureId + '/image')
					.end(function(err, res) {
						expect(res).to.have.status(HttpStatus.OK);
						expect(res.headers['content-type']).to.equal('image/png');
						expect(res.headers['content-length']).to.equal('196988');
						done();
				});
		});
	});

	it('should get a thumbnail of the profile picture smaller than half of its original size', (done) => {
		request.get('/captures/' + captureId + '/thumbnail')
			.end(function(err, res) {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.headers['content-type']).to.equal('image/jpeg');
				expect(parseInt(res.headers['content-length'])).to.be.below(196988/2);
				done();
		});
	});
});





