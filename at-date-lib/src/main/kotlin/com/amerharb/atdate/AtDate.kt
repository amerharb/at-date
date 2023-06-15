package com.amerharb.atdate

abstract class AtDate {
    abstract fun getHeader(): Array<UByte>
    abstract fun getBody(): Array<UByte>
    fun getPayload(): Array<UByte> {
        val payload = mutableListOf<UByte>()
        payload.addAll(getHeader())
        payload.addAll(getBody())
        return payload.toTypedArray()
    }

    abstract fun getNotation(): String
}