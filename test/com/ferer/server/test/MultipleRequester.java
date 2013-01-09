package com.ferer.server.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * acm | 12/12/12 3:34 PM
 */
public class MultipleRequester {

    public static void main(String[] args) {
        new MultipleRequester();
    }

    public MultipleRequester() {
        runTest();
    }

    private void runTest() {
        for (int i = 0; i < 1; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://127.0.0.1:9095");
                        System.out.println(Thread.currentThread().getId() + ": requesting, " + url);
                        Object content = url.getContent();
                        StringBuilder stringBuilder = new StringBuilder();
                        if (content instanceof InputStream) {
                            InputStreamReader inputStreamReader = new InputStreamReader((InputStream) content);
                            int c;
                            char ch;
                            while ((c = inputStreamReader.read()) != -1) {
                                ch = (char) c;
                                stringBuilder.append(ch);
                            }
                        }
                        System.out.println(Thread.currentThread().getId() + ": content: " + stringBuilder);
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getId() + ": got Exception!");
                    }
                }
            }.start();
        }
    }
}
