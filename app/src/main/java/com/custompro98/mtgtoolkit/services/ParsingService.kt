package com.custompro98.mtgtoolkit.services

interface ParsingService {
    fun parse(callback: (String) -> Unit)
}