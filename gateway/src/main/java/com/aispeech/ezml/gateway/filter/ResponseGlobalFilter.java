package com.aispeech.ezml.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 全局过滤器
 * 所有请求都会执行
 * 可拦截get、post等请求做逻辑处理
 */
@Component
public class ResponseGlobalFilter implements GlobalFilter, GatewayFilter, Ordered {
    @Value("${app.filter.extract-response-body}")
    public boolean EXTRACT_RESPONSE_BODY = true;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (EXTRACT_RESPONSE_BODY == false) {
            return chain.filter(exchange);
        }
        ServerHttpResponse decoratedResponse = getServerHttpResponse(exchange);
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private ServerHttpResponse getServerHttpResponse(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {


            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                Flux<DataBuffer> flux = null;
                if (body instanceof Mono) {
                    Mono<? extends DataBuffer> mono = (Mono<? extends DataBuffer>) body;
                    body = mono.flux();

                }
                if (body instanceof Flux) {
                    flux = (Flux<DataBuffer>) body;
                    return super.writeWith(flux.buffer().map(dataBuffers -> {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        dataBuffers.forEach(i -> {
                            byte[] array = new byte[i.readableByteCount()];
                            i.read(array);
                            DataBufferUtils.release(i);
                            outputStream.write(array, 0, array.length);
                        });
                        String result = outputStream.toString();
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("response body:" + result);
                        return bufferFactory.wrap(result.getBytes());
                    }));
                }

                System.out.println("response(no flux) body:" + body);
                return super.writeWith(body);
            }

        };
        return decoratedResponse;
    }

    @Override
    public int getOrder() {
        return -2;

    }
}
