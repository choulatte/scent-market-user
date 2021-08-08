package com.choulatte.scentuser.dto

import com.choulatte.scentuser.domain.User
import com.choulatte.scentuser.domain.UserStatusType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.stream.Collectors

data class UserDTO(
    val id: Long?,
    private val username: String,
    var email: String?,
    private var password: String,
    var roles: MutableList<String>,
    var recordedDate: Date?,
    var lastModifiedDate: Date?,
    var statusType: UserStatusType?,
    var validity: Boolean?
) : UserDetails {
    override fun getPassword(): String = this.password

    override fun getUsername(): String = this.username

    override fun getAuthorities(): Collection<GrantedAuthority?> = roles.stream()
        .map { role: String? -> SimpleGrantedAuthority(role) }
        .collect(Collectors.toList())

    override fun isAccountNonExpired(): Boolean = this.statusType != UserStatusType.EXPIRED && this.validity == true

    override fun isAccountNonLocked(): Boolean = this.statusType != UserStatusType.LOCKED && this.validity == true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = this.statusType == UserStatusType.NORMAL && this.validity == true

    fun encodePassword(encode: (String?) -> String): UserDTO {
        this.password = encode(this.password)

        return this
    }

    fun sealPassword(): UserDTO {
        this.password = "SEALED"

        return this
    }

    fun setDefaultRole(): UserDTO {
        this.roles.clear()
        this.roles.add("USER_ROLE")

        return this
    }

    fun toEntity(): User = User(
        username = this.username,
        email = this.email,
        password = this.password,
        roles = this.roles,
        recordedDate = Date(),
        lastModifiedDate = Date(),
        statusType = UserStatusType.NORMAL,
        validity = true
    )

    fun toLoginDTO(): LoginDTO = LoginDTO(
        username = this.username,
        password = this.password
    )
}
