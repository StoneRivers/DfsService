package com.jymf.main;

import com.jymf.bean.json.UploadDownJson;
import com.jymf.bean.packet.BasePackage;
import com.jymf.exception.CrcCheckErrorException;
import com.jymf.exception.IllegalPackageException;
import com.jymf.service.Handler;
import com.jymf.tool.JsonTool;
import com.jymf.tool.PacketTool;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.zeromq.ZMQ;

import java.util.Arrays;

/**
 * 服务主程序,main函数所在类.
 * @author Zhang
 * @version 0.1
 */
public class ServerService {

    static {
        PropertyConfigurator.configure("./log4j.properties");
    }

    public static void main(String[] args) {
        final ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket router = context.socket(ZMQ.ROUTER);
        ZMQ.Socket dealer = context.socket(ZMQ.DEALER);

        router.bind("tcp://*:5555");
        dealer.bind("tcp://*:5556");

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable(){
                public void run() {
                    ZMQ.Socket response = context.socket(ZMQ.REP);
                    Logger logger = Logger.getRootLogger();

                    response.connect("tcp://*:5556");

                    while (!Thread.currentThread().isInterrupted()) {
                        logger.debug("waitting for data");
                        byte[] requestBytes = response.recv();
                        logger.debug(Arrays.toString(requestBytes));
                        BasePackage basePackage = new BasePackage(requestBytes);
                        int statusCode;
                        String errorMsg;
                        try {
                            basePackage.check();
                            basePackage.prepare();
                        } catch (IllegalPackageException e) {
                            statusCode = 1;
                            errorMsg = e.getMessage();
                            logger.warn("SC:"+statusCode+" "+errorMsg);
                            e.printStackTrace();
                            response.send(new byte[]{});
                            continue;
                        } catch (CrcCheckErrorException e) {
                            statusCode = 2;
                            errorMsg = e.getMessage();
                            logger.warn("SC:"+statusCode+" "+errorMsg);
                            e.printStackTrace();
                            response.send(new byte[]{});
                            continue;
                        } catch (Exception e) {
                            statusCode = 3;
                            errorMsg = e.getMessage();
                            logger.warn("SC:"+statusCode+" "+errorMsg);
                            e.printStackTrace();
                            String jsonString = JsonTool.toJson(new UploadDownJson(statusCode,errorMsg));
                            byte[] respContent = PacketTool.pack(basePackage.getCopeCode(),jsonString,null);
                            response.send(respContent);
                            continue;
                        }
                        Handler handler = basePackage.selectHandler();
                        handler.handle();
                        byte[] respContent = PacketTool.pack(basePackage.getCopeCode(),handler.getJsonString(),handler.getContent());
                        logger.debug(Arrays.toString(respContent));
                        response.send(respContent);
                    }
                    response.close();
                }

            }).start();
        }
        ZMQ.proxy(router, dealer, null);
        router.close();
        dealer.close();
        context.term();
    }
}
