package com.jymf.bean.packet;

import com.jymf.tool.CrcTool;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * Created by zhangyuanan on 15/12/18.
 */
public class BasePackageTest {

    private BasePackage aPackage = null;

    @Before
    public void init(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream ops = new DataOutputStream(baos);
            ops.write(new String("JY").getBytes("utf-8"));    //插入JY
            ops.writeShort(2);                                //插入操作码2
            String json = "This test is for checking and preparing";
            byte[]  jsonData = json.getBytes("utf-8");
            ops.writeShort((short)jsonData.length);
            ops.write(jsonData);


            ops.writeInt(CrcTool.crc32(baos.toByteArray()));  //插入CRC32校验码
            //System.out.println(Arrays.toString(baos.toByteArray()));
            aPackage = new BasePackage(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Test
    public void otherTest(){
        int i = -5;
        long l = i;
        int i1 = (int)l;
        System.out.println(l);
        System.out.println(i1);


    }
}
