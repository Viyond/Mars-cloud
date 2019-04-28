package com.mars.cloud.registered;

import com.mars.cloud.core.constant.CloudConstant;
import com.mars.cloud.core.helper.ZookeeperHelper;
import com.mars.cloud.core.util.CloudConfigUtil;
import com.mars.cloud.core.util.CloudUtil;
import com.mars.core.constant.EasySpace;
import com.mars.mvc.resolve.model.EasyMappingModel;

import java.util.Map;

/**
 * 注册接口
 */
public class Registered {

    private static ZookeeperHelper zookeeperHelper = new ZookeeperHelper();

    private static EasySpace constants = EasySpace.getEasySpace();

    /**
     * 发布注册接口
     * @throws Exception
     */
    public static void register() throws Exception {
        try {
            /* 打开zookeeper连接 */
            zookeeperHelper.openConnection();

            /* 获取本服务的名称 */
            String serverName = CloudConfigUtil.getCloudName();
            /* 获取本服务IP */
            String ip = CloudUtil.getLocalIp();
            /* 获取本服务端口 */
            String port = CloudUtil.getPort();

            /* 将本服务的接口发布注册到zookeeper */
            Map<String,EasyMappingModel> maps = getControllers();
            for(String methodName : maps.keySet()){

                String node = CloudConstant.API_SERVER_NODE
                        .replace("{serverName}",serverName)
                        .replace("{method}",methodName)
                        .replace("{ip}",ip)
                        .replace("{port}",port);

                /* 将本服务的接口已写入zookeeper */
                zookeeperHelper.createNodes(node,CloudUtil.getLocalHost()+"/"+methodName);
            }

        } catch (Exception e){
            throw new Exception("注册与发布接口失败",e);
        } finally {
            zookeeperHelper.closeConnection();
        }
    }

    /**
     * 获取所有的controller对象
     * @return duix
     */
    private static Map<String,EasyMappingModel> getControllers() {
        Map<String,EasyMappingModel> controlObjects = null;
        Object obj = constants.getAttr("controlObjects");
        if(obj != null) {
            controlObjects = (Map<String,EasyMappingModel>)obj;
        }
        return controlObjects;
    }
}
