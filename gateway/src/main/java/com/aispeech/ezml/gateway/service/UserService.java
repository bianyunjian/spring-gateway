package com.aispeech.ezml.gateway.service;

import com.aispeech.ezml.gateway.auth.UserInfoReq;
import com.aispeech.ezml.gateway.auth.UserInfoResp;
import com.aispeech.ezml.gateway.base.BaseResponse;
import com.aispeech.ezml.gateway.support.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final String SERVICE_NAME = "DEMO";

    @Autowired
    private DiscoveryClient discoveryClient;

    public BaseResponse<UserInfoResp> getUserInfo(UserInfoReq req) {
        if (null == req || StringUtils.isEmpty(req.getUserName())) return null;

        BaseResponse<UserInfoResp> resp = new BaseResponse<>();
        String serviceUri = "";
        List<String> serviceNames = discoveryClient.getServices();
        for (String serviceName : serviceNames) {
            if (serviceName.equalsIgnoreCase(SERVICE_NAME)) {

                List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
                for (ServiceInstance serviceInstance : serviceInstances) {
                    serviceUri = serviceInstance.getUri().toString();
                    System.out.println("find user service uri: " + serviceUri);
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(serviceUri) == false) {

            String requestUri = serviceUri + "/getUserInfo";
            String respJson = HttpUtils.doPostJson(requestUri, JSON.toJSONString(req));
            if (StringUtils.isEmpty(respJson) == false) {
                resp = JSON.parseObject(respJson, new TypeReference<BaseResponse<UserInfoResp>>() {
                });
                resp.success();

            }
        }

        return resp;
    }

}
