package com.example.demo.data

import java.io.IOException

class HttpException : IOException {
    var code: Int = 0
        private set

    constructor(message: String) : super(message)

    constructor(code: Int, message: String) : super(message) {
        this.code = code
    }

    constructor(message: String, cause: Throwable) : super(message, cause)

    override fun toString(): String {
        return "HttpException{code=$code, message='$message', cause=$cause}"
    }
}