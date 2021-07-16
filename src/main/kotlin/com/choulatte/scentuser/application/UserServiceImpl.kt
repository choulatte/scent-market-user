package com.choulatte.scentuser.application

import com.choulatte.scentuser.dto.UserDTO
import com.choulatte.scentuser.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val userRepository: UserRepository
) : UserService {
    lateinit var passwordEncoder: PasswordEncoder

    override fun join(user: UserDTO): Long? {
        user.encodePassword { password -> passwordEncoder.encode(password) }

        return userRepository.save(user.toEntity()).getId()
    }

    override fun loadUserByUsername(username: String): UserDTO? {
        return userRepository.findByUsername(username)?.toDTO()
    }
}