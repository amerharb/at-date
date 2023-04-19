package com.amerharb.atdate

fun main(args: Array<String>) {
    println("@Date")
    println("input: ${args.joinToString()}")
    if (args.isEmpty()) {
        while (true) {
            mainMenu()
        }
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
            println("Encoding...")
            val atDate = encode(arg1)
            printEncodingResult(atDate.getPayload())
        }
        if (arg1.startsWith("0x")) {
            println("Decoding...")
            val arrayOfBytes = getByteArrayFromHexString(arg1)
            val atDate = decode(arrayOfBytes)
            println("Notation: ${atDate.getNotation()}")
        }
    }
}

fun mainMenu() {
    println("1. Encode")
    println("2. Decode")
    println("3. Exit")
    print("Enter your choice: ")
    when (readlnOrNull()) {
        "1" -> encodeMenu()
        "2" -> decodeMenu()
        "3" -> exitProcess(0)
        else -> {
            println("Invalid choice")
        }
    }
}

fun encodeMenu() {
    try {
        print("Enter date in @Date format: ")
        val input = readlnOrNull() ?: return
        val atDate = encode(input)
        printEncodingResult(atDate.getPayload())
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun decodeMenu() {
    try {
        print("Enter payload in Hex format (0x1234..) :")
        val input = readlnOrNull() ?: return
        val arrayOfUBytes =  getByteArrayFromHexString(input)
        val atDate = decode(arrayOfUBytes)
        println("Notation: ${atDate.getNotation()}")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun exitProcess(status: Int) {
    println("Exiting with status $status")
    kotlin.system.exitProcess(status)
}

fun printEncodingResult(payload: Array<UByte>) {
    println("Hex: 0x${payload.joinToString("") { it.toString(16).padStart(2, '0') }}")
    println("Bin: 0b${payload.joinToString("") { it.toString(2).padStart(8, '0') }}")
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
