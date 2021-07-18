package com.choulatte.scentuser.domain

import com.choulatte.scentuser.dto.UserDTO
import javax.persistence.*

@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private val id: Long? = null,

    @Column(name = "username", length = 30, nullable = false, unique = true)
    private val username: String? = null,

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private val email: String? = null,

    @Column(name = "password", length = 300, nullable = false)
    private val password: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    private val roles: MutableList<String> = ArrayList()
) {
    fun getId(): Long? {
        return this.id
    }

    fun toDTO(): UserDTO {
        return UserDTO(
            id = this.id,
            username = this.username,
            email = this.email,
            password = this.password,
            roles = this.roles
        )
    }
}
