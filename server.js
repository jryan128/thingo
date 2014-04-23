var path = require('path');
var fs = require('fs');
var express = require('express');
var categories = require('./bazingo-category');
var board = require('./bazingo-board');

function setupRoutes(app, categories) {
    app.get('/', function(req, res){
        res.render('index.ejs');
    });
    app.get('/categoryPage', function(req, res){
        res.render('categoryPage.ejs', {categories: categories});
    });
    app.get('/menuPage', function(req, res){
        res.render('menuPage.ejs', {category: req.query.category});
    });
    app.get('/guidedPage', function(req, res){
        var category = categories[req.query.category];
        res.render('guidedPage.ejs', {phrases: category.phrases});
    });
    app.get('/randomBoard', function(req, res){
        var category = categories[req.query.category];
        var phrases = board.getRandomBoardPhrases(category.phrases);
        res.render('board.ejs', {phrases: phrases, freeCell: category.freeCell});
    });
    app.post('/guidedBoard', function(req, res){
        var category = categories[req.query.category];
        console.log(req);
//        res.render('board.ejs', {phrases: phrases, freeCell: category.freeCell});
    });
    app.use(express.static(__dirname + '/public'));
}

function runServer(categories) {
    console.log('Categories loaded: ' + Object.keys(categories).join(','));

    var app = express();
    var port = 3000;

    setupRoutes(app, categories);
    app.listen(port);
    console.log('Bazingo server started on port ' + port);
}

console.log('Initializing server...');
categories.createCategories(runServer);