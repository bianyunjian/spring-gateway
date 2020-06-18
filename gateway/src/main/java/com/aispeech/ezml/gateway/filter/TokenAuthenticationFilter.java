package com.aispeech.ezml.gateway.filter;

import com.aispeech.ezml.gateway.base.BaseResponse;
import com.aispeech.ezml.gateway.base.ErrorCode;
import com.aispeech.ezml.gateway.base.UserTokenInfo;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 对客户端header 中的 Authorization 信息进行认证
 */
@Component
public class TokenAuthenticationFilter implements GlobalFilter, GatewayFilter, Ordered {

    private static final String Bearer_Prefix = "Bearer ";
    private static final String Authorization_Header = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
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
            String header = request.getHeaders().getFirst(Authorization_Header);
            if (header == null || !header.startsWith(Bearer_Prefix)) {
                throw new RuntimeException("请求头中Authorization信息为空");
            }
            //截取Authorization Bearer
            String token = header.substring(Bearer_Prefix.length());

            boolean tokenValid = false;
            if (!StringUtils.isEmpty(token)) {

                //有token,处理token
                UserTokenInfo tokenInfo = parseToken(token);
                if (tokenInfo != null && tokenInfo.isValid()) {
                    //token有效token
                    //把用户信息设置到header中，传递给后端服务
                    mutate.header("userId", tokenInfo.getUserId());
                    mutate.header("userName", tokenInfo.getUserName());
                    mutate.build();
                    tokenValid = true;
                } else {
                    //token 无效或者过期的处理
                    tokenValid = false;
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

    private UserTokenInfo parseToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        UserTokenInfo tokenInfo = new UserTokenInfo();
        if (token.startsWith("123")) {
            tokenInfo.setValid(true);
        } else {
            tokenInfo.setValid(false);
        }
        tokenInfo.setUserId("byj001");
        tokenInfo.setUserName("Bian Yun Jian");
        return tokenInfo;
    }

    private boolean checkNeedAuth(ServerHttpRequest request) {
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
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