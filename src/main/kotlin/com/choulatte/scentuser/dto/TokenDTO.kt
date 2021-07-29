package com.choulatte.scentuser.dto

data class TokenDTO(
    val validationToken: String?,
    val refreshToken: String?
) {
}