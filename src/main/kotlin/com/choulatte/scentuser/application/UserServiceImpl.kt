package com.choulatte.scentuser.application

import com.choulatte.scentuser.domain.User
import com.choulatte.scentuser.domain.UserStatusType
import com.choulatte.scentuser.dto.LoginReqDTO
import com.choulatte.scentuser.dto.UserDTO
import com.choulatte.scentuser.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val userRepository: UserRepository
) : UserService {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun login(loginReqDTO: LoginReqDTO): UserDTO? {
        val user: UserDTO = loadUserByUsername(loginReqDTO.username)!!

        if (passwordEncoder.matches(loginReqDTO.password, user.password)) {
            return user.sealPassword()
        }

        return null
    }

    override fun join(userDTO: UserDTO): Long? {
        userDTO.setDefaultRole().encodePassword { password -> passwordEncoder.encode(password) }

        return userRepository.save(userDTO.toEntity()).getId()
    }

    override fun updateUserInfo(userDTO: UserDTO): UserDTO? {
        val user: User = getUser(userDTO.username)!!

        if (passwordEncoder.matches(userDTO.password, user.getPassword())) {
            return userRepository.save(user.update(userDTO)).toDTO().sealPassword()
        }

        return null
    }

    override fun updateUserStatus(userDTO: UserDTO): UserDTO? {
        val user: User = getUser(userDTO.username)!!

        if (passwordEncoder.matches(userDTO.password, user.getPassword())) {
            return userRepository.save(user.updateStatus(userDTO.statusType!!)).toDTO().sealPassword()
        }

        return null
    }

    override fun withdraw(userDTO: UserDTO): Boolean? {
        val user: User = getUser(userDTO.username)!!

        if (passwordEncoder.matches(userDTO.password, user.getPassword())) {
            userRepository.save(user.updateStatus(UserStatusType.WITHDRAWAL)).toDTO().sealPassword()

            return true
        }

        return false
    }

    override fun loadUserByUsername(username: String): UserDTO? {
        return getUser(username)?.toDTO()
    }

    private fun getUser(username: String): User? {
        return userRepository.findByUsername(username)
    }
}