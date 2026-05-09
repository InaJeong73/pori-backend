package com.pori.global.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Pori API", description = "수도권 대학생 익명 포트폴리오 커뮤니티 PORI 백엔드 API", version = "1.0.0"),
        servers = @Server(url = "/", description = "Default server"))
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("로그인 또는 회원가입 후 발급받은 accessToken을 입력하세요. Bearer 접두사는 Swagger UI가 자동으로 붙입니다.");

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Pori API")
                        .version("1.0.0")
                        .description("""
                                ## Pori 백엔드 API 문서

                                수도권 대학생을 위한 익명 포트폴리오 커뮤니티 PORI의 백엔드 API입니다.

                                ### 인증이 필요한 API 사용 방법
                                1. `POST /auth/register`로 회원가입하거나 `POST /auth/login`으로 로그인합니다.
                                2. 응답의 `accessToken` 값을 복사합니다.
                                3. Swagger UI 우측 상단 Authorize 버튼을 클릭합니다.
                                4. `accessToken` 값을 입력한 뒤 Authorize를 클릭합니다.
                                5. 이후 자물쇠 아이콘이 붙은 API를 호출할 수 있습니다.

                                ### 인증 없이 호출 가능한 API
                                - `GET /tech-stack/options` - 기술 스택 옵션 목록
                                - `POST /auth/register` - 회원가입
                                - `POST /auth/login` - 로그인
                                - `POST /auth/refresh` - 토큰 갱신
                                - `GET /users/handles/check` - 핸들 중복 확인
                                - `GET /health` - 서버 상태 확인

                                ### 주요 개념
                                - `handle`: 유저 고유 식별자입니다. 영문, 숫자, 언더스코어 3~20자를 사용합니다.
                                - `privacyLevel`: `1`은 전체 공개, `2`는 링크 공유 공개, `3`은 비공개입니다.
                                - Give-to-Get: 일부 피드/채팅 기능은 본인 포트폴리오가 `PUBLISHED` 상태이고 기준 점수를 충족해야 사용할 수 있습니다.
                                - `accessToken` 유효 기간은 15분, `refreshToken` 유효 기간은 14일입니다.
                                """)
                        .contact(new Contact().name("PORI Team")))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
    }
}
