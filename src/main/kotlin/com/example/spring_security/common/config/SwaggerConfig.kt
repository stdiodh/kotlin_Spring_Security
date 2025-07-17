package com.example.spring_security.common.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Configuration
class SwaggerConfig {

    @Bean
    fun openApi() : OpenAPI = OpenAPI()
        .components(Components())
        .info(apiInfo())

    private fun apiInfo() : Info = Info()
        .title("웹서버 API 명세서")
        .description("동아리 스터디에서 만든 서버의 API 명세서입니다.")
        .version("1.0.0")
}