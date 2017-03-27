const chai = require('chai');
const chaiHttp = require('chai-http');
const server = require('../server');
const User = require('../app/models/user');
const expect = chai.expect;
const randomstring = require("randomstring");
const HttpStatus = require('http-status-codes');
const _ = require('lodash');

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

		chai.request(server)
			.post('/users')
			.send({username: newUsername, password: password})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				token = res.body.token;
				done();
			});
		});

	it('should create a text capture', (done) => {
		createCapture(token, text, captureDate1).then(function() {
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
		createCapture(token, text, captureDate1).then(function() {
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
		createCapture(token, text, captureDate2).then(function() {
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
  this.timeout(7000);

  before('Make 2 entries with 2 captures each', (done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		text = 'anytext';
		captureDate1 = '1/1/2001';
		captureDate2 = '2/1/2001';

		chai.request(server)
			.post('/users')
			.send({username: newUsername, password: password})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				token = res.body.token;

				createCapture(token, text, captureDate1)
					.then(function(){
						createCapture(token, text, captureDate2)
					})
					.then(function() {
						console.log('a');
						createCapture(token, text, captureDate1)
					})
					.then(DelayPromise(2000))
					.then(function() {
						console.log('b');
						createCapture(token, text, captureDate2)
					})
					.then(DelayPromise(2000))
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
							})
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


function createCapture(token, text, date) {
	return new Promise((resolve, reject) => {
		chai.request(server)
			.post('/users/self/captures')
			.set({'x-access-token': token})
			.send({text: text, captureDate: date})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				resolve();
			});
	});
}

// To avoid race conditions for database operations
function DelayPromise(delay) {
  return function(data) {
    return new Promise(function(resolve, reject) {
      setTimeout(function() {
        resolve(data);
      }, delay);
    });
  }
}

