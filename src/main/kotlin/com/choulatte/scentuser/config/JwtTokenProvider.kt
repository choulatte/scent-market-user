package com.choulatte.scentuser.config

import com.choulatte.scentuser.dto.TokenDTO
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
    private var secretKey: String = "choulatte-jwt-token-secret-key",
    val tokenValidTime: Long = 30 * 60 * 1000L
) {
    init {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    fun createToken(username: String?, roles: List<String?>): TokenDTO? {
        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = roles
        val now = Date()
        return TokenDTO(validationToken = Jwts.builder().setClaims(claims).setIssuedAt(now)
            .setExpiration(Date(now.time + tokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey).compact(),
            refreshToken = "refresh token is not supported.")
    }

    fun getUserIdx(token: String?): String? = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject

    fun resolveToken(request: HttpServletRequest): String? = request.getHeader("Authorization")

    fun validateToken(token: String?): Boolean {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}