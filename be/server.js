const express = require('express')
const app = express()
const bodyParser= require('body-parser')
const _ = require('lodash')
const MongoClient = require('mongodb').MongoClient

app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json());


app.get('/hello', (req, res) => {
  res.send('Hello World')
})

app.post('/quotes', (req, res) => {

	console.log(req.body)
	res.send('ok');

  // db.collection('quotes').save(req.body, (err, result) => {
  //   if (err) return console.log(err)
  //   console.log('saved to database')
  //   res.redirect('/')
  // })
})


MongoClient.connect('mongodb://admin:cpsc410@ds117109.mlab.com:17109/db1', (err, database) => {
  if (err) return console.log(err)
  var db = database
  app.listen(3000, () => {
    console.log('listening on 3000')
  })
})