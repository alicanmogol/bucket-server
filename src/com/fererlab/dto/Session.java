package com.fererlab.dto;

import java.util.TreeMap;

/**
 * acm 10/15/12 4:27 PM
 */
public class Session<K, V> extends TreeMap<K, V> {

    public String toCookie() {
        StringBuilder cookie = new StringBuilder();
        if (this.size() > 0) {
            cookie.append("Set-Cookie:  ");
            for (K key : this.keySet()) {
                cookie.append(key);
                cookie.append("=");
                cookie.append(this.get(key));
                cookie.append(";");
            }
            cookie.append("\n");
        }
        return cookie.toString();
    }

}
