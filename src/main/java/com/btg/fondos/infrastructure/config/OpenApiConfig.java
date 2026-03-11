package com.btg.fondos.infrastructure.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BTG Pactual - API de Fondos de Inversión")
                        .version("1.0.0")
                        .description("API REST para la gestión de fondos de inversión. "
                                + "Permite a los clientes suscribirse, cancelar suscripciones "
                                + "y consultar su historial de transacciones.")
                        .contact(new Contact()
                                .name("BTG Pactual")
                                .url("https://www.btgpactual.com.co")));
    }
}
