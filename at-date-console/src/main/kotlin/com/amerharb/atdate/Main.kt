package com.amerharb.atdate

fun main(args: Array<String>) {
	println("@Date")
	println("input: ${args.joinToString(" ")}")
	if (args.isEmpty()) {
		println("enter @...@ to encode, 0x... to decode or Q to Quit")
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
				printEncodingResult(encode(arg1))
			}

			arg1.startsWith("0x") -> {
				println("Decoding...")
				val arrayOfBytes = getByteArrayFromHexString(arg1)
				printDecodingResult(decode(arrayOfBytes))
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
		"@" -> encodeCommand(command)
		"0" -> decodeCommand(command)
		"q" -> exitProcess()
		else -> {
			println("Invalid input")
		}
	}
}

fun encodeCommand(input: String) {
	try {
		printEncodingResult(encode(input))
	} catch (e: Exception) {
		println("Error: ${e.message}")
	}
}

fun decodeCommand(input: String) {
	try {
		val arrayOfUBytes = getByteArrayFromHexString(input)
		printDecodingResult(decode(arrayOfUBytes))
	} catch (e: Exception) {
		println("Error: ${e.message}")
	}
}

fun exitProcess(status: Int = 0) {
	println("Exiting with status $status")
	kotlin.system.exitProcess(status)
}

fun printEncodingResult(atDate: AtDate) {
	println("Hex: 0x${atDate.getPayload().joinToString("") { it.toString(16).padStart(2, '0') }}")
	println("Bin: 0b${atDate.getPayload().joinToString("") { it.toString(2).padStart(8, '0') }}")
}

fun printDecodingResult(atDate: AtDate) {
	println("Notation: ${atDate.getNotation()}")
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
	println(
		"""
		for encoding a date use @Date format
		Usage: atdate [@ISO-Date {properties}@]
		Example: atdate @2019-05-05 {d:1}@

		for decoding a date use hexadecimal format
		Usage: atdate [0x....]
		Example: atdate 0xC007E2
		""".trimIndent()
	)
}
