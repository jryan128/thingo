var path = require('path');
var fs = require('fs');
var async = require('async');

var port = 3000;

function initializeServer(initializeFinished) {
    console.log('Initializing server...');

    function makeCategory(tsvPath, finished) {
        var category = {
            name: path.basename(tsvPath, '.tsv'),
            phrases: [],
            freeCell: null
        };

        var sv = require('sv');
        var parser = new sv.Parser()
            .on('data', function (obj) {
                if (!category.freeCell) {
                    category.freeCell = obj;
                } else {
                    category.phrases.push(obj);
                }
            }).
            on('finish', function () {
                finished(null, category);
            });

        var sprints = fs.createReadStream(tsvPath, {encoding: 'utf8'});
        sprints.pipe(parser);
    }

    var basePath = 'phrases';
    fs.readdir(basePath, function (err, files) {
        files = files.map(function(item) { return basePath + '/' + item; });
        async.map(files, makeCategory, function (err, results) {
            // FIXME: error checking
            if (err) {
                console.log(err);
                console.log('Shutting down server due to error...');
            } else {
                var categories = results.reduce(function (obj, next) {
                    obj[next.name] = next;
                    return obj;
                }, {});
                initializeFinished(categories);
            }
        });
    });
}

var express = require('express');
var app = express();

function loadTemplate(categories) {
    app.render('categoryList.ejs', {categories: categories},
        function(err, html){
            runServer(categories, html);
        });
}

function getRandomBoardPhrases(phrases) {
    // the board format is assumed to be flat.
    // no hierarchy of cell placement like in the design document

    // TSV format: Text \t Description
    // - The first row is the free cell.
    // - There are no header rows.

    // return a list of randomly selected celles
    function pickRandomCells(cells, numberOfCells) {
        if (cells.length < numberOfCells) {
            // TODO: handle error case
        }

        // bucket of numbers to avoid copying a bunch of objects
        var bucket = [];
        var cellLength = cells.length;
        for (var i = 0; i < cellLength; i++) {
            bucket.push(i);
        }

        // pick a random phrase from the bucket without replacement
        function pickRandomCell() {
            var randomIndex = Math.floor(Math.random() * bucket.length);
            var index = bucket.splice(randomIndex, 1)[0];
            return cells[index];
        }

        var ret = [];
        for (var j = 0; j < numberOfCells; j++) {
            ret.push(pickRandomCell());
        }
        return ret;
    }

    // cells looks like: [ [TEXT, DESCRIPTION], [TEXT, DESCRIPTION] ...]
    // we need at least 24 random cells to fill a board, the 25th is the "Free" cell
    var minimumCells = 24;
    return pickRandomCells(phrases, minimumCells);
}

function runServer(categories, categoryPage) {
    app.get('/', function(req, res){
        res.render('index.ejs');
    });
    app.get('/categoryPage', function(req, res){
        res.render('categoryPage.ejs', {categories: categories});
    });
    app.get('/menuPage', function(req, res){
        res.render('menuPage.ejs', {category: req.query.category});
    });
    app.get('/randomBoard', function(req, res){
        var category = categories[req.query.category];
        var phrases = getRandomBoardPhrases(category.phrases);
        var freeCell = category.freeCell;
        res.render('randomBoard.ejs', {phrases: phrases, freeCell: freeCell});
    });

    app.use(express.static(__dirname + '/public'));

    console.log('Categories loaded: ' + Object.keys(categories).join(','));
    app.listen(port);
    console.log('Bazingo server started on port ' + port);
}

initializeServer(loadTemplate);