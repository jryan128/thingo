var path = require('path');
var fs = require('fs');
var async = require('async');

var port = 3000;

function initializeServer(initializeFinished) {
    console.log('Initializing server...');

    function makeCategory(tsvPath, finished) {
        var category = {
            name: path.basename(tsvPath, '.tsv'),
            phrases: []
        };

        var sv = require('sv');
        var parser = new sv.Parser()
            .on('data', function (obj) {
                category.phrases.push(obj);
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
                    obj[next.name] = next.phrases;
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
function runServer(categories, categoryPage) {
    app.get('/', function(req, res){
        res.send(categoryPage);
    });
    app.use(express.static(__dirname + '/public'));

    console.log('Categories loaded: ' + Object.keys(categories).join(','));
    app.listen(port);
    console.log('Bazingo server started on port ' + port);
}

initializeServer(loadTemplate);