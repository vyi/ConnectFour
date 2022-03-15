package connectfour

import kotlin.math.min

typealias Mat<K> = MutableList<MutableList<K>>
fun repr(arg : Mat<Int>) : Unit {

    for (index in arg[0].indices) { // Each row
        print(" ${index + 1}")
    }
    println(" ") //change line
    for (index in 1..arg.size) { // Each row
        val tempML = arg[index-1]
        for (element in tempML) {
            if (element > 0) {
                // We'll do something here later
                when(element) {
                    1 -> print("║o")
                    2 -> print("║*")
                }
            } else {
                print("║ ")
            }
        }
        println("║")
    }
    val tempML = arg.last()
    print("╚")
    for (index in 1..tempML.size-1) {
        print("═╩")
    }
    println("═╝")
}

fun getTranspose(board: Mat<Int>) : Mat<Int> {
    val numRows = board.size
    val numColumns = board[0].size

    val tBoard = mutableListOf<MutableList<Int>>()

    for (j in 0..numColumns-1) {
        val tRow = mutableListOf<Int>()
        for (i in 0..numRows-1) {
            tRow.add(board[i][j])
        }
        tBoard.add(tRow)
    }
    return tBoard
}

fun getRowAsStr(m : Mat<Int>) : String {
    val numRows = m.size
    var ss = ""
    for (i in 0..numRows-1) {
        ss += m[i].joinToString("")

        if (i < numRows-1){
            ss += "_"
        }
    }
    return ss
}
fun getDiagonalStr(m: Mat<Int>) : String {
    var ss = ""
    val numRows = m.size
    val numColumns = m[0].size

    if (numRows<4 || numColumns<4){
        print("The board is smaller than expected!")
        return ""
    }

    for (i in 3..numRows-1) { // traverse all rows greater than 4
        val diag = mutableListOf<Int>()
        for (j in 0..i) {
            if (j < numColumns) {
                diag.add(m[i-j][j])
            } else {
                // there are no more columns to add to diagonal
                break
            }
        }
        ss += diag.joinToString("")
        ss += '_'
    }

    for (j in 1..numColumns-4) {
        val diag = mutableListOf<Int>()
        var jj = j
        for (i in numRows-1 downTo 0) {
            diag.add(m[i][jj++])
            if (jj > numColumns-1){
                break
            }
        }
        ss += diag.joinToString("")
        ss += '_'
    }

    return ss
}

