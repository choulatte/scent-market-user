package com.choulatte.scentuser.application

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

    override fun loadUserByUsername(username: String): UserDTO? {
        return userRepository.findByUsername(username)?.toDTO()
    }
}