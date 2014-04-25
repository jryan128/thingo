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
 * Return an Array of phrases to fill a board with.
 * @param {Array} phrases to choose from
 * @param {Number} numberOfPhrases
 * @returns {Array} an Array of phrases
 */
function getRandomPhrases(phrases, numberOfPhrases) {
    if (phrases.length < numberOfPhrases) {
        // FIXME: handle error case
    }

    // use a bucket of random numbers so we don't copy around
    var bucket = makeBucket(phrases.length);

    var ret = [];
    for (var i = 0; i < numberOfPhrases; i++) {
        var randomIndex = Math.floor(Math.random() * bucket.length);
        var index = bucket.splice(randomIndex, 1)[0];
        ret.push(phrases[index]);
    }

    return ret;
}

exports.getRandomBoardPhrases = function getRandomBoardPhrases(phrases) {
    return getRandomPhrases(phrases, 24);
};

/**
 *
 * @param {Array} alreadySelected
 * @param {Array} phrases
 * @returns {Array}
 */
exports.getGuidedBoardPhrases = function getGuidedBoardPhrases(alreadySelected, phrases) {
    var totalNeeded = 24;
    var numberNeeded = totalNeeded - alreadySelected.length;

    if (numberNeeded <= 0) {
        return getRandomPhrases(alreadySelected, totalNeeded);
    }

    // FIXME: this function is not efficient in the slightest, it's so slow OMG
    var shuffledCategoryPhrases = getRandomPhrases(phrases, phrases.length);
    var ret = [];
    ret = ret.concat(alreadySelected);
    var len = shuffledCategoryPhrases.length;
    // FIXME: change to while with numberNeeded, but make it safe if category doesn't have enough crap in it?
    for (var i=0; i < len; i++) {
        var p = shuffledCategoryPhrases[i];
        if (!(p in alreadySelected)) {
            ret.push(p);
            numberNeeded -= 1;
        }

        if (numberNeeded === 0) {
            break;
        }
    }
    return getRandomPhrases(ret, ret.length);
};