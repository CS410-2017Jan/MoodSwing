const chai = require('chai');
const chaiHttp = require('chai-http');
const server = require('../server');
const User = require('../app/models/user');
const expect = chai.expect;
const randomstring = require("randomstring");
const HttpStatus = require('http-status-codes');
const _ = require('lodash');
const TestUtils = require('./TestUtils');

chai.use(chaiHttp);

var newUsername;
var password;
var token;
var text;
var captureDate1;
var captureDate2;

describe('One entry for multiple captures of same date', () => {
  before((done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		text = 'anytext';
		captureDate1 = '1/1/2000';
		captureDate2 = '2/1/2000';

		TestUtils.createUser(newUsername, password).then(function(returnedToken) {
			token = returnedToken;
			done();
		})
	});

	it('should create a text capture', (done) => {
		TestUtils.createCapture(token, text, captureDate1).then(function() {
			done();
		})
	});

	it('should create an entry for captures date', (done) => {
		chai.request(server)
			.get('/users/' + newUsername + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body).to.have.lengthOf(1);
				expect(res.body[0]).to.have.property('entryDate').and.equal(captureDate1);
				expect(res.body[0].captures).to.be.an('array');
				expect(res.body[0].captures).to.have.length(1);
				expect(res.body[0].captures[0]).to.have.property('text').and.equal(text);
				done();
			});
	});

	it('should create a second text capture with same date as first', (done) => {
		TestUtils.createCapture(token, text, captureDate1).then(function() {
			done();
		})
	});

	it('should add capture to entry with same date', (done) => {
		chai.request(server)
			.get('/users/' + newUsername + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body).to.have.lengthOf(1);
				expect(res.body[0]).to.have.property('entryDate').and.equal(captureDate1);
				expect(res.body[0].captures).to.be.an('array');
				expect(res.body[0].captures).to.have.lengthOf(2);
				done();
			});
	});

	it('should create a text capture with different date as first two', (done) => {
		TestUtils.createCapture(token, text, captureDate2).then(function() {
			done();
		})
	});

	it('should get entries sorted by date', (done) => {
		chai.request(server)
			.get('/users/' + newUsername + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body).to.have.lengthOf(2)
				expect(res.body[0]).to.have.property('entryDate').and.equal(captureDate2);
				expect(res.body[1]).to.have.property('entryDate').and.equal(captureDate1);
				done();
			});
	});
});

var entryId1;
var entryId2;
var captureId1;

describe('Deleting captures and entries', function() {
  this.timeout(15000);

  before('Make 2 entries with 2 captures each', (done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		text = 'anytext';
		captureDate1 = '1/1/2001';
		captureDate2 = '2/1/2001';

		TestUtils.createUser(newUsername, password)
			.then(function(returnedToken) {
				token = returnedToken;
				TestUtils.createCapture(token, text, captureDate1);
			})
			.then(function(){
				TestUtils.createCapture(token, text, captureDate2);
			})
			.then(function() {
				TestUtils.createCapture(token, text, captureDate1);
			})
			.then(TestUtils.DelayPromise(5000))
			.then(function() {
				TestUtils.createCapture(token, text, captureDate2);
			})
			.then(TestUtils.DelayPromise(5000))
			.then(function(){
				chai.request(server)
					.get('/users/' + newUsername + '/entries')
					.end((err, res) => {
						expect(res.body).to.be.an('array');
						expect(res.body).to.have.lengthOf(2);
						expect(res.body[0].captures).to.have.lengthOf(2);
						expect(res.body[1].captures).to.have.lengthOf(2);
						entryId1 = res.body[1]['_id'];
						entryId2 = res.body[0]['_id'];
						captureId1 = res.body[0].captures[0]['_id'];
						done();
					});
			});
	});

	it('should delete entire entry', (done) => {
		chai.request(server)
			.delete('/users/self/entries/' + entryId1)
			.set({'x-access-token': token})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				done();
			});
	});

	it('should leave only second entry in journal', (done) => {
		chai.request(server)
			.get('/users/' + newUsername + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body).to.have.lengthOf(1);
				expect(res.body[0]).to.have.property('entryDate').and.equal(captureDate2);
				done();
			});
	});

	it('should delete one capture', (done) => {
		chai.request(server)
			.delete('/users/self/captures/' + captureId1)
			.set({'x-access-token': token})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				done();
			});
	});

	it('should leave entry with one capture', (done) => {
		chai.request(server)
			.get('/users/' + newUsername + '/entries')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(res.body).to.have.lengthOf(1);
				expect(res.body[0]).to.have.property('entryDate').and.equal(captureDate2);
				expect(res.body[0].captures).to.have.lengthOf(1);
				expect(res.body[0].captures[0]).to.have.property('_id').and.not.equal(captureId1);
				done();
			});
	});
});


