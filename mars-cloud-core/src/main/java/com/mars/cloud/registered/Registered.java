package com.mars.cloud.registered;

import com.mars.cloud.core.annotations.NotRest;
import com.mars.cloud.core.constant.CloudConstant;
import com.mars.cloud.core.helper.ZkHelper;
import com.mars.cloud.core.util.CloudConfigUtil;
import com.mars.cloud.core.util.CloudUtil;
import com.mars.core.annotation.enums.ReqMethod;
import com.mars.core.constant.MarsConstant;
import com.mars.core.constant.MarsSpace;
import com.mars.mvc.model.MarsMappingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 注册接口
 */
public class Registered {

    private Logger marsLogger = LoggerFactory.getLogger(Registered.class);

    private MarsSpace constants = MarsSpace.getEasySpace();

    /**
     * 发布注册接口
     *
     * @throws Exception 异常
     */
    public void register() throws Exception {
        try {

            /* 打开zookeeper连接 */
            ZkHelper.openConnection();

            marsLogger.info("接口注册中.......");

            /* 获取本服务的名称 */
            String serverName = CloudConfigUtil.getCloudName();
            /* 获取本服务IP */
            String ip = CloudUtil.getLocalIp();
            /* 获取本服务端口 */
            String port = CloudUtil.getPort();

            /* 将本服务的接口发布注册到zookeeper */
            Map<String, MarsMappingModel> maps = getMarsApis();

            /* 注册接口 */
            for (String methodName : maps.keySet()) {

                /* 返回null代表这个接口不是rest接口，而是一个普通的http接口，所以不需要注册 */
                MarsMappingModel marsMappingModel = checkIsRest(maps,methodName);
                if(marsMappingModel == null){
                    continue;
                }

                /* 校验接口是否符合规则 */
                checkRequestMethod(marsMappingModel);

                /* 将本服务的接口写入zookeeper */
                String node = CloudConstant.API_SERVER_NODE
                        .replace("{serverName}", serverName)
                        .replace("{method}", methodName)
                        .replace("{ip}", ip)
                        .replace("{port}", port);

                ZkHelper.createNodes(node, CloudUtil.getLocalHost() + "/" + methodName);

                marsLogger.info("接口[" + CloudUtil.getLocalHost() + "/" + methodName + "]注册成功");
            }
        } catch (Exception e) {
            throw new Exception("注册与发布接口失败", e);
        }
    }

    /**
     * 获取所有的MarsApi对象
     *
     * @return 所有的MarsApi对象
     */
    private Map<String, MarsMappingModel> getMarsApis() {
        Map<String, MarsMappingModel> controlObjects = null;
        Object obj = constants.getAttr(MarsConstant.CONTROLLER_OBJECTS);
        if (obj != null) {
            controlObjects = (Map<String, MarsMappingModel>) obj;
        }
        return controlObjects;
    }

    /**
     * 校验cloud接口是否为post
     * @param marsMappingModel
     * @throws Exception
     */
    private void checkRequestMethod(MarsMappingModel marsMappingModel) throws Exception {
        Class cls = marsMappingModel.getCls();
        String methodName = marsMappingModel.getMethod();
        if(!marsMappingModel.getReqMethod().equals(ReqMethod.POST)){
            throw new Exception("MarsCloud对外提供的接口必须是POST方式["+cls+"."+methodName+"]");
        }
        if(marsMappingModel.getExeMethod().getParameterCount() > 1){
            throw new Exception("MarsCloud对外提供的接口只允许有一个参数，如果你有多个参数，可以合并成一个实体类或者用Map["+cls+"."+methodName+"]");
        }
    }

    /**
     * 校验是否是Rest接口
     * @param maps
     * @param methodName
     * @return true 不是rest，false 是rest
     */
    private MarsMappingModel checkIsRest(Map<String, MarsMappingModel> maps,String methodName){
        MarsMappingModel marsMappingModel = maps.get(methodName);
        Class<?> controllerCls = marsMappingModel.getCls();
        NotRest notRest = controllerCls.getAnnotation(NotRest.class);
        if(notRest == null){
           return marsMappingModel;
        }
        return null;
    }
}
