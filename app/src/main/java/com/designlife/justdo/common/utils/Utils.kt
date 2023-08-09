package com.designlife.justdo.common.utils

fun String.camelCase() : String{
    if (this.isEmpty() || this.isBlank())
        return this
    if (this.length > 1)
        return this.get(0).uppercase() + this.substring(1)
    return this
}

