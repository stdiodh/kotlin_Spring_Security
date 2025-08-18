package com.example.spring_security.aws.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class S3Service (
    private val amazonS3: AmazonS3
) {
    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucketName: String

    fun uploadImageS3(image: MultipartFile): String {
        if (image.isEmpty || image.originalFilename == null) {
            throw AmazonS3Exception("파일이 비었습니다!")
        }

        val originalFilename = image.originalFilename!!
        val extension = originalFilename.substringAfterLast('.', "")
        if (extension.isBlank()) {
            throw AmazonS3Exception("파일 확장자가 없습니다.")
        }

        val s3FileName = "${UUID.randomUUID()}.$extension"
        val inputStream = image.inputStream

        val metadata = ObjectMetadata().apply {
            this.contentType = image.contentType
            this.contentLength = image.size
        }

        if (!::bucketName.isInitialized || bucketName.isBlank()) {
            throw AmazonS3Exception("버킷 이름이 설정되지 않았습니다.")
        }

        try {
            val putObjectRequest = PutObjectRequest(bucketName, "image/$s3FileName", inputStream, metadata)
            amazonS3.putObject(putObjectRequest)
        } catch (e: Exception) {
            // 예외 로그 출력
            println("S3 업로드 에러: ${e.message}")
            throw AmazonS3Exception("이미지 업로드 에러가 발생했습니다.", e)
        } finally {
            inputStream.close()
        }

        return amazonS3.getUrl(bucketName, "image/$s3FileName").toString()
    }

    fun uploadImage(image: MultipartFile): String {
        return uploadImageS3(image)
    }

    fun uploadImages(images: List<MultipartFile>): List<String> {
        val imageUrls = mutableListOf<String>()
        for (image in images) {
            val imageUrl = uploadImageS3(image)
            imageUrls.add(imageUrl)
        }
        return imageUrls
    }

    fun deleteImage(keyOrUrl: String): String {
        if (!::bucketName.isInitialized || bucketName.isBlank()) {
            throw AmazonS3Exception("버킷 이름이 설정되지 않았습니다.")
        }

        val key = if (keyOrUrl.startsWith("http")) {
            keyOrUrl.substringAfter(".amazonaws.com/")
        } else {
            keyOrUrl
        }

        amazonS3.deleteObject(bucketName, key)
        return "이미지가 삭제되었습니다."
    }
}
