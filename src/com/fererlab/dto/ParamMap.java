package com.fererlab.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * acm 10/16/12 5:37 PM
 */
public class ParamMap<K, V> extends ConcurrentHashMap<K, V> {

    private List<Param<K, V>> params = new ArrayList<Param<K, V>>();

    public void addParam(Param<K, V> param) {
        params.add(param);
        put(param.getKey(), param.getValue());
    }

    public void addParams(List<Param<K, V>> params) {
        this.params = params;
        for (Param<K, V> param : params) {
            addParam(param);
        }
    }

    public List<Param<K, V>> getParams() {
        return params;
    }

}