package com.jymf.bean.json;

import java.util.TreeMap;

/**
 * 下载下行数据包包体中Json POJO类
 * @author Zhang
 * @version 0.1
 */
public class RespContentJson {
    private String itemId = null;
    private TreeMap<Integer,String> spec = new TreeMap<Integer, String>();
    private TreeMap<Integer,String> over = new TreeMap<Integer, String>();
    private TreeMap<Integer,String> intr = new TreeMap<Integer, String>();

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setSpec(int key, String value){
        spec.put(key,value);
    }

    public void setOver(int key, String value){
        over.put(key,value);
    }

    public void setIntr(int key, String value){
        intr.put(key,value);
    }

    public TreeMap<Integer, String> getSpec() {
        return spec;
    }

    public void setSpec(TreeMap<Integer, String> spec) {
        this.spec = spec;
    }

    public TreeMap<Integer, String> getOver() {
        return over;
    }

    public void setOver(TreeMap<Integer, String> over) {
        this.over = over;
    }

    public TreeMap<Integer, String> getIntr() {
        return intr;
    }

    public void setIntr(TreeMap<Integer, String> intr) {
        this.intr = intr;
    }
}
