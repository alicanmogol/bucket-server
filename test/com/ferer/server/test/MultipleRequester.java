package com.ferer.server.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * acm | 12/12/12
 */
public class MultipleRequester {

    public static void main(String[] args) {
        new MultipleRequester();
    }

    public MultipleRequester() {
        //runNonBlocking();
        runBlocking();
    }

    private void runBlocking() {
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            try {
                URL url = new URL("http://localhost:9091/sample-application/product/all");
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
                System.out.println("response: " + stringBuilder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("completed in: " + (System.currentTimeMillis() - startTime) + " milliseconds");
        }
    }

    private void runNonBlocking() {
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://localhost:9091/sample.application");
                        //System.out.println(Thread.currentThread().getId() + ": requesting, " + url);
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
                        //System.out.println(Thread.currentThread().getId() + ": content: " + stringBuilder);
                    } catch (Exception e) {
                        //System.out.println(Thread.currentThread().getId() + ": got Exception!");
                    }
                    System.out.println("completed in: " + (System.currentTimeMillis() - startTime) + " milliseconds");
                }
            }.start();
        }
    }
}
