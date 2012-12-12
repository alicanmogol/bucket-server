package com.fererlab.dto;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * acm 10/15/12 4:27 PM
 */
public class Session<K, V> extends ConcurrentSkipListMap<K, V> {

    public String toCookie() {
        StringBuilder cookie = new StringBuilder();
        for (K key : this.keySet()) {
            cookie.append(key);
            cookie.append("=");
            cookie.append(this.get(key));
            cookie.append(";");
        }
        return cookie.toString();
    }

}
