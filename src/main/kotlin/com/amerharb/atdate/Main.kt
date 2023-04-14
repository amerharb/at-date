package com.amerharb.atdate

fun main(args: Array<String>) {
    println("@Date")
    println("input: ${args.joinToString()}")
    if (args.isEmpty()) {
        mainMenu()
    } else {
        val arg1 = args[0]
        if (arg1 == "-h" || arg1 == "--help") {
            println("for encoding a date use @Date format")
            println("Usage: atdate [@date]")
            println("Example: atdate @2019-05-05 {d:1}@")
            println()
            println("for decoding a date use hexadecimal format")
            println("Usage: atdate [0x....]")
            println("Example: atdate 0xC007E2")
            exitProcess(0)
        }
        if (arg1.startsWith("@")) {
            println("Encoding")
            val atDate = encode(arg1)
            println("Hex: 0x${atDate.getPayload().joinToString("") { it.toString(16) }}")
            println("Bin: 0b${atDate.getPayload().joinToString("") { it.toString(2) }}")
        }
        if (arg1.startsWith("0x")) {
            println("Decoding")
        }
        val atDate = encode(args[0])
    }
}

fun mainMenu() {
    println("1. Encode")
    println("2. Decode")
    println("3. Exit")
    print("Enter your choice: ")
    when (readlnOrNull()?.toInt()) {
        1 -> encodeMenu()
        2 -> TODO()
        3 -> exitProcess(0)
        else -> {
            println("Invalid choice")
            mainMenu()
        }
    }
}

fun encodeMenu() {
    print("Enter date in @Date format: ")
    val input = readlnOrNull() ?: return
    val atDate = encode(input)
    println(atDate.getPayload().joinToString { it.toString(16) })
    mainMenu()
}

fun exitProcess(status: Int) {
    println("Exiting with status $status")
    kotlin.system.exitProcess(status)
}
