package com.choulatte.scentuser.application

import com.choulatte.scentuser.dto.UserDTO
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService: UserDetailsService {
    fun login(user: UserDTO): Boolean?
    fun join(user: UserDTO): Long?
}