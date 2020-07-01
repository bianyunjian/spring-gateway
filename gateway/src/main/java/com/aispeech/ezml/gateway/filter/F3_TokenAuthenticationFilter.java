package com.aispeech.ezml.gateway.filter;

import com.aispeech.ezml.gateway.base.BaseResponse;
import com.aispeech.ezml.gateway.base.ErrorCode;
import com.aispeech.ezml.gateway.base.UserTokenInfo;
import com.aispeech.ezml.gateway.support.PrivilegeChecker;
import com.aispeech.ezml.gateway.support.TokenManager;
import com.aispeech.ezml.gateway.support.TokenUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 对客户端header 中的 Authorization 信息进行认证
 */
@Component
public class F3_TokenAuthenticationFilter implements WebFilter {
    @Value("${app.filter.enable-token-auth}")
    public boolean ENABLE_TOKEN_AUTH = true;
    @Value("${app.filter.enable-token-auth-check-cache}")
    public boolean ENABLE_TOKEN_AUTH_CHECK_CACHE = true;

    @Autowired
    private TokenManager TokenManager;
    @Autowired
    private PrivilegeChecker PrivilegeChecker;

    private List<String> tokenAuthIgnorePathList = new ArrayList<>();


    public F3_TokenAuthenticationFilter(@Value("${app.filter.token-auth-ignore-path}") String tokenAuthIgnorePath) {
        if (StringUtils.isEmpty(tokenAuthIgnorePath) == false) {
            String[] array = tokenAuthIgnorePath.split(",");
            for (String path :
                    array) {
                tokenAuthIgnorePathList.add(path.trim());
            }
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        ServerHttpResponse response = exchange.getResponse();

        String requestMethod = request.getMethodValue();
        String requestPath = request.getPath().toString();

        try {
            //检查该请求是否需要验证
            if (checkNeedAuth(requestMethod, requestPath) == false) {
                System.out.println("pass through with no auth for " + requestMethod + ":" + requestPath);
                return chain.filter(exchange);
            }

            //获取header中的Authorization
            String header = request.getHeaders().getFirst(TokenUtil.Authorization_Header);
            if (header == null || !header.startsWith(TokenUtil.Bearer_Prefix)) {
                throw new RuntimeException("请求头中Authorization信息为空");
            }
            //截取Authorization Bearer
            String token = header.substring(TokenUtil.Bearer_Prefix.length());

            boolean tokenValid = false;
            String validateMessage = "";
            if (!StringUtils.isEmpty(token)) {

                //解析token
                UserTokenInfo tokenInfo = TokenUtil.getInstance().decodeToken(token);
                if (tokenInfo != null && tokenInfo.isValid()) {
                    //检查token信息是否有效
                    boolean checkMatchCache = false;
                    if (ENABLE_TOKEN_AUTH_CHECK_CACHE) {
                        //检查token是否在网关缓存中, 如果不在缓存中，说明已经失效
                        checkMatchCache = TokenManager.checkAccessToken(tokenInfo.getUserId(), token);
                    } else {
                        checkMatchCache = true;
                    }
                    if (checkMatchCache) {

                        if (PrivilegeChecker.checkUserPrivilegeForRequestPath(tokenInfo, requestPath)) {
                            //把用户信息设置到header中，传递给后端服务
                            mutate.header("userId", tokenInfo.getUserId());

                            String userNameBase64Str = Base64.getEncoder().encodeToString(tokenInfo.getUserName().getBytes()); //header中有中文必须编码
                            mutate.header("userNameBase64", userNameBase64Str);
                            mutate.build();
                            tokenValid = true;
                        } else {
                            validateMessage = "用户没有调用当前接口的权限";
                        }
                    } else {
                        validateMessage = "用户token已在网关缓存中失效";
                    }
                } else {
                    //token 无效或者过期
                    validateMessage = "用户token无效或者过期";
                }
            }

            if (tokenValid == false) {
                //token无效
                System.out.println(validateMessage);
                DataBuffer bodyDataBuffer = responseErrorInfo(response, HttpStatus.UNAUTHORIZED.toString(), "未通过鉴权的请求," + validateMessage);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(bodyDataBuffer));
            } else {
                System.out.println("鉴权验证通过");
                ServerHttpRequest build = mutate.build();
                return chain.filter(exchange.mutate().request(build).build());
            }
        } catch (Exception ex) {
            System.out.println("鉴权遇到异常:" + ex.getMessage());
            DataBuffer bodyDataBuffer = responseErrorInfo(response, HttpStatus.UNAUTHORIZED.toString(), ex.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.just(bodyDataBuffer));
        }


    }


    /**
     * check if need auth for the requestPath
     *
     * @param requestMethod
     * @param requestPath
     * @return
     */
    private boolean checkNeedAuth(String requestMethod, String requestPath) {
        if (ENABLE_TOKEN_AUTH == false) return false;

        if (requestMethod.equalsIgnoreCase(HttpMethod.OPTIONS.toString())) {
            return false;
        }

        if (tokenAuthIgnorePathList.size() > 0) {
            for (String ignorePathPattern :
                    tokenAuthIgnorePathList) {
                if (ignorePathPattern.contains("*")) {
                    String matchPattern = ignorePathPattern.replace("*", "");
                    if (requestPath.contains(matchPattern)) {
                        System.out.println("match token auth ignore path with [" + requestPath + "]");
                        return false;
                    }
                } else {
                    if (ignorePathPattern.equalsIgnoreCase(requestPath)) {
                        System.out.println("match token auth ignore path with [" + requestPath + "]");
                        return false;
                    }
                }
            }
        }


        return true;
    }


    /**
     * 自定义返回错误信息
     *
     * @param response
     * @param status
     * @param message
     * @return
     */
    public DataBuffer responseErrorInfo(ServerHttpResponse response, String status, String message) {
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        BaseResponse<String> resp = new BaseResponse<>();
        resp.fail(status + "-" + message, ErrorCode.UNAUTHORIZED);

        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes());
        return bodyDataBuffer;

    }


}