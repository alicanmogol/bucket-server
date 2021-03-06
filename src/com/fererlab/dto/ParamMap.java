package com.fererlab.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * acm 10/16/12
 */
public class ParamMap<K extends String, V extends Param<K, Object>> extends TreeMap<K, V> {

    public void addParam(V param) {
        put(param.getKey(), param);
    }

    public List<Param<K, Object>> getParamList() {
        List<Param<K, Object>> params = new ArrayList<Param<K, Object>>();
        for (V v : values()) {
            params.add(v);
        }
        return params;
    }

    public Object getValue(K k) {
        return get(k).getValue();
    }

}