package com.fererlab.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * acm 10/16/12 5:37 PM
 */
public class ParamMap<K extends String, V extends Param<K, Object>> extends TreeMap<K, V> {

    private List<Param<K, Object>> paramList = new ArrayList<>();


    public void addParam(V param) {
        paramList.add(param);
        put(param.getKey(), param);
    }

    public List<Param<K, Object>> getParamList() {
        return paramList;
    }
}