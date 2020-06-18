package com.aispeech.ezml.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 全局过滤器
 * 所有请求都会执行
 * 可拦截get、post等请求做逻辑处理
 */
@Component
public class RequestGlobalFilter implements GlobalFilter, GatewayFilter, Ordered {
    @Value("${app.filter.extract-request-params}")
    public boolean EXTRACT_REQUEST_PARAMS = true;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String uriString = serverHttpRequest.getURI().toString();
        System.out.println("request uri:" + uriString);
        String method = serverHttpRequest.getMethodValue();
        System.out.println("request method:" + method);

        if (EXTRACT_REQUEST_PARAMS == false) {
            return chain.filter(exchange);
        }

        if (HttpMethod.POST.name().equalsIgnoreCase(method)) {

            //判断是否是文件上传
            if (checkIfFileUploadRequest(serverHttpRequest)) {
                System.out.println("request file upload");
                return chain.filter(exchange);
            }


            // 读取请求体
            ServerRequest serverRequest = new DefaultServerRequest(exchange);
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                    .flatMap(body -> {
                        //记录请求体日志
                        System.out.println("request body:" + body);
                        return Mono.just(body);
                    });


            //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);

            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                    .then(Mono.defer(() -> {
                        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public HttpHeaders getHeaders() {
                                long contentLength = headers.getContentLength();
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.putAll(super.getHeaders());
                                if (contentLength > 0) {
                                    httpHeaders.setContentLength(contentLength);
                                } else {
                                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                                }
                                return httpHeaders;
                            }

                            @Override
                            public Flux<DataBuffer> getBody() {
                                return outputMessage.getBody();
                            }
                        };

                        return chain.filter(exchange.mutate().request(decorator).build());
                    }));

        } else if (HttpMethod.GET.name().equalsIgnoreCase(method)) {

            MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
            if (queryParams.size() > 0) {
                System.out.println("request params:");
                for (String k :
                        queryParams.keySet()) {
                    System.out.println(k + "=" + queryParams.get(k));
                }

            }
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }

    private boolean checkIfFileUploadRequest(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            List<String> headerValue = headers.get(HttpHeaders.CONTENT_TYPE);

            boolean findBoundary = false;
            boolean findFormData = false;
            for (String v :
                    headerValue) {
                if (v.contains("boundary=")) {
                    findBoundary = true;
                }
                if (v.contains("multipart/form-data")) {
                    findFormData = true;
                }
            }

            return findBoundary && findFormData;
        }
        return false;
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
