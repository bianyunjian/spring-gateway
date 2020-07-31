package com.aispeech.ezml.gateway.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.PollingServerListUpdater;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.niws.loadbalancer.EurekaNotificationServerListUpdater;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

@RestController
@RequestMapping(path = "/config")
public class RefreshController {


    @GetMapping(path = "/refresh")
    public void refresh() {
        try {
            DiscoveryClient bean = SpringUtil.getBean(DiscoveryClient.class);
            if (bean != null) {
                Method method = DiscoveryClient.class.getDeclaredMethod("refreshRegistry");
                method.setAccessible(true);
                method.invoke(bean);
            }

            DynamicServerListLoadBalancer balancer = SpringUtil.getBean(DynamicServerListLoadBalancer.class);
            if (null != balancer) {
                Method method = DynamicServerListLoadBalancer.class.getDeclaredMethod("updateListOfServers");
                method.setAccessible(true);
                method.invoke(balancer);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
