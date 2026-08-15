package com.example.demo.data

class HttpResponse<T>(val code: String = "", val message: String = "", val data: T?) {
    override fun toString(): String {
        return "HttpResponse{code='$code', message='$message', data=$data}"
    }
}
