package com.jymf.service.fdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.ClientGlobal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class FdfsService {

    TrackerClient tracker = null;
    TrackerServer trackerServer = null;
    StorageServer storageServer = null;
    StorageClient storageClient = null;

    static {
        try {
            //String fdfsConfigPath = FdfsService.class.getResource("/").getFile()+"fdfs_client.conf";
            String fdfsConfigPath = "./fdfs_client.conf";
            ClientGlobal.init(fdfsConfigPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public String upLoadByteArray(byte[] data, int offset, int length, String ext, HashMap<String,String> map) throws IOException,MyException{
        String[] fileIds = new String[0];
        try {
            tracker = new TrackerClient();
            trackerServer = tracker.getConnection();
            storageServer = null;
            storageClient = new StorageClient(trackerServer, storageServer);
            int mapSize = map.size();
            NameValuePair[] nameValuePairs = new NameValuePair[mapSize];
            int index = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nameValuePairs[index++] = new NameValuePair(entry.getKey(),entry.getValue());
            }
            fileIds = storageClient.upload_file(data, offset, length, ext, nameValuePairs);
        } catch (IOException e) {
            throw e;
        } catch (MyException e) {
            throw e;
        } finally {
            trackerServer.close();
        }
        //return fileIds[0]+fileIds[1];
        return fileIds[1];    //由于这里在服务端配置了只使用一个group,所以忽略掉了fileIds[0]中的group信息.

    }

    public String uploadByteArray(byte[] data, String ext, HashMap<String, String> map) throws IOException,MyException{
        return upLoadByteArray(data,0,data.length,ext,map);
    }
}
