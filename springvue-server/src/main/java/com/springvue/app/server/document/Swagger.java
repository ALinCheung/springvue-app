package com.springvue.app.server.document;

import com.fasterxml.classmate.TypeResolver;
import com.springvue.app.server.document.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class Swagger {

    @Value("${server.servlet.context-path}")
    private String context;

    @Autowired
    private TypeResolver typeResolver;

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Web示例应用")
                        .description("# swagger-bootstrap-ui-demo RESTful APIs")
                        .termsOfServiceUrl("http://www.xx.com/")
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("springvue-server")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.springvue.app.server.controller"))
                .paths(PathSelectors.any())
                .build()
                // 添加Swagger-model
                .additionalModels(typeResolver.resolve(OAuthAccessToken.class))
                .additionalModels(typeResolver.resolve(OAuthCheckToken.class))
                .additionalModels(typeResolver.resolve(OAuth2AccessToken.class))
                .additionalModels(typeResolver.resolve(OIDCJwks.class))
                .additionalModels(typeResolver.resolve(OIDCUserInfo.class))
                .additionalModels(typeResolver.resolve(OIDCOpenidConfig.class));
    }

    /**
     * 解决context-path重复问题
     *
     * @return
     */
    @Bean
    @Primary
    public PathProvider pathProvider() {
        return new DefaultPathProvider() {
            @Override
            public String getOperationPath(String operationPath) {
                operationPath = operationPath.replaceFirst(context, "/");
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
                return Paths.removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
            }

            @Override
            public String getResourceListingPath(String groupName, String apiDeclaration) {
                apiDeclaration = super.getResourceListingPath(groupName, apiDeclaration);
                return apiDeclaration;
            }
        };
    }
}
