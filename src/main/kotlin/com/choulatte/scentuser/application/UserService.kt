package com.choulatte.scentuser.application

import com.choulatte.scentuser.dto.LoginReqDTO
import com.choulatte.scentuser.dto.UserDTO
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService: UserDetailsService {
    fun login(loginReqDTO: LoginReqDTO): UserDTO?
    fun join(userDTO: UserDTO): Long?
    fun updateUserInfo(userDTO: UserDTO): UserDTO?
    fun updateUserStatus(userDTO: UserDTO): UserDTO?
    fun withdraw(userDTO: UserDTO): Boolean?
}