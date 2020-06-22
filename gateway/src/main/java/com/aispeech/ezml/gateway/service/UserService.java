package com.aispeech.ezml.gateway.service;

import com.aispeech.ezml.gateway.auth.UserInfoReq;
import com.aispeech.ezml.gateway.auth.UserInfoResp;
import com.aispeech.ezml.gateway.base.BaseResponse;
import com.aispeech.ezml.gateway.support.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Value("${app.service.user-service}")
    public String USER_SERVICE = "DEMO";
    @Value("${app.service.user-service-api}")
    public String USER_SERVICE_API = "/getUserInfo";


    @Autowired
    private DiscoveryClient discoveryClient;

    public BaseResponse<UserInfoResp> getUserInfo(UserInfoReq req) {
        if (null == req || StringUtils.isEmpty(req.getUserName())) return null;

        BaseResponse<UserInfoResp> resp = new BaseResponse<>();
        String serviceUri = "";
        List<String> serviceNames = discoveryClient.getServices();
        for (String serviceName : serviceNames) {
            if (serviceName.equalsIgnoreCase(USER_SERVICE)) {

                List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
                for (ServiceInstance serviceInstance : serviceInstances) {
                    serviceUri = serviceInstance.getUri().toString();
                    System.out.println("find user service uri: " + serviceUri);
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(serviceUri) == false) {

            String requestUri = serviceUri + USER_SERVICE_API;
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
