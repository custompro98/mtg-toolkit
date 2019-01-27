package com.custompro98.mtgtoolkit.services

import com.custompro98.mtgtoolkit.callbacks.ParsingCallback

interface ParsingService {
    fun parse(callback: ParsingCallback)
}