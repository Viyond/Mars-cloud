package com.mars.cloud.request;

import com.mars.cloud.core.util.CloudHttpUtil;
import com.mars.cloud.load.GetServerApis;
import com.mars.core.util.SerializableUtil;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 发起rest请求
 */
public class MarsRestTemplate {

    /**
     * 发起请求
     *
     * @param serverName serverName
     * @param methodName methodName
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName,Class<T> resultType) throws Exception {
        return request(serverName, methodName, null,resultType);
    }

    /**
     * 发起请求
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Object params, Class<T> resultType) throws Exception {
        String url = "http://";
        try {

            url = url + GetServerApis.getUrl(serverName, methodName);

            if(params == null){
                params = new HashMap<>();
            }

            InputStream inputStream = CloudHttpUtil.request(url, params);

            return SerializableUtil.deSerialization(inputStream, resultType);
        } catch (Exception e) {
            throw new Exception("发起请求出现异常,url:[" + url + "],", e);
        }
    }
}
