package com.wms.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration with JWT Bearer authentication.
 */
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT token for authentication. Login via /auth/login to get token.")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WMS API")
                        .version("1.0.0")
                        .description("Warehouse Management System REST API.<br><br>" +
                                "**Authentication:** Most endpoints require a JWT token. " +
                                "Use `/auth/login` to obtain a token, then use the Authorize button to set it.<br><br>"
                                +
                                "**Default Admin:** username=`admin`, password=`admin123`")
                        .contact(new Contact()
                                .name("WMS Team")
                                .email("wms@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
