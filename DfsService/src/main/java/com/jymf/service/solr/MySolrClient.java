package com.jymf.service.solr;

import com.jymf.bean.json.RespContentJson;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;

import java.io.*;
import java.util.Properties;

/**
 * 用于进行建立Solr索引和查询Solr索引的类
 * @author Zhang
 * @version 0.1
 */
public class MySolrClient {
    protected static HttpSolrClient client = null;
    protected static String[] fieldNames = null;
    protected static String field1 = null;
    protected static String field2 = null;
    protected static String field3 = null;


    /**
     * 读取Solr配置文件,主要是字段名的配置和Solr服务器地址的配置
     */
    static {
        try {
            Properties properties = new Properties();
            //String solrConfigPath = MySolrClient.class.getResource("/").getFile()+"solr_config.properties";
            String solrConfigPath = "./solr_config.properties";
            File file = new File(solrConfigPath);
            System.out.println(file.getCanonicalPath());
            InputStream in = new FileInputStream(solrConfigPath);
            properties.load(in);
            String solrServerUrl = properties.getProperty("SolrServerUrl");
            client = new HttpSolrClient(solrServerUrl);
            fieldNames = properties.getProperty("FieldName").split(",");
            field1 = fieldNames[0];
            field2 = fieldNames[1];
            field3 = fieldNames[2];
            client = new HttpSolrClient(solrServerUrl);

        } catch (FileNotFoundException e) {
            System.out.println("The config file is not exists");
        } catch (IOException e){
            System.out.println("Loading config file error");
        }
    }


    /**
     * 建立Solr索引
     * @param fileName String 文件名
     * @param fdfsId String fdfsId
     * @param fileLevel int 文件级别:1.企业文件 2.商品文件
     * @throws IOException
     * @throws SolrServerException
     */
    public void buildIndex(String fileName, String fdfsId, int fileLevel) throws IOException,SolrServerException{
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField(field1, fileName);
        solrInputDocument.addField(field2, fdfsId);
        solrInputDocument.addField(field3,fileLevel);
        client.add(solrInputDocument);
    }

    /**
     * 根据企业Id或者商品Id查询返回文件下载下行数据包包体的Json封装对象
     * @param itemId String 企业Id或商品Id
     * @param fileLevel int 文件级别:1.企业文件 2.商品文件
     * @return RespContentJson 下载下行数据包包体的Json封装对象
     * @throws IOException
     * @throws SolrServerException
     */
    public RespContentJson queryByItemId(String itemId, int fileLevel) throws IOException,SolrServerException{
        SolrParams params = new SolrQuery(field1+":"+itemId+"* AND "+field3+":"+fileLevel);
        QueryResponse response = client.query(params);
        SolrDocumentList list = response.getResults();

        RespContentJson respContentJson = new RespContentJson();
        respContentJson.setItemId(itemId);
        for (SolrDocument solrDocument : list) {
            String fileName = solrDocument.get(field1).toString();
            String dfsId = solrDocument.get(field2).toString();
            String[] fileNameSep= fileName.split("_");
            String fileType = fileNameSep[fileNameSep.length-2];
            int fileOrder = Integer.parseInt(fileNameSep[fileNameSep.length-1]);
            if (fileType.equals("spec")){
                respContentJson.setSpec(fileOrder,dfsId);
            } else if (fileType.equals("over")){
                respContentJson.setOver(fileOrder,dfsId);
            } else if (fileType.equals("intr")){
                respContentJson.setIntr(fileOrder,dfsId);
            }

        }
        return respContentJson;
    }

    /**
     * 提交缓存中的数据,真正建立Solr索引
     * @throws IOException
     * @throws SolrServerException
     */
    public static void commit() throws IOException, SolrServerException{
        client.commit();
    }

}
