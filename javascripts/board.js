$(function () {
    // disable all text selection
    document.onselectstart = function () {
        return false;
    };
    document.onmousedown = function () {
        return false;
    };

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    // set document title
    var categoryName = getParameterByName("category");
    // TODO: throw error for no category param
    document.title = "Bazingo! :: " + categoryName + " Board";

    $.ajax('javascripts/phrases/' + categoryName + '.tsv'
        ).fail(function () {
            // TODO: handle error case
        }
    ).done(function (data) {
            simpleTsvHandler(data);
        }
    );

    function simpleTsvHandler(data) {
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
        var cells = $.tsv.parseRows(data);
        var TEXT = 0;
        var DESCRIPTION = 1;

        // the free cell is the first row in the tsv
        var freeCell = cells[0];

        // we need at least 24 random cells to fill a board, the 25th is the "Free" cell
        var minimumCells = 24;
        var randomlyPickedCells = pickRandomCells(cells, minimumCells);

        var $cells = $('.board td');
        var $cellsSansFreeCell = $cells.not('#freeCell');
        $cellsSansFreeCell.each(function () {
            $(this).text(randomlyPickedCells.pop()[TEXT]);
        });

        $('#freeCell').first().text(freeCell[TEXT]);

        $cells.click(function () {
            $(this).toggleClass('enabled');
        });
    }

// TODO: implement and use as the default
//    function frequencyBasedJsonHandler(data) {
//        // see https://docs.google.com/document/d/13KzkFXrJMa007kJnSrzNyDvolV5pMrv9Jmh4u1pYH8I/edit#heading=h.uyopupdsl3wv
//        // for board design specs
//
//        // for each category of frequency, there needs to be
//        // at least 8 cells to choose from
//        var minimumCells = 8;
//
//        // populate the array with random phrases
//        function pickPhrases(phrases) {
//            if (phrases.length < minimumCells) {
//                // TODO: handle error case
//            }
//
//            // bucket of numbers to avoid copying a bunch of objects
//            var bucket = [];
//            var phrasesLength = phrases.length;
//            for (var i = 0; i < phrasesLength; i++) {
//                bucket.push(i);
//            }
//
//            // pick a random phrase from the bucket without replacement
//            function getRandomPhrase() {
//                var randomIndex = Math.floor(Math.random() * bucket.length);
//                var index = bucket.splice(randomIndex, 1)[0];
//                // TODO: check for undefined
//                return phrases[index];
//            }
//
//            var ret = [];
//            for (var j = 0; j < minimumCells; j++) {
//                ret.push(getRandomPhrase());
//            }
//            return ret;
//        }
//
//        // TODO: validate the json format? one, two and three might not exist?
//        var cells = {
//            "one": pickPhrases(data.one),
//            "two": pickPhrases(data.two),
//            "three": pickPhrases(data.three)
//        };
//
//        // give normal cells their text
//        $.each(cells, function (key, value) {
//            // TODO: picker selector
//            $('.' + key).each(function () {
//                var p = value.pop();
//                if (typeof p !== 'undefined') {
//                    $(this).text(p.text);
//                } else {
//                    // TODO: report error?
//                }
//            });
//        });
//
//        // TODO: assert only one freeCell?
//        // TODO: assert freeCell exists, if not set to Free, or report error?
//        // give the "free cell" its text
//        $('#freeCell').first().text(data.freeCell.text);
//        // board cell clicking event
//
//        // attach click event handlers to all but the free cells
//        // TODO: possibly cache selector
//        $('.board td').not('#freeCell').click(function () {
//            $(this).toggleClass('enabled');
//        });
//    }
});
