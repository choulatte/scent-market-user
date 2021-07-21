package com.choulatte.scentuser.application

import com.choulatte.pay.grpc.AccountServiceGrpc
import com.choulatte.pay.grpc.PaymentServiceGrpc
import com.choulatte.product.grpc.ProductServiceGrpc
import com.choulatte.scentuser.domain.User
import com.choulatte.scentuser.domain.UserStatusType
import com.choulatte.scentuser.dto.LoginReqDTO
import com.choulatte.scentuser.dto.UserDTO
import com.choulatte.scentuser.repository.UserRepository
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val userRepository: UserRepository
) : UserService {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    private val payChannel: ManagedChannel = ManagedChannelBuilder
        .forAddress("172.20.10.3", 8090)
        .usePlaintext().build()
    private val productChannel: ManagedChannel = ManagedChannelBuilder
        .forAddress("172.20.10.4", 8091)
        .usePlaintext().build()
    private val accountStub: AccountServiceGrpc.AccountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(payChannel)
    private val productStub: ProductServiceGrpc.ProductServiceBlockingStub = ProductServiceGrpc.newBlockingStub(productChannel)

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
            user.updateStatus(UserStatusType.WITHDRAWAL)

            // TODO: Pending and invalidating user accounts and products using gRPC client Stubs

            userRepository.save(user)

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