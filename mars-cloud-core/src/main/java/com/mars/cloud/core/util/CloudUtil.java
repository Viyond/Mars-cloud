package com.mars.cloud.core.util;

import com.mars.core.util.MarsAddressUtil;

/**
 * 工具类
 */
public class CloudUtil {

    /**
     * 本机接口的完整请求前缀
     */
    private static String localHost;

    /**
     * 获取本机接口的完整请求前缀
     *
     * @return localhost
     * @throws Exception 异常
     */
    public static String getLocalHost() throws Exception {
        if (localHost == null) {
            localHost = getLocalIp() + ":" + getPort();
        }
        return localHost;
    }

    /**
     * 获取本机在局域网的IP
     *
     * @return ip
     * @throws Exception 异常
     */
    public static String getLocalIp() throws Exception {
        return MarsAddressUtil.getLocalIp();
    }

    /**
     * 获取端口号
     *
     * @return 端口号
     */
    public static String getPort() {
        return String.valueOf(MarsAddressUtil.getPort());
    }
}
