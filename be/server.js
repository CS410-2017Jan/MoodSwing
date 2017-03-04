const express = require('express')
const app = express()
const bodyParser= require('body-parser')
const _ = require('lodash')
const port = process.env.PORT || 3000;
const MongoClient = require('mongodb').MongoClient
const mongoose = require('mongoose')
const morgan = require('morgan')
const config = require('./config')
const router = express.Router()
const authenticatedRoutes = require('./authenticatedRoutes')
const publicRoutes = require('./publicRoutes')
const User   = require('./app/models/user')

app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json())
app.use(morgan('dev'))

app.use(authenticatedRoutes)
app.use(publicRoutes)


mongoose.connect(config.database, function(err, db) {
  if (err) {
    console.log('Unable to connect to the server. Please start the server. Error:', err)
    return
  }
  app.listen(port, () => {
    console.log('listening on ' + port)
  })
})
