package com.example.demo.data

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ParameterizedTypeImpl(
    private val rawType: Type,
    private val actualTypeArgument: Type
) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(actualTypeArgument)
    }

    override fun getRawType(): Type {
        return rawType
    }

    override fun getOwnerType(): Type? {
        return null
    }
}
