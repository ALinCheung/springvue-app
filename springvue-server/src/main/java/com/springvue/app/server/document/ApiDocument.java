package com.springvue.app.server.document;

import cn.hutool.core.collection.CollectionUtil;
import com.agile.sso.auth.doc.model.*;
import com.agile.sso.auth.model.OIDCOpenidConfig;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.*;

/**
 * 针对依赖接口和filter生成接口文档
 */
@Component
public class ApiDocument implements ApiListingScannerPlugin {

    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        List<ApiDescription> descs = new ArrayList<>();
        // OAuth模块
        Set<String> oauthSet = new HashSet<>(Collections.singletonList("OAuth模块"));
        descs.add(getAccessToken(oauthSet));
        descs.add(checkToken(oauthSet));
        descs.add(authorize(oauthSet));
        // OIDC模块
        Set<String> oauth2Set = new HashSet<>(Collections.singletonList("OIDC模块"));
        descs.add(getAccessTokenByOAuth2(oauth2Set));
        descs.add(checkTokenByOAuth2(oauth2Set));
        descs.add(authorizeByOAuth2(oauth2Set));
        descs.add(oidcJwks(oauth2Set));
        descs.add(oidcUserInfo(oauth2Set));
        descs.add(openidConfig(oauth2Set));
        return descs;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }

    private ApiDescription getAccessToken(Set<String> tags) {
        String desc = "获取token";
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(getParamter("client_id", "客户端名称", true));
        parameters.add(getParamter("client_secret", "客户端秘钥", true));
        parameters.add(getParamter("grant_type", "授权类型(password, refresh_token, authorization_code, ldap)", true));
        parameters.add(getParamter("username", "用户名"));
        parameters.add(getParamter("password", "密码"));
        parameters.add(getParamter("code", "授权码(authorization_code类型必填)"));
        parameters.add(getParamter("scope", "权限范围(read write)"));
        Operation getOperation = getOperation(HttpMethod.GET, desc, tags, parameters, new ModelRef(OAuthAccessToken.class.getSimpleName()));
        Operation postOperation = getOperation(HttpMethod.POST, desc, tags, parameters, new ModelRef(OAuthAccessToken.class.getSimpleName()));
        return new ApiDescription("oauth-endpoint", "/oauth/token", desc, Arrays.asList(getOperation, postOperation), false);
    }

    private ApiDescription checkToken(Set<String> tags) {
        String desc = "检查token";
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(getParamter("token", "令牌"));
        Operation getOperation = getOperation(HttpMethod.GET, desc, tags, parameters, new ModelRef(OAuthCheckToken.class.getSimpleName()));
        return new ApiDescription("oauth-endpoint", "/oauth/check_token", desc, Arrays.asList(getOperation), false);
    }

    private ApiDescription authorize(Set<String> tags) {
        String desc = "获取授权码";
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(getParamter("client_id", "客户端名称", true));
        parameters.add(getParamter("response_type", "响应类型(token, code)", true));
        parameters.add(getParamter("redirect_uri", "携带token或者code的重定向地址", true));
        parameters.add(getParamter("scope", "权限范围(read write)", true));
        parameters.add(getParamter("state", "请求状态编码"));
        Operation getOperation = getOperation(HttpMethod.GET, desc, null, tags, parameters, CollectionUtil.newHashSet(new ResponseMessageBuilder().code(HttpStatus.FOUND.value()).message(HttpStatus.FOUND.getReasonPhrase()).build()));
        return new ApiDescription("oauth-endpoint", "/oauth/authorize", desc, Arrays.asList(getOperation), false);
    }

    private ApiDescription getAccessTokenByOAuth2(Set<String> tags) {
        String desc = "获取OAuth2 token";
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(getParamter("client_id", "客户端名称", true));
        parameters.add(getParamter("client_secret", "客户端秘钥", true));
        parameters.add(getParamter("grant_type", "授权类型(refresh_token, authorization_code)", true));
        parameters.add(getParamter("code", "授权码", true));
        Operation getOperation = getOperation(HttpMethod.GET, desc, tags, parameters, new ModelRef(OAuth2AccessToken.class.getSimpleName()));
        Operation postOperation = getOperation(HttpMethod.POST, desc, tags, parameters, new ModelRef(OAuth2AccessToken.class.getSimpleName()));
        return new ApiDescription("oauth2-endpoint", "/oauth2/token", desc, Arrays.asList(getOperation, postOperation), false);
    }

    private ApiDescription checkTokenByOAuth2(Set<String> tags) {
        String desc = "检查OAuth2 token";
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(getParamter("token", "令牌"));
        Operation getOperation = getOperation(HttpMethod.GET, desc, tags, parameters, new ModelRef(OAuthCheckToken.class.getSimpleName()));
        return new ApiDescription("oauth2-endpoint", "/oauth2/check_token", desc, Arrays.asList(getOperation), false);
    }

    private ApiDescription authorizeByOAuth2(Set<String> tags) {
        String desc = "获取OAuth2授权码";
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(getParamter("client_id", "客户端名称", true));
        parameters.add(getParamter("response_type", "响应类型(code)", true));
        parameters.add(getParamter("redirect_uri", "携带code的重定向地址", true));
        parameters.add(getParamter("scope", "权限范围(read write openid)", true));
        parameters.add(getParamter("state", "请求状态编码"));
        Operation getOperation = getOperation(HttpMethod.GET, desc, null, tags, parameters, CollectionUtil.newHashSet(new ResponseMessageBuilder().code(HttpStatus.FOUND.value()).message(HttpStatus.FOUND.getReasonPhrase()).build()));
        return new ApiDescription("oauth2-endpoint", "/oauth2/authorize", desc, Arrays.asList(getOperation), false);
    }

    private ApiDescription oidcJwks(Set<String> tags) {
        String desc = "获取OIDC加密信息";
        String notes = "用于解析id_token";
        List<Parameter> parameters = new ArrayList<>();
        Operation getOperation = getOperation(HttpMethod.GET, desc, notes, tags, parameters, new ModelRef(OIDCJwks.class.getSimpleName()));
        return new ApiDescription("oauth2-endpoint", "/oauth2/jwks", desc, Arrays.asList(getOperation), false);
    }

    private ApiDescription oidcUserInfo(Set<String> tags) {
        String desc = "获取OIDC用户信息";
        String notes = "需要添加Authorization :Bearer Token";
        List<Parameter> parameters = new ArrayList<>();
        Operation getOperation = getOperation(HttpMethod.GET, desc, notes, tags, parameters, new ModelRef(OIDCUserInfo.class.getSimpleName()));
        return new ApiDescription("oauth2-endpoint", "/userinfo", desc, Arrays.asList(getOperation), false);
    }

    private ApiDescription openidConfig(Set<String> tags) {
        String desc = "获取OAuth2 OIDC配置信息";
        List<Parameter> parameters = new ArrayList<>();
        Operation getOperation = getOperation(HttpMethod.GET, desc, tags, parameters, new ModelRef(OIDCOpenidConfig.class.getSimpleName()));
        return new ApiDescription("oauth2-endpoint", "/.well-known/openid-configuration", desc, Arrays.asList(getOperation), false);
    }

    private Parameter getParamter(String name, String desc) {
        return getParamter(name, desc, false);
    }

    private Parameter getParamter(String name, String desc, Boolean required) {
        return new ParameterBuilder()
                .name(name)
                .description(desc)
                .type(new TypeResolver().resolve(String.class))
                .modelRef(new ModelRef("string"))
                .required(required)
                .build();
    }

    private Operation getOperation(HttpMethod method, String desc, Set<String> tags, List<Parameter> parameters, ModelRef modelRef) {
        return getOperation(method, desc, null, tags, parameters, CollectionUtil.newHashSet(new ResponseMessageBuilder().code(HttpStatus.OK.value()).message(HttpStatus.OK.getReasonPhrase()).responseModel(modelRef).build()));
    }

    private Operation getOperation(HttpMethod method, String desc, String notes, Set<String> tags, List<Parameter> parameters, ModelRef modelRef) {
        return getOperation(method, desc, notes, tags, parameters, CollectionUtil.newHashSet(new ResponseMessageBuilder().code(HttpStatus.OK.value()).message(HttpStatus.OK.getReasonPhrase()).responseModel(modelRef).build()));
    }

    private Operation getOperation(HttpMethod method, String desc, String notes, Set<String> tags, List<Parameter> parameters, Set<ResponseMessage> responseMessages) {
        return new OperationBuilder(new CachingOperationNameGenerator())
                .method(method)
                .uniqueId(desc)
                .summary(desc).tags(tags).notes(notes)
                .responseMessages(responseMessages)
                .consumes(CollectionUtil.newHashSet(MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE))
                .produces(CollectionUtil.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                .parameters(parameters)
                .build();
    }
}