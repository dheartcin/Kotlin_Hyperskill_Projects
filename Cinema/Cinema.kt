package cinema

class Cinema(val numRows: Int, val numSeats: Int){
    val Rows = mutableListOf<Row>()
    val totalSeats = numRows * numSeats
    var soldTickets = 0
    val totalIncome: Int
    var currentIncome = 0
    val tickets = mutableListOf(mutableListOf<Ticket>())
    init{
        //Initialize Rows
        for(i in 0..numRows-1){
            this.Rows.add(Row(i+1, numSeats))
        }
        //Initialize Total Income
        this.totalIncome = calculateTotalIncome()
        //Initialize Tickets
        if(totalSeats <= 60) {
            for (i in 0..numRows-1) {
                this.tickets.add(mutableListOf<Ticket>())
                for (j in 0..numSeats-1) {
                    this.tickets[i].add(Ticket(10, i+1, j+1))
                }
            }
        } else {
            for(i in 0..numRows/2-1){
                this.tickets.add(mutableListOf<Ticket>())
                for(j in 0..numSeats-1){
                    this.tickets[i].add(Ticket(10, i+1, j+1))
                }
            }
            for(i in numRows/2..numRows-1){
                this.tickets.add(mutableListOf<Ticket>())
                for(j in 0..numSeats-1){
                    this.tickets[i].add(Ticket(8, i+1, j+1))
                }
            }
        }
    }
    fun printSeatingChart(){
        println("\nCinema:")
        for(i in 0..numSeats-1){
            if(i == 0) print(" ")
            print(" ${i+1}")
            if(i == numSeats-1) print("\n")
        }
        for(currentRow in Rows){
            currentRow.printRow()
            print("\n")
        }
    }
    fun calculateTotalIncome(): Int{
        if(this.totalSeats <= 60) return (totalSeats * 10)
        else return (numSeats*(numRows/2 * 10 + (numRows - numRows/2) * 8))
    }
    fun printTotalIncome(){
        println("Total income:")
        println("$${this.totalIncome}")
    }
    fun ticketPrice(row: Int, seat: Int): Int{
        return this.tickets[row-1][seat-1].price
    }
    fun reserveSeat(row: Int, seat: Int): Boolean{
        if((row-1 >= this.numRows)||(seat-1 >= this.numSeats)){
            println("Wrong Input!")
            return false
        } else if (Rows[row-1].Seats[seat-1].isVacant) {
            this.Rows[row-1].Seats[seat-1].changeSeatStatus(false)
            return true
        } else {
            println("That ticket has already been purchased!")
            return false
        }
    }
    fun buyTicket(){
        var success = false
        while(success == false) {
            println("Enter a row number:")
            val rowNumber = readln().toInt()
            println("Enter a seat number in that row:")
            val seatNumber = readln().toInt()
            success = this.reserveSeat(rowNumber, seatNumber)
            if(success == true){
                this.soldTickets++
                println("Ticket price: $${this.ticketPrice(rowNumber, seatNumber)}")
                this.currentIncome += this.ticketPrice(rowNumber, seatNumber)
            }
        }
        this.printSeatingChart()
    }
    fun calculatePercentage(): String{
        val percentage: Float = this.soldTickets.toFloat()/this.totalSeats * 100
        val formatPercentage = "%.2f".format(percentage)
        return formatPercentage
    }
    fun printStats(){
        println("\nNumber of purchased tickets: ${this.soldTickets}")
        println("Percentage: ${this.calculatePercentage()}%")
        println("Current income: $${this.currentIncome}")
        println("Total income: $${this.totalIncome}\n")
    }
}
class Row(val number: Int, val numSeats: Int){
    val Seats = mutableListOf<Seat>()
    init{
        for(i in 0..numSeats-1){
            this.Seats.add(Seat(true))
        }
    }
    fun printRow(){
        print(this.number)
        for(currentSeat in Seats){
            currentSeat.printSeat()
        }
    }
}
class Seat(var isVacant: Boolean = true){
    fun changeSeatStatus(status: Boolean){
        this.isVacant = status
    }
    fun printSeat(){
        print(" ")
        if(isVacant) print("S")
        else print("B")
    }
}
class Ticket(val price: Int, val row: Int, val seat: Int){
}

fun main() {
    var userAction = 3
    // write your code here
    println("Enter the number of rows:")
    val numRows = readln().toInt()
    println("Enter the number of seats in each row:")
    val numSeats = readln().toInt()

    val myCinema = Cinema(numRows, numSeats)

    while(userAction != 0){
        println("1. Show the seats\n" +
                "2. Buy a ticket\n" +
                "3. Statistics\n" +
                "0. Exit")
        userAction = readln().toInt()
        when(userAction){
            1 -> myCinema.printSeatingChart()
            2 -> myCinema.buyTicket()
            3 -> myCinema.printStats()
            0 -> return
        }
    }
}