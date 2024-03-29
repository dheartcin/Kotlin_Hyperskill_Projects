package flashcards
import java.io.File
import kotlin.io.path.*
import kotlin.random.Random

class CardSet(var maxError: Int = 0){
    val cards: MutableMap<String, String> = mutableMapOf<String, String>()
    val errors: MutableMap<String, Int> = mutableMapOf<String, Int>()
    var hardest: MutableList<String> = mutableListOf<String>()
    var maxErrors: Int = 0
    var log = Log()

    fun addCard() {
        //record every action to log attribute
        //get user's term and check if it already exists
        this.log.add("The Card")
        val term = this.log.add()
        if(this.cards.containsKey(term)){
            this.log.add("The card \"$term\" already exists.")
            return
        }
        //get user's definition and check if it already exists
        this.log.add("The definition of the card:")
        val definition = this.log.add()
        if(this.cards.containsValue(definition)){
            this.log.add("The definition \"$definition\" already exists.")
            return
        }
        //add new card to the set and set and update error mutable map
        this.cards.put(term, definition)
        if(!this.errors.containsKey(term)) this.errors.put(term, 0)

        this.log.add("The pair (\"$term\":\"$definition\") has been added.")
    }
    fun removeCard(){
        this.log.add("Which card?")
        val term = this.log.add()
        if(this.cards.containsKey(term)){
            this.cards.remove(term)
            this.log.add("The card has been removed.")
        } else{
            this.log.add("Can't remove \"$term\": there is no such card.")
        }

        if(this.errors.containsKey(term)) this.errors.remove(term)
    }
    fun import(importFile: String){
        val fileName: String
        //importFile won't be an empty string if passed as run argument
        //if not, ask user for file name
        if(importFile == ""){
            this.log.add("File name:")
            fileName = this.log.add()
        } else{
            fileName = importFile
        }
        val filePath = Path(fileName)
        if(filePath.exists()){
            val lines: List<String> = filePath.readLines()
            for(line in lines){
                val split = line.split(" ")
                this.cards.put(split[0], split[1])
                //if line in import file contains error count, load it as well
                if(split.size == 3) {
                    this.errors.put(split[0], split[2].toInt())
                } else {
                    this.errors.put(split[0], 0)
                }
            }
            this.log.add("${lines.size} cards have been loaded.")
        } else {
            this.log.add("File not found.")
            return
        }
    }
    fun export(exportFile: String){
        val fileName: String
        if(exportFile == ""){
            this.log.add("File name:")
            fileName = this.log.add()
        } else{
            fileName = exportFile
        }
        val filePath = Path(fileName)

        //uncomment lines below to test output file
        //val myFile = Path("C:\\Users\\Violin\\OneDrive\\${filePath.fileName}.txt")
        filePath.writeText("")
        //myFile.writeText("")
        for((term, definition) in this.cards){
            filePath.appendText("$term $definition ${this.errors[term]}\n")
            //myFile.appendText("$term $definition ${this.errors[term]}\n")
            this.log.add("added pair $term $definition")
        }
        this.log.add("${this.cards.size} cards have been saved.")
    }
    fun ask(){
        val allTerms: Set<String> = this.cards.keys
        this.log.add("How many times to ask?")
        val number = this.log.add().toInt()

        for(i in 1..number) {
            val random = Random.nextInt(0, this.cards.size)
            val randomTerm = allTerms.elementAt(random)
            val randomDefinition = this.cards[randomTerm]

            this.log.add("Print the definition of \"$randomTerm\":")
            val userDefinition = this.log.add()
            if (userDefinition == randomDefinition) {
                this.log.add("Correct!")
            } else {
                //increase current error count if user gets it wrong.
                //make sure it's not null.
                val current: Int = this.errors[randomTerm]!!
                this.errors[randomTerm] = current + 1

                //if the definition already exists, find the corresponding term
                if (this.cards.containsValue(userDefinition)) {
                    this.log.add(
                        "Wrong. The right answer is \"$randomDefinition\", but your definition is correct for \"${
                            this.cards.filterValues { it == userDefinition }.keys.elementAt(
                                0
                            )
                        }\"."
                    )
                } else {
                    this.log.add("Wrong. The right answer is \"$randomDefinition\".")
                }
            }
        }
    }
    fun hardestCard(){
        var max = this.maxErrors
        for(card in this.errors){
            if(card.value > max) {
                max = card.value
                //clear current card list of max error count
                //so that we can store those with new max count
                this.hardest.clear()
                this.hardest.add(card.key)
            }
            else if((card.value == max)&&(!this.hardest.contains(card.key))) {
                //add cards with same error count to the list
                this.hardest.add(card.key)
            }
        }
        if(max == 0) this.log.add("There are no cards with errors.")
        else if(this.hardest.size == 1){
            this.log.add("The hardest card is \"${this.hardest[0]}\". You have $max errors answering it.")
        } else{
            this.log.add("The hardest cards are ${this.hardest.joinToString(transform = {"\"$it\""})}. You have $max errors answering them.")
        }
    }
    fun resetStats(){
        this.errors.clear()
        this.hardest.clear()
        this.maxErrors = 0
        this.log.add("Card statistics have been reset.")
    }
}
class Log(){
    val logs: MutableList<String> = mutableListOf<String>()

    fun add(output: String){
        println(output)
        this.logs.add(output)
    }
    fun add(): String{
        val userInput: String = readln()
        this.logs.add(userInput)
        return userInput
    }
    fun add(list: MutableList<String>){
        this.logs.addAll(list)
    }
    fun add(secondLog: Log){
        this.logs.addAll(secondLog.logs)
    }
    fun saveToFile(){
        this.add("File name:")
        val filePath = Path(this.add())
        filePath.writeText("")
        for(line in this.logs) {
            filePath.appendText("$line\n")
        }
        this.add("The log has been saved.")
    }
}

fun main(args: Array<String>) {
    val message = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"
    var userAction = ""
    val set= CardSet(0)
    val myLog = Log()
    var importFile: String = ""
    var exportFile: String = ""

    if(args.contains("-import")){
        set.import(args[args.indexOf("-import")+1])
    }

    //get user action
    while(userAction != "exit"){
        myLog.add(message)
        userAction = myLog.add()
        when(userAction){
            "add" -> set.addCard()
            "remove" -> set.removeCard()
            "import" -> set.import(importFile)
            "export" -> set.export(exportFile)
            "ask" -> set.ask()
            "exit" -> {
                if(args.contains("-export")){
                    set.export(args[args.indexOf("-export")+1])
                }
                println("Bye bye!")
            }
            "log" -> {
                myLog.add(set.log)
                myLog.saveToFile()
            }
            "hardest card" -> set.hardestCard()
            "reset stats" -> set.resetStats()
        }
    }
}

