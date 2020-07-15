package com.aispeech.ezml.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * 全局过滤器
 * 所有响应都会执行
 */
@Component
public class F4_ResponseFilter implements WebFilter {
    @Value("${app.filter.extract-response-body}")
    public boolean EXTRACT_RESPONSE_BODY = true;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

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

                    MediaType contentType = getDelegate().getHeaders().getContentType();
                    System.out.println("response Content-Type :" + contentType.toString());
                    if ((APPLICATION_JSON.isCompatibleWith(contentType))
                            || (TEXT_PLAIN.isCompatibleWith(contentType))) {

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
                }

                return super.writeWith(body);
            }

        };
        return decoratedResponse;
    }


}
