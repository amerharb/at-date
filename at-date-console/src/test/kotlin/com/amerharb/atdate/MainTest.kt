package com.amerharb.atdate

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.util.LinkedList
import java.util.Queue
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MainTest {
	// Encoding Moment Examples
	@Test
	fun testEncodeExample1() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@2019-05-05{d:1 t:0 a:s l:0-0}@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0xc007e2
			|Bin: 0b110000000000011111100010
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testEncodeExample2() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:0-0}@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0xd607e3179c10
			|Bin: 0b110101100000011111100011000101111001110000010000
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testEncodeExample3() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:1-1}@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0x459407e3179c100202
			|Bin: 0b010001011001010000000111111000110001011110011100000100000000001000000010
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	// Decoding Moment Examples
	@Test
	fun testDecodeExample1() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0xC007E2"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@

		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testDecodeExample2() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0xD607E3179C10"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @2019-05-05T19:53:00+02:00 { d:1 t:5 z:1 a:s l:0-0 }@
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testDecodeExample3() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0x459407e3179c100202"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @2019-05-05T19:53:00+02:00 { d:1 t:5 z:1 a:s l:1-1 }@
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	// Encoding Period Examples
	@Test
	fun testEncodePeriodCase1() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@P1D@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0x880002
			|Bin: 0b100010000000000000000010
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testEncodePeriodCase2() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@P15D@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0x88001e
			|Bin: 0b100010000000000000011110
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testEncodePositiveTinyPeriod() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@P+tp@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0x80
			|Bin: 0b10000000
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testEncodeNegativeTinyPeriod() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "@P-tp@"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Encoding...
			|Hex: 0xa0
			|Bin: 0b10100000
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	// Decoding Period Examples
	@Test
	fun testDecodeCase1() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0x880002"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @P+1D { d:1 t:0 l:0-0 }@
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testDecodeCase2() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0x88001e"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @P+15D { d:1 t:0 l:0-0 }@
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testDecodePositiveTinyPeriod() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0x80"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @P+tp@
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	fun testDecodeNegativeTinyPeriod() {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		System.setOut(PrintStream(outContent))
		val input = "0xa0"
		main(arrayOf(input))
		val actual = outContent.toString().replace("\r\n", "\n")
		val expected = """
			|@Date
			|input: $input
			|Decoding...
			|Notation: @P-tp@
			|
		""".trimMargin("|")
		assertEquals(expected, actual)
		System.setOut(originalOut)
	}

	@Test
	@Ignore // Ignored as this test can not work in GitHub Actions
	fun testCopyResult() {
		val originalInputStream = System.`in`
		try {
			clearClipboard()
			System.setIn(InputStreamMock("0xa0\nc\nq"))
			main(emptyArray())
			val expected = "@P-tp@"
			val actual = getClipboard()
			assertEquals(expected, actual)
		} finally {
			System.setIn(originalInputStream)
		}
	}
}

class InputStreamMock(input: String) : InputStream() {
	private val queue: Queue<Char> = LinkedList()

	init {
		this.queue.addAll(input.toList())
	}

	override fun read(): Int {
		return this.queue.poll()?.code ?: -1
	}
}

fun getClipboard(): String {
	val clipboard = Toolkit.getDefaultToolkit().systemClipboard
	return clipboard.getData(DataFlavor.stringFlavor) as String
}

fun clearClipboard() {
	try {
		val clipboard = Toolkit.getDefaultToolkit().systemClipboard
		clipboard.setContents(StringSelection(""), null)
	} catch (e: Exception) {
		println("Error: ${e.message}")
		System.err.println(e.message)
	}
}
