package com.aispeech.ezml.gateway.filter;

import com.aispeech.ezml.gateway.base.BaseResponse;
import com.aispeech.ezml.gateway.base.ErrorCode;
import com.aispeech.ezml.gateway.base.UserTokenInfo;
import com.aispeech.ezml.gateway.support.TokenManager;
import com.aispeech.ezml.gateway.support.TokenUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
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
import java.util.List;

/**
 * 对客户端header 中的 Authorization 信息进行认证
 */
@Component
public class F3_TokenAuthenticationFilter implements WebFilter {
    @Value("${app.filter.enable-token-auth}")
    public boolean ENABLE_TOKEN_AUTH = true;

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

        try {
            //检查是否需要验证
            if (checkNeedAuth(request) == false) {
                System.out.println("pass through with no auth");
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
            if (!StringUtils.isEmpty(token)) {

                //有token,处理token
                UserTokenInfo tokenInfo = TokenUtil.decodeToken(token);
                if (tokenInfo != null && tokenInfo.isValid()) {
                    //检查token是否在缓存中, 如果不在缓存中，说明已经失效
                    boolean checkPass = TokenManager.checkAccessToken(tokenInfo.getUserId(), token);
                    if (checkPass) {
                        //把用户信息设置到header中，传递给后端服务
                        mutate.header("userId", tokenInfo.getUserId());
                        mutate.header("userName", tokenInfo.getUserName());
                        mutate.build();
                        tokenValid = true;
                    }
                } else {
                    //TODO token 无效或者过期的处理
                }
            }

            if (tokenValid == false) {
                //token无效
                System.out.println("token无效");
                DataBuffer bodyDataBuffer = responseErrorInfo(response, HttpStatus.UNAUTHORIZED.toString(), "未通过鉴权的请求");
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

    private boolean checkNeedAuth(ServerHttpRequest request) {
        if (ENABLE_TOKEN_AUTH == false) return false;

        String method=request.getMethodValue();
        if(method.equalsIgnoreCase(HttpMethod.OPTIONS.toString())){
            return false;
        }

        String path = request.getPath().toString();
        if (tokenAuthIgnorePathList.size() > 0) {
            for (String ignorePathPattern :
                    tokenAuthIgnorePathList) {
                if (ignorePathPattern.contains("*")) {
                    String matchPattern = ignorePathPattern.replace("*", "");
                    if (path.contains(matchPattern)) {
                        System.out.println("match token auth ignore path with [" + path + "]");
                        return false;
                    }
                } else {
                    if (ignorePathPattern.equalsIgnoreCase(path)) {
                        System.out.println("match token auth ignore path with [" + path + "]");
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