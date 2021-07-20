package com.choulatte.scentuser.dto

import com.choulatte.scentuser.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

data class UserDTO(
    val id: Long?,
    private val username: String,
    val email: String?,
    private var password: String,
    var roles: MutableList<String>
) : UserDetails {
    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return roles.stream()
            .map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }
            .collect(Collectors.toList())
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun encodePassword(encode: (String?) -> String): UserDTO {
        this.password = encode(this.password)

        return this
    }

    fun sealPassword(): UserDTO {
        this.password = "SEALING"

        return this
    }

    fun setDefaultRole(): UserDTO {
        this.roles.clear()
        this.roles.add("USER_ROLE")

        return this
    }

    fun toEntity(): User {
        return User(
            username = this.username,
            email = this.email,
            password = this.password,
            roles = this.roles)
    }
}
