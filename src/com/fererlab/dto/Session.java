package com.fererlab.dto;

import java.util.TreeMap;

/**
 * acm 10/15/12
 */
public class Session<K extends String, V extends String> extends TreeMap<K, V> {

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
