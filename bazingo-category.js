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

// TODO: create unit tests for Category
function Category(name) {
    this.phrases = {};
    this.name = name;
    this.freeCell = null;
}

/**
 * Add a phrase to the phrase list.
 * @param {Object} phrasePair
 */
Category.prototype.addPhrase = function (phrasePair) {
    if (this.phrases.hasOwnProperty(phrasePair.phrase)) {
        // FIXME: add error checking
    }
    this.phrases[phrasePair.phrase]= phrasePair.description;
};

// TODO: create integration tests for TSV to Category
function makeCategory(tsvPath, callback) {
    // FIXME: error handling? is it needed here?
    var category = new Category(path.basename(tsvPath, '.tsv'));

    var parser = new sv.Parser()
        .on('data', function (obj) {
            if (!category.freeCell) {
                category.freeCell = obj;
            } else {
                category.addPhrase(obj);
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
 * @param {String} [basePath]
 */
exports.createCategories = function createCategories(callback, basePath) {
    basePath = typeof basePath !== 'undefined' ? basePath : 'phrases';
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