package com.aispeech.ezml.gateway;

import cn.hutool.extra.spring.SpringUtil;
import com.aispeech.ezml.gateway.config.servicesync.ServiceRealTimeSyncBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication implements ApplicationRunner, DisposableBean {


    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("执行一些启动后自定义的方法......");

        ServiceRealTimeSyncBean bean = SpringUtil.getBean(ServiceRealTimeSyncBean.class);
        if (bean != null) {
            bean.register();
        }
    }

    @Override
    public void destroy() throws Exception {
        ServiceRealTimeSyncBean bean = SpringUtil.getBean(ServiceRealTimeSyncBean.class);
        if (bean != null) {
            bean.unregister();
        }
    }
}

