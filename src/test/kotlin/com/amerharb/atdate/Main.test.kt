package com.amerharb.atdate

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class TestMain {
    @Test
    fun testCase0() {
        val outContent = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outContent))
        val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:0-0}@"
        main(arrayOf(input))
        val actual = outContent.toString()
        // 0xD6 07 E3 17 9C 10
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
    fun testCase1() {
        val outContent = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outContent))
        val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:1-1}@"
        main(arrayOf(input))
        val actual = outContent.toString()
        // 0xD6 07 E3 17 9C 10
        val expected = """|@Date
                          |input: $input
                          |Encoding...
                          |Hex: 0x459407e3179c100202
                          |Bin: 0b010001011001010000000111111000110001011110011100000100000000001000000010
                          |""".trimMargin("|")
        assertEquals(expected, actual)
        System.setOut(originalOut)
    }
}