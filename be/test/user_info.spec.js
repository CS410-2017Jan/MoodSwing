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
var displayName;
var token;
var newDisplayName;
var newPassword;

describe('user creation', () => {
  before(() => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		password = 'test';
		displayName = 'TestDisplayName';
  });

	it('should not create user without password param', (done) => {
		chai.request(server)
			.post('/users/')
			.send({username: newUsername})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.BAD_REQUEST);
				done();
			});
	});

	it('should return error when getting non-existent user', (done) => {
		chai.request(server)
			.get('/users/' + newUsername)
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.NOT_FOUND);
				done();
			});
	});

	it('should not accept login with non-existent user', (done) => {
		chai.request(server)
			.post('/users/login')
			.send({username: newUsername, password: password})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.BAD_REQUEST);
				done();
			});
	});

	it('should create user with proper params', (done) => {
		chai.request(server)
			.post('/users')
			.send({username: newUsername, password: password, displayName: displayName})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				done();
			});
	});

	it('should not create user with same username', (done) => {
		chai.request(server)
			.post('/users')
			.send({username: newUsername, password: password, displayName: displayName})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CONFLICT);
				done();
			});
	});

	it('should get user info on newly created user', (done) => {
		chai.request(server)
			.get('/users/' + newUsername)
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.have.property('displayName').and.equal(displayName);
				done();
			});
	});

	it('should be on user list for searching', (done) => {
		chai.request(server)
			.get('/users')
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.be.an('array');
				expect(_.some(res.body, {username: newUsername, displayName: displayName})).to.be.true;
				done();
			});
	});

	it('should not accept login with wrong password', (done) => {
		chai.request(server)
			.post('/users/login')
			.send({username: newUsername, password: 'wrongPassword'})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.UNAUTHORIZED);
				done();
			});
	});

	it('should login with correct password and return token', (done) => {
		chai.request(server)
			.post('/users/login')
			.send({username: newUsername, password: password})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.have.property('token').and.be.a('string');
				done();
			});
	});
});


describe('user login and authenticated profile editing', () => {
  before((done) => {
    newUsername = 'automatedtest' + randomstring.generate(7);
		oldPassword = 'test2';
		newPassword = 'newPassword';
		displayName = 'Test2DisplayName';
		newDisplayName = 'Test2NewDisplayName';

		chai.request(server)
			.post('/users')
			.send({username: newUsername, password: oldPassword, displayName: displayName})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				token = res.body.token;
				done();
			});
		});

	it('should change display name and password', (done) => {
		chai.request(server)
			.put('/users/self')
			.set('x-access-token', token)
			.send({username: newUsername, oldPassword: oldPassword,
				newPassword: newPassword, newDisplayName: newDisplayName})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				done();
			});
	});

	it('should not change display name and password with bad authentication', (done) => {
		chai.request(server)
			.put('/users/self')
			.set('x-access-token', token)
			.send({username: newUsername, oldPassword: 'wrongPassword',
				newPassword: 'abc', newDisplayName: '123'})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.UNAUTHORIZED);
				done();
			});
	});

	it('should show new display name when getting user info', (done) => {
		chai.request(server)
			.get('/users/' + newUsername)
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.have.property('displayName').and.equal(newDisplayName);
				done();
			});
	});

	it('should login with new password', (done) => {
		chai.request(server)
			.post('/users/login')
			.send({username: newUsername, password: newPassword})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.OK);
				expect(res.body).to.have.property('token').and.be.a('string');
				done();
			});
	});
});


