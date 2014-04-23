/**
 * Creates a bucket of numbers up to bucketSize
 * @param bucketSize
 * @returns {Array} of numbers in order
 */
function makeBucket(bucketSize) {
    var bucket = [];
    for (var i = 0; i < bucketSize; i++) {
        bucket.push(i);
    }
    return bucket;
}

/**
 * Return an Array of 24 phrases to fill a board with.
 * @param {Array} phrases to choose from
 * @returns {Array} an Array of phrases
 */
exports.getRandomBoardPhrases = function getRandomBoardPhrases(phrases) {
    var numberOfCells = 24;
    if (phrases.length < numberOfCells) {
        // FIXME: handle error case
    }

    // use a bucket of random numbers so we don't copy around
    var bucket = makeBucket(phrases.length);

    var ret = [];
    for (var i = 0; i < numberOfCells; i++) {
        var randomIndex = Math.floor(Math.random() * bucket.length);
        var index = bucket.splice(randomIndex, 1)[0];
        ret.push(phrases[index]);
    }

    return ret;
};