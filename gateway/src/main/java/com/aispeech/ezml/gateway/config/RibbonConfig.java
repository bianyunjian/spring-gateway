package com.aispeech.ezml.gateway.config;

import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.niws.loadbalancer.EurekaNotificationServerListUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonConfig {
    /**
     * ribbon默认的更新机制是通过PollingServerListUpdater维护的线程去同步，那么我们可以利用配置让ribbon使用事件监听机制更新
     *
     * @return
     */
    @Bean
    public ServerListUpdater ribbonServerListUpdater() {
        return new EurekaNotificationServerListUpdater();

    }
}
