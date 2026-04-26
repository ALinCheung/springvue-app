package com.springvue.app.server.document;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Web示例应用")
                        .description("# RESTful APIs")
                        .version("1.0")
                        .contact(new Contact().name("xx.com").url("http://www.xx.com/")));
    }
}
