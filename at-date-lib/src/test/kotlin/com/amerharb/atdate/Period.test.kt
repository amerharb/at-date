package com.amerharb.atdate

import com.amerharb.atdate.Examples.example4
import com.amerharb.atdate.Examples.example5
import com.amerharb.atdate.Examples.example6
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestPeriod {
	@Test
	@OptIn(ExperimentalUnsignedTypes::class)
	fun testGetPayloadExample4() {
		// 0x880002
		val expected = ubyteArrayOf(0x88U, 0x00U, 0x02U).toTypedArray()
		val actual = example4.getPayload()
		assertContentEquals(expected, actual)
	}

	@Test
	fun testGetNotationExample4() {
		val expected = "@P+1D { d:1 t:0 l:0-0 }@"
		val actual = example4.getNotation()
		assertEquals(expected, actual)
	}

	@Test
	@OptIn(ExperimentalUnsignedTypes::class)
	fun testGetPayloadExample5() {
		// 0xa0
		val expected = ubyteArrayOf(0xa0U).toTypedArray()
		val actual = example5.getPayload()
		assertContentEquals(expected, actual)
	}

	@Test
	fun testGetNotationExample5() {
		val expected = "@P-tp@"
		val actual = example5.getNotation()
		assertEquals(expected, actual)
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	@Test
	fun testGetPayloadExample6() {
		// 0xA80002
		val expected = ubyteArrayOf(0xA8U, 0x00U, 0x02U).toTypedArray()
		val actual = example6.getPayload()
		assertContentEquals(expected, actual)
	}

	@Test
	fun testGetNotationExample6() {
		val expected = "@P-1D { d:1 t:0 l:0-0 }@"
		val actual = example6.getNotation()
		assertEquals(expected, actual)
	}
}
