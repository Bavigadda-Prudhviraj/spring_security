package com.prudhviraj.security.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Spring Security",
                version = "1.0.0",
                description = "This API encompasses all spring security features. For inquiries or support, please reach out using the contact information below.",
                contact = @Contact(
                        name = "Bavigadda Prudhviraj",
                        email = "prudhviraj729@gmail.com",
                        url = "https://github.com/Bavigadda-Prudhviraj"
                ),
                license = @License(
                        name = "API License",
                        url = "https://licenseurl.com"
                )
        )
)
public class OpenApiConfig {
    // Any additional configuration can be added here
}

