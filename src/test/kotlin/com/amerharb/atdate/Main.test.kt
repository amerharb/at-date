package com.amerharb.atdate

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMain {
    @Test
    fun testEncodeExample2() {
        val outContent = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outContent))
        val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:0-0}@"
        main(arrayOf(input))
        val actual = outContent.toString().replace("\r\n", "\n")
        val expected = """|@Date
                          |input: $input
                          |Encoding...
                          |Hex: 0xd607e3179c10
                          |Bin: 0b110101100000011111100011000101111001110000010000
                          |""".trimMargin("|")
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
        val expected = """|@Date
                          |input: $input
                          |Encoding...
                          |Hex: 0x459407e3179c100202
                          |Bin: 0b010001011001010000000111111000110001011110011100000100000000001000000010
                          |""".trimMargin("|")
        assertEquals(expected, actual)
        System.setOut(originalOut)
    }

    // Decoding Examples
    @Test
    fun testDecodeExample1() {
        val outContent = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outContent))
        val input = "0xC007E2"
        main(arrayOf(input))
        val actual = outContent.toString().replace("\r\n", "\n")
        val expected = """|@Date
                          |input: $input
                          |Decoding...
                          |Notation: @2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@
                          |""".trimMargin("|")
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
        val expected = """|@Date
                          |input: $input
                          |Decoding...
                          |Notation: @2019-05-05T19:53:00+02:00 { d:1 t:5 z:1 a:s l:1-1 }@
                          |""".trimMargin("|")
        assertEquals(expected, actual)
        System.setOut(originalOut)
    }
}