var chai = require('chai');
var chaiHttp = require('chai-http');
var server = require('../server');
var User = require('../app/models/user');
var expect = chai.expect;
chai.use(chaiHttp);

const OK = 200;
const ERROR = 400;


describe('/GET user', () => {
	it('it should GET all the books', (done) => {
		chai.request(server)
			.get('/users/eric')
			.end((err, res) => {
				console.log(res.body);
				expect(res).to.have.status(OK);
				expect(res.body).to.be.an('object');
				done();
			});
	});
});