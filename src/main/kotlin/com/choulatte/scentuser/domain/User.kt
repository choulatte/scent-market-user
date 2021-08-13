package com.choulatte.scentuser.domain

import com.choulatte.scentuser.dto.UserDTO
import com.choulatte.scentuser.exception.UserIllegalStateException
import java.io.Serializable
import java.util.*
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
    private var email: String? = null,

    @Column(name = "password", length = 300, nullable = false)
    private var password: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    private val roles: MutableList<String> = ArrayList(),

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "recorded_date", nullable = false)
    private var recordedDate: Date? = null,

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false)
    private var lastModifiedDate: Date? = null,

    @Column(name = "status_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private var statusType: StatusType? = null,

    @Column(name = "validity", nullable = false)
    private var validity: Boolean? = null,
) : Serializable {
    fun getId(): Long? = this.id

    fun getPassword(): String? = this.password

    fun toDTO(): UserDTO = UserDTO(
        id = this.id,
        username = this.username.orEmpty(),
        email = this.email,
        password = this.password.orEmpty(),
        roles = this.roles,
        recordedDate = this.recordedDate,
        lastModifiedDate = this.lastModifiedDate,
        statusType = this.statusType,
        validity = this.validity
    )

    fun update(userDTO: UserDTO): User {
        if (this.statusType == StatusType.WITHDRAWAL) throw UserIllegalStateException()

        this.email = userDTO.email
        this.lastModifiedDate = Date()

        return this
    }

    fun updateStatus(userStatusType: StatusType): User {
        if (this.statusType == StatusType.WITHDRAWAL) throw UserIllegalStateException()

        this.statusType = userStatusType
        this.lastModifiedDate = Date()

        return this
    }

    enum class StatusType {
        NORMAL,
        LOCKED,
        EXPIRED,
        WITHDRAWAL
    }
}
