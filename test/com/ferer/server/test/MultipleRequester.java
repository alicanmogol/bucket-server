package com.ferer.server.test;

import java.io.IOException;
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
        for (int i = 0; i < 100; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getId() + ": requesting");
                        URL url = new URL("http://127.0.0.1:9095/");
                        System.out.println(Thread.currentThread().getId() + ": content" + url.getContent());
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getId() + ": got response");
                    }
                }
            }.start();
        }
    }
}
