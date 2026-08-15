package com.example.demo.activity.paging

import java.util.Objects

internal class ItemData(val name: String) {
    var id: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemData

        if (name != other.name) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }
}
