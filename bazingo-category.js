var path = require('path');
var fs = require('fs');
var async = require('async');
var sv = require('sv');

function isError(err) {
    if (err) {
        console.log(err);
        console.log('Shutting down server due to error...');
        return true;
    } else {
        return false;
    }
}

function makeCategory(tsvPath, callback) {
    // FIXME: error handling? is it needed here?
    var category = {
        name: path.basename(tsvPath, '.tsv'),
        phrases: [],
        freeCell: null
    };

    var parser = new sv.Parser()
        .on('data', function (obj) {
            if (!category.freeCell) {
                category.freeCell = obj;
            } else {
                category.phrases.push(obj);
            }
        })
        .on('finish', function () {
            callback(null, category);
        });

    var sprints = fs.createReadStream(tsvPath, {encoding: 'utf8'});
    sprints.pipe(parser);
}

/**
 * Is called when the category loading is finished with the list of categories.
 * @callback categoryLoadFinishedCallback
 * @param {Array} categories
 */

/**
 * Passes loaded categories into callback.
 * @param {categoryLoadFinishedCallback} callback
 */
exports.createCategories = function createCategories(callback) {
    var basePath = 'phrases';
    fs.readdir(basePath, function processFiles(err, files) {
        // FIXME: proper error handling
        if (isError(err)) {
            return;
        }

        files = files.map(function (item) {
            return basePath + '/' + item;
        });
        async.map(files, makeCategory, function onFinished(err, results) {
            // FIXME: proper error handling
            if (isError(err)) {
                return;
            }

            var categories = results.reduce(reduceCategory, {});
            callback(categories);

            function reduceCategory(obj, next) {
                obj[next.name] = next;
                return obj;
            }
        });
    });
};