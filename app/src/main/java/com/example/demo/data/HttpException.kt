package com.example.demo.data

import java.io.IOException

class HttpException : IOException {
    var code: Any = 0
        private set

    constructor(code: Int, message: String) : super(message) {
        this.code = code
    }

    constructor(code: String, message: String) : super(message) {
        this.code = code
    }

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)

    override fun toString(): String {
        return "HttpException{code=$code, message='$message', cause=$cause}"
    }
}
