var express = require('express');
var app = express();

var router = express.Router();

app.use(express.static(__dirname + '/public'));

//app.get('/', function(req, res){
//   res.send('Hello world!');
//});

app.listen(3000);