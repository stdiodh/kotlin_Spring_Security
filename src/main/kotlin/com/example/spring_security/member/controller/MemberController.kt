package com.example.spring_security.member.controller

import com.example.spring_security.common.dto.BaseResponse
import com.example.spring_security.common.dto.CustomUser
import com.example.spring_security.common.dto.TokenInfo
import com.example.spring_security.member.dto.LoginDto
import com.example.spring_security.member.dto.MemberRequestDto
import com.example.spring_security.member.dto.MemberResponseDto
import com.example.spring_security.member.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@Tag(name = "회원가입 Api 컨트롤러", description = "회원가입과 로그인 API 명세서 입니다.")

@RestController
@RequestMapping("/api/member")
class MemberController (
    private val memberService: MemberService
){
    @Operation(
        summary = "회원가입",
        description = "회원 정보를 입력받아 신규 회원을 등록합니다."
    )
    @PostMapping("/join")
    private fun signUp(@Valid @RequestBody memberRequestDto: MemberRequestDto)
            : ResponseEntity<BaseResponse<String>>{
        val result = memberService.signUp(memberRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse(data = result))
    }

    @Operation(
        summary = "로그인",
        description = "아이디와 비밀번호를 입력받아 인증을 수행하고 JWT 토큰을 반환합니다."
    )
    @PostMapping("/login")
    private fun login(@Valid @RequestBody loginDto: LoginDto)
            : ResponseEntity<BaseResponse<TokenInfo>> {
        val tokenInfo = memberService.login(loginDto)
        return ResponseEntity.status(HttpStatus.OK)
            .body(BaseResponse(data = tokenInfo))
    }

    @Operation(
        summary = "내 정보 조회",
        description = "회원 정보를 조회합니다."
    )
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/info")
    private fun searchMyInfo()
    : ResponseEntity<BaseResponse<MemberResponseDto>> {
        val memberId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id
        val result = memberService.searchMyInfo(memberId)
        return ResponseEntity.status(HttpStatus.OK)
            .body(BaseResponse(data = result))
    }

    @Operation(
        summary = "로그아웃",
        description = "로그인한 사용자의 토큰을 반환합니다."
    )
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/logout")
    private fun logout() : ResponseEntity<BaseResponse<String>> {
        val memberId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id
        val result = memberService.logout(memberId)
        return ResponseEntity.status(HttpStatus.OK).body(
            BaseResponse(data = result)
        )
    }
}
