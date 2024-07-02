package me.henriquelluiz.utils

import java.security.MessageDigest

fun generateETag(content: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(content.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}