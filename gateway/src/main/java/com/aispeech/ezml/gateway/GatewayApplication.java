package com.aispeech.ezml.gateway;

import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.niws.loadbalancer.EurekaNotificationServerListUpdater;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * ribbon默认的更新机制是通过PollingServerListUpdater维护的线程去同步，那么我们可以利用配置让ribbon使用事件监听机制更新
     * @return
     */
    @Bean
    public ServerListUpdater ribbonServerListUpdater() {
        return new EurekaNotificationServerListUpdater();
    }
}

