package com.example.demo.util

import java.lang.reflect.Field

object ReflectUtils {
    @JvmStatic
    fun setField(clazz: Class<*>, obj: Any?, name: String, value: Any?): Boolean {
        try {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            field.set(obj, value)
            return true
        } catch (_: Exception) {
            return false
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getFields(
        clazz: Class<*>,
        obj: Any?,
        filter: (field: Field) -> Boolean = { true }
    ): MutableMap<String, Any?> {
        val map = HashMap<String, Any?>()
        try {
            for (field in clazz.declaredFields) {
                if (filter(field)) {
                    field.isAccessible = true
                    map[field.getName()] = field.get(obj)
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return map
    }
}
