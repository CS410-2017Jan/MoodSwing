const chai = require('chai');
const chaiHttp = require('chai-http');
const expect = chai.expect;
const HttpStatus = require('http-status-codes');
const server = require('../server');

chai.use(chaiHttp);

module.exports.createCapture = function(token, text, date, emotion='') {
	return new Promise((resolve, reject) => {
		chai.request(server)
			.post('/users/self/captures')
			.set({'x-access-token': token})
			.send({text: text, captureDate: date, emotion: emotion})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				resolve();
			});
	});
}

module.exports.createUser = function(username, password) {
	return new Promise((resolve, reject) => {
		chai.request(server)
			.post('/users')
			.send({username: username, password: password})
			.end((err, res) => {
				expect(res).to.have.status(HttpStatus.CREATED);
				resolve(res.body.token);
			});
	});
}

// To avoid race conditions for database operations
module.exports.DelayPromise = function(delay) {
  return function(data) {
    return new Promise(function(resolve, reject) {
      setTimeout(function() {
        resolve(data);
      }, delay);
    });
  }
}