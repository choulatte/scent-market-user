package com.choulatte.scentuser.controller

import com.choulatte.scentuser.application.UserService
import com.choulatte.scentuser.config.JwtTokenProvider
import com.choulatte.scentuser.dto.LoginReqDTO
import com.choulatte.scentuser.dto.TokenRespDTO
import com.choulatte.scentuser.dto.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/users"])
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @PostMapping(value = [""])
    fun login(@RequestBody loginReqDTO: LoginReqDTO): ResponseEntity<TokenRespDTO> {
        val userDTO: UserDTO? = userService.login(loginReqDTO)

        if (userDTO != null) {
            return ResponseEntity.ok(jwtTokenProvider.createToken(userDTO.username, userDTO.roles))
        }

        return ResponseEntity.notFound().build()
    }

    @PostMapping(value = ["/join"])
    fun join(@RequestBody userDTO: UserDTO): ResponseEntity<Long> {
        return ResponseEntity.ok(userService.join(userDTO))
    }

    @PutMapping(value = [""])
    fun updateUserInfo(@RequestBody userDTO: UserDTO): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.updateUserInfo(userDTO))
    }

    @DeleteMapping(value = [""])
    fun withdraw(@RequestBody userDTO: UserDTO): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.withdraw(userDTO))
    }

}