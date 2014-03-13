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

    $.getJSON('javascripts/phrases/' + categoryName + '.json'
        ).fail(function () {
            // TODO: handle error case
        }
    ).done(function (data) {
            simpleJsonHandler(data);
        }
    );

    function simpleJsonHandler(data) {
        // see https://docs.google.com/document/d/13KzkFXrJMa007kJnSrzNyDvolV5pMrv9Jmh4u1pYH8I/edit#heading=h.uyopupdsl3wv
        // for board design specs

        // we need at least 24 cells to fill a board, "Free" cell is the 25th
        var minimumCells = 24;

        // populate the array with random phrases
        function pickPhrases(phrases) {
            if (phrases.length < minimumCells) {
                // TODO: handle error case
            }

            // bucket of numbers to avoid copying a bunch of objects
            var bucket = [];
            var phrasesLength = phrases.length;
            for (var i = 0; i < phrasesLength; i++) {
                bucket.push(i);
            }

            // pick a random phrase from the bucket without replacement
            function getRandomPhrase() {
                var randomIndex = Math.floor(Math.random() * bucket.length);
                var index = bucket.splice(randomIndex, 1)[0];
                // TODO: check for undefined
                return phrases[index];
            }

            var ret = [];
            for (var j = 0; j < minimumCells; j++) {
                ret.push(getRandomPhrase());
            }
            return ret;
        }

        // TODO: validate the json format? after generation too?
        var cells = pickPhrases(data.phrases);

        // give normal cells their text
        var $cells = $('.board td');
        var $cellsSansFreeCell = $cells.not('#freeCell');

        $cellsSansFreeCell.each(function () {
            var c = cells.pop();
            $(this).text(c.text);
        });

        // TODO: assert only one freeCell?
        // TODO: assert freeCell exists, if not set to Free, or report error?
        // give the "Free" cell its text
        $('#freeCell').first().text(data.freeCell.text);
        // board cell clicking event

        // attach click event handlers to all cells
        // TODO: possibly cache selector
        $cells.click(function () {
            $(this).toggleClass('enabled');
        });
    }

    function frequencyBasedJsonHandler(data) {
        // see https://docs.google.com/document/d/13KzkFXrJMa007kJnSrzNyDvolV5pMrv9Jmh4u1pYH8I/edit#heading=h.uyopupdsl3wv
        // for board design specs

        // for each category of frequency, there needs to be
        // at least 8 cells to choose from
        var minimumCells = 8;

        // populate the array with random phrases
        function pickPhrases(phrases) {
            if (phrases.length < minimumCells) {
                // TODO: handle error case
            }

            // bucket of numbers to avoid copying a bunch of objects
            var bucket = [];
            var phrasesLength = phrases.length;
            for (var i = 0; i < phrasesLength; i++) {
                bucket.push(i);
            }

            // pick a random phrase from the bucket without replacement
            function getRandomPhrase() {
                var randomIndex = Math.floor(Math.random() * bucket.length);
                var index = bucket.splice(randomIndex, 1)[0];
                // TODO: check for undefined
                return phrases[index];
            }

            var ret = [];
            for (var j = 0; j < minimumCells; j++) {
                ret.push(getRandomPhrase());
            }
            return ret;
        }

        // TODO: validate the json format? one, two and three might not exist?
        var cells = {
            "one": pickPhrases(data.one),
            "two": pickPhrases(data.two),
            "three": pickPhrases(data.three)
        };

        // give normal cells their text
        $.each(cells, function (key, value) {
            // TODO: picker selector
            $('.' + key).each(function () {
                var p = value.pop();
                if (typeof p !== 'undefined') {
                    $(this).text(p.text);
                } else {
                    // TODO: report error?
                }
            });
        });

        // TODO: assert only one freeCell?
        // TODO: assert freeCell exists, if not set to Free, or report error?
        // give the "free cell" its text
        $('#freeCell').first().text(data.freeCell.text);
        // board cell clicking event

        // attach click event handlers to all but the free cells
        // TODO: possibly cache selector
        $('.board td').not('#freeCell').click(function () {
            $(this).toggleClass('enabled');
        });
    }
});
