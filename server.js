var express = require('express');

var sv = require('sv');
var parser = new sv.Parser();
var romCom = [];
parser.on('data', function(obj) {
//    console.log('sprinter ->', obj);
    romCom.push(obj);
});
parser.on('finish', function(){
    console.log(romCom);
});
var fs = require('fs');
var sprints = fs.createReadStream('phrases/Romantic Comedy.tsv', {encoding: 'utf8'});
sprints.pipe(parser);

var app = express();
var router = express.Router();
app.use(express.static(__dirname + '/public'));

//app.get('/', function(req, res){
//   res.send('Hello world!');
//});

// parse tsv into object
app.listen(3000);
console.log('Bazingo server started.');