fun playGame(rows: Int, cols: Int, player1: String, player2: String, firstChance: Int=0, numGames: Int=1): Int {
    // Flag to keep track of game process

    var game_status = 0

    val board = mutableListOf<MutableList<Int>>()
    var GAME_ON = true
    var player1turn = (firstChance % 2) == 0

    //Initialize board
    for (i in 1..rows) {
        val tempML = mutableListOf<Int>()
        for (j in 1..cols) {
            tempML.add(0)
        }
        board.add(tempML)
    }
    val cols_population = mutableMapOf<Int, Int>()
    for (j in 1..cols) {
        cols_population.put(j,0)
    }


    repr(board)
    // Take user input
    var cnum = 0
    do {

        if (player1turn) println("$player1's turn:") else println("$player2's turn:")
        val ss = readLine()!!
        if (ss.matches(Regex("end"))) {
            GAME_ON = false
            //println("Game over!")
            return -1
        }
        try {
            cnum = ss.toInt()
        } catch (e: NumberFormatException){
            println("Incorrect column number")
            continue
        }
        if (cnum > cols || cnum < 1) {
            println("The column number is out of range (1 - $cols)")
            continue
        }
        val fillLevel = cols_population[cnum]!!
        if (fillLevel > rows){
            println("Column $cnum is full")
            continue
        } else {
            cols_population[cnum] = fillLevel + 1

            if (player1turn) {
                board[rows - fillLevel - 1][cnum-1] = 1
                player1turn = false
            } else {
                board[rows - fillLevel - 1][cnum-1] = 2
                player1turn = true
            }
        }

        // Game decision
        // Create string to check if the game has been won
        val r1 = getRowAsStr(board)
        val r2 = getRowAsStr(getTranspose(board))
        val r3 = getDiagonalStr(board)
        val r4 = getDiagonalStr(vflip(board))
        //print(r4)
        // Has player1 won ?
        if (r1.contains("1111") || r2.contains("1111") || r3.contains("1111") || r4.contains("1111")) {
            game_status = 1
            GAME_ON = false
        }
        // Has player2 won ?
        if (r1.contains("2222") || r2.contains("2222") || r3.contains("2222") || r4.contains("2222")) {
            game_status = 2

            GAME_ON = false
        }
        // Is there empty space left in the board ?
        if (! r1.contains('0')) {
            game_status = 3
            GAME_ON = false
        }
        //
        repr(board)

    }while(GAME_ON)

    return game_status

}
fun playGames(player1: String, player2: String) : MutableList<Int> {

    var player1Tally = 0
    var player2Tally = 0
    var totalGames = 0
    var numGames = 0
    var curGame = 0
    // Board size settings
    var rows = 6  // Default setting for num of rows
    var cols = 7  // Default setting for num of columns
    var input_parsed = false

    do {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")

        // Check input format
        val regex = Regex("\\d+\\s*[xX]\\s*\\d+")
        val ss = readLine()!!.trim()
        if (ss.matches(regex)) { // 3 input tokens received
            if(ss.contains("x")) {
                rows = ss.split("x").first().trim().toInt()
                cols = ss.split("x").last().trim().toInt()
            }
            if(ss.contains("X")) {
                rows = ss.split("X").first().trim().toInt()
                cols = ss.split("X").last().trim().toInt()
            }
            if (rows < 5 || rows > 9) {
                println("Board rows should be from 5 to 9")
                input_parsed = false
            } else {
                if (cols < 5 || cols > 9){
                    println("Board columns should be from 5 to 9")
                } else {
                    input_parsed = true
                }
            }
        } else {
            if (ss.isEmpty()) input_parsed = true else println("Invalid input")
        }
    }while (!input_parsed)

    //Parse the number of games
    input_parsed = false
    do {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        print("Input a number of games: ")
        val ss = readLine()!!.trim()
        if (ss.length == 0){
            input_parsed = true
            numGames = 1
        } else {
            //parse the integer and brace for NumberFormatException
            try {
                numGames = ss.toInt()
            } catch (e: NumberFormatException) {
                println("Invalid Input")
                continue
            }

            //is the number zero ?
            if (numGames <= 0){
                println("Invalid Input")
                continue
            }else {
                input_parsed = true
            }

        }

    } while(!input_parsed)

    // Log the settings chosen
    println("$player1 VS $player2")
    println("$rows X $cols board")
    if (numGames == 1) {
        println("Single game")
    } else {
        println("Total $numGames games")
    }

    while(totalGames < numGames){
        if (numGames>1) {
            println("Game #${curGame+1}")
        }
        val winner = playGame(rows, cols, player1, player2, curGame, totalGames)
        if (winner == -1) {
            //player ended the game
            break
        }
        curGame++
        totalGames += 1
        when {
            winner == 1 -> {player1Tally += 2
                println("Player $player1 won")}
            winner == 2 -> {player2Tally += 2
                println("Player $player2 won")}
            winner == 3 -> {++player1Tally
                ++player2Tally
                println("It is a draw")
            }

        }
        if (numGames>1) {
            println("Score\n$player1: $player1Tally $player2: $player2Tally ")
        }
    }

    println("Game over!")
    return mutableListOf(player1Tally, player2Tally)
}

fun main() {
    // Name of the game
    println("Connect Four")
    // Input player names
    println("First player's name: ")
    val player1 = readLine()!!
    println("Second player's name: ")
    val player2 = readLine()!!

    val scoreBoard = playGames(player1, player2)

}

fun vflip(m : Mat<Int>): Mat<Int> {
    val numRows = m.size
    val numColumns = m[0].size
    val res = mutableListOf<MutableList<Int>>()
    for (i in 0..numRows-1) {
        val tRow = mutableListOf<Int>()
        for (j in numColumns-1 downTo 0) {
            tRow.add(m[i][j])
        }
        res.add(tRow)
    }
    return res
}