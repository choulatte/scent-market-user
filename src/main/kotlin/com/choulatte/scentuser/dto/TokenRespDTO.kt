package com.choulatte.scentuser.dto

data class TokenRespDTO(
    val validationToken: String?,
    val refreshToken: String?
) {

}