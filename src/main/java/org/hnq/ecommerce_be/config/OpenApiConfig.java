package org.hnq.ecommerce_be.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce T-shirt Print API")
                        .description("Backend bán áo in ảnh: Auth, Products, Orders, AI Design generation (HuggingFace/OpenAI)")
                        .version("v1")
                        .contact(new Contact().name("HNQ").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://example.com"));
    }
}
