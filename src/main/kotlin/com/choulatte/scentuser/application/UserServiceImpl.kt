package com.choulatte.scentuser.application

import com.choulatte.pay.grpc.AccountServiceGrpc
import com.choulatte.pay.grpc.AccountServiceOuterClass
import com.choulatte.product.grpc.ProductServiceGrpc
import com.choulatte.product.grpc.ProductServiceOuterClass
import com.choulatte.scentuser.domain.User
import com.choulatte.scentuser.domain.UserStatusType
import com.choulatte.scentuser.dto.LoginReqDTO
import com.choulatte.scentuser.dto.UserDTO
import com.choulatte.scentuser.repository.UserRepository
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    @Qualifier(value = "pay")
    private val payChannel: ManagedChannel,
    @Qualifier(value = "product")
    private val productChannel: ManagedChannel
) : UserService {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    private val accountServiceStub: AccountServiceGrpc.AccountServiceStub = AccountServiceGrpc.newStub(payChannel)
    private val productServiceStub: ProductServiceGrpc.ProductServiceStub = ProductServiceGrpc.newStub(productChannel)

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

            val countDownLatch: CountDownLatch = CountDownLatch(2)
            var isAnyRequestNotProcessed: Boolean = false

            val accountStreamObserver: StreamObserver<AccountServiceOuterClass.AccountsPendingResponse> = object: StreamObserver<AccountServiceOuterClass.AccountsPendingResponse> {
                override fun onNext(value: AccountServiceOuterClass.AccountsPendingResponse?) {
                    if (value?.result == AccountServiceOuterClass.AccountsPendingResponse.Result.OK) {
                        return
                    }

                    isAnyRequestNotProcessed = true
                }

                override fun onError(t: Throwable?) {
                    isAnyRequestNotProcessed = true
                }

                override fun onCompleted() {
                    countDownLatch.countDown()
                }

            }

            val productStreamObserver: StreamObserver<ProductServiceOuterClass.ProductsPendingResponse> = object: StreamObserver<ProductServiceOuterClass.ProductsPendingResponse> {
                override fun onNext(value: ProductServiceOuterClass.ProductsPendingResponse?) {
                    if (value?.result == ProductServiceOuterClass.ProductsPendingResponse.Result.OK) {
                        return
                    }

                    isAnyRequestNotProcessed = true
                }

                override fun onError(t: Throwable?) {
                    isAnyRequestNotProcessed = true
                }

                override fun onCompleted() {
                    countDownLatch.countDown()
                }

            }

            accountServiceStub.doUserAccountsPending(AccountServiceOuterClass.AccountsPendingRequest.newBuilder().setUserId(user.getId()!!).build(), accountStreamObserver)
            productServiceStub.doUserProductsPending(ProductServiceOuterClass.ProductsPendingRequest.newBuilder().setUserId(user.getId()!!).build(), productStreamObserver)

            try {
                countDownLatch.await(500, TimeUnit.MILLISECONDS)
            } catch (ignored: InterruptedException) { }

            if (isAnyRequestNotProcessed.not()) {
                userRepository.save(user)

                return true
            }

            accountServiceStub.undoUserAccountsPending(AccountServiceOuterClass.AccountsPendingRequest.newBuilder().setUserId(user.getId()!!).build(), null)
            productServiceStub.undoUserProductsPending(ProductServiceOuterClass.ProductsPendingRequest.newBuilder().setUserId(user.getId()!!).build(), null)
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