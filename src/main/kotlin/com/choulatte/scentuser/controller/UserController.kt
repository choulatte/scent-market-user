package com.choulatte.scentuser.controller

import com.choulatte.scentuser.service.UserService
import com.choulatte.scentuser.config.JwtTokenProvider
import com.choulatte.scentuser.dto.LoginDTO
import com.choulatte.scentuser.dto.TokenDTO
import com.choulatte.scentuser.dto.UserDTO
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/users"])
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @PostMapping(value = [""])
    @ApiOperation(value = "사용자 인증", notes = "사용자 인증 요청을 처리하고 인증 토큰을 반환합니다.")
    fun login(@RequestBody loginDTO: LoginDTO): ResponseEntity<TokenDTO> {
        val userDTO: UserDTO? = userService.login(loginDTO)

        if (userDTO != null) {
            return ResponseEntity.ok(jwtTokenProvider.createToken(userDTO.username, userDTO.roles))
        }

        return ResponseEntity.notFound().build()
    }

    @PostMapping(value = ["/join"])
    @ApiOperation(value = "사용자 등록", notes = "새로운 사용자를 등록합니다.")
    fun join(@RequestBody userDTO: UserDTO): ResponseEntity<Long> = ResponseEntity.ok(userService.join(userDTO))

    @PutMapping(value = [""])
    @ApiOperation(value = "사용자 정보 수정", notes = "등록된 사용자 정보를 수정합니다.")
    fun updateUserInfo(@RequestBody userDTO: UserDTO): ResponseEntity<UserDTO> = ResponseEntity.ok(userService.updateUserInfo(userDTO))

    @DeleteMapping(value = [""])
    @ApiOperation(value = "사용자 탈퇴", notes = "등록된 사용자를 탈퇴 처리합니다.")
    fun withdraw(@RequestBody userDTO: UserDTO): ResponseEntity<Boolean> = ResponseEntity.ok(userService.withdraw(userDTO))
}