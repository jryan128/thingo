import groovy.json.JsonBuilder

// converts all csv files inside of category-csv into json files
// puts the generated files directly into the Bazingo project
new File('category-csv').eachFileMatch(~/.*\.csv/) { inputFile ->
    println "Converting $inputFile to json..."
    def data = inputFile.text.split('\n').collect { it.split(',')}
    def builder = new JsonBuilder()
    builder {
        freeCell {
            text data[0][0]
            description data[0][1]
        }
        phrases data[1..-1].collect {
            if (it.size() > 1) {
                [text: it[0], description: it[1]]
            } else {
                [text: it[0]]
            }
        }
    }

    def outputFile = new File("../javascripts/phrases/${inputFile.name.replaceAll('\\.csv', '')}.json")
    println "Outputting json to $outputFile..."
    outputFile.withWriter { builder.writeTo(it) }
}
