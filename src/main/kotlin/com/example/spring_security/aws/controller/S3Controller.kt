package com.example.spring_security.aws.controller

import com.example.spring_security.aws.dto.S3RequestDto
import com.example.spring_security.aws.service.S3Service
import com.example.spring_security.common.dto.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/aws/s3")
class S3Controller(
    private val s3Service: S3Service
) {

    @Operation(
        summary = "단일 이미지 업로드",
        description = "단일 이미지를 AWS S3에 업로드하고, 업로드된 이미지 URL을 반환합니다."
    )
    @PostMapping("/imageUpload", consumes = ["multipart/form-data"])
    fun imageUpload(
        @Parameter(description = "업로드할 이미지 파일", required = true)
        @RequestPart(value = "image", required = true) image: MultipartFile
    ): ResponseEntity<BaseResponse<String>> {
        val result = s3Service.uploadImage(image)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse(data = result))
    }

    @Operation(
        summary = "다중 이미지 업로드",
        description = "여러 이미지를 AWS S3에 업로드하고, 업로드된 이미지 URL 리스트를 반환합니다."
    )
    @PostMapping("/imagesUpload", consumes = ["multipart/form-data"])
    fun imagesUpload(
        @Parameter(description = "업로드할 이미지 파일 리스트", required = true)
        @RequestPart(value = "image", required = true) images: List<MultipartFile>
    ): ResponseEntity<BaseResponse<List<String>>> {
        val result = s3Service.uploadImages(images)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse(data = result))
    }

    @Operation(
        summary = "이미지 삭제",
        description = "S3에 저장된 이미지를 파일명으로 삭제합니다."
    )
    @DeleteMapping("/delete")
    fun deleteImage(
        @RequestBody s3RequestDto: S3RequestDto
    ): ResponseEntity<BaseResponse<String>> {
        val result = s3Service.deleteImage(s3RequestDto.imageFileName)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }
}