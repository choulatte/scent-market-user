package com.choulatte.scentuser.controller

import com.choulatte.scentuser.application.UserService
import com.choulatte.scentuser.config.JwtTokenProvider
import com.choulatte.scentuser.dto.LoginDTO
import com.choulatte.scentuser.dto.TokenDTO
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
    fun login(@RequestBody loginDTO: LoginDTO): ResponseEntity<TokenDTO> {
        val userDTO: UserDTO? = userService.login(loginDTO)

        if (userDTO != null) {
            return ResponseEntity.ok(jwtTokenProvider.createToken(userDTO.username, userDTO.roles))
        }

        return ResponseEntity.notFound().build()
    }

    @PostMapping(value = ["/join"])
    fun join(@RequestBody userDTO: UserDTO): ResponseEntity<Long> = ResponseEntity.ok(userService.join(userDTO))

    @PutMapping(value = [""])
    fun updateUserInfo(@RequestBody userDTO: UserDTO): ResponseEntity<UserDTO> = ResponseEntity.ok(userService.updateUserInfo(userDTO))

    @DeleteMapping(value = [""])
    fun withdraw(@RequestBody userDTO: UserDTO): ResponseEntity<Boolean> = ResponseEntity.ok(userService.withdraw(userDTO))
}