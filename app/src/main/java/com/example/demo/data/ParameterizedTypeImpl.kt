package com.example.demo.data

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ParameterizedTypeImpl(
    private val rawType: Type,
    private vararg val typeArguments: Type
) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> = arrayOf(*typeArguments)

    override fun getRawType(): Type = rawType

    override fun getOwnerType(): Type? = null
}
