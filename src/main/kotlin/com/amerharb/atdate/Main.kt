package com.amerharb.atdate

fun main(args: Array<String>) {
    println("@Date")
    println("input: ${args.joinToString()}")
    if (args.isEmpty()) {
        println("enter @Date to encode, 0x... to decode or Q to Quit")
        while (true) {
            mainMenu()
        }
    } else {
        val arg1 = args[0]
        when {
            arg1 == "-h" || arg1 == "--help" -> {
                printHelp()
                exitProcess(0)
            }

            arg1.startsWith("@") -> {
                println("Encoding...")
                val moment = encode(arg1)
                printEncodingResult(moment)
            }

            arg1.startsWith("0x") -> {
                println("Decoding...")
                val arrayOfBytes = getByteArrayFromHexString(arg1)
                val moment = decode(arrayOfBytes)
                printDecodingResult(moment)
            }

            else -> {
                println("Invalid input")
            }
        }
    }
}

fun mainMenu() {
    print(">")
    val command = readlnOrNull()
    if (command.isNullOrBlank()) {
        println("Invalid input")
        return
    }
    when (command[0].lowercase()) {
        "@" -> encodeMenu(command)
        "0" -> decodeMenu(command)
        "q" -> exitProcess()
        else -> {
            println("Invalid input")
        }
    }
}

fun encodeMenu(input: String) {
    try {
        val moment = encode(input)
        printEncodingResult(moment)
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun decodeMenu(input: String) {
    try {
        val arrayOfUBytes = getByteArrayFromHexString(input)
        val moment = decode(arrayOfUBytes)
        printDecodingResult(moment)
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun exitProcess(status: Int = 0) {
    println("Exiting with status $status")
    kotlin.system.exitProcess(status)
}

fun printEncodingResult(moment: Moment) {
    println("Hex: 0x${moment.getPayload().joinToString("") { it.toString(16).padStart(2, '0') }}")
    println("Bin: 0b${moment.getPayload().joinToString("") { it.toString(2).padStart(8, '0') }}")
}

fun printDecodingResult(moment: Moment) {
    println("Notation: ${moment.getNotation()}")
}

fun getByteArrayFromHexString(hexString: String): Array<UByte> {
    require(hexString.startsWith("0x")) { "Hex string must start with '0x'." }
    require(hexString.length % 2 == 0) { "Hex string length must be even." }

    return hexString
        .substring(2)
        .chunked(2)
        .map { it.toUByte(16) }
        .toTypedArray()
}

fun printHelp() {
    println("for encoding a date use @Date format")
    println("Usage: atdate [@ISO-Date {properties}@]")
    println("Example: atdate @2019-05-05 {d:1}@")
    println()
    println("for decoding a date use hexadecimal format")
    println("Usage: atdate [0x....]")
    println("Example: atdate 0xC007E2")
}