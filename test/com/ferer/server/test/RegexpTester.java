package com.ferer.server.test;

import com.fererlab.dto.Param;
import com.fererlab.dto.ParamRelation;

import java.util.Arrays;

/**
 * acm | 12/17/12
 */
public class RegexpTester {

    public static void main(String[] args) {
        new RegexpTester();
    }

    public RegexpTester() {
        //runRegexpTest1();
        //runRegexpTest2();
        runRegexpTest3();
    }

    private void runRegexpTest3() {
        String rawRequest = "" +
                "GET /some/uri/with?multiple=params&like=this HTTP/1.1\n" +
                "GET /some/uri/with?multiple=params&like=this HTTP/1.1   \n" +
                "User-Agent: Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4\n" +
                "Cookie: bbsessionhash=ef0382f877cd6743c53e78f2bc3077bc; bblastvisit=1355751890; bblastactivity=0\n" +
                "Accept-Language: en-US,en;q=0.8\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\n" +
                "";
        String[] requestRows = rawRequest.split("\n");
        for (String requestRow : requestRows) {
            String[] splits = requestRow.split("(^GET )|(^POST )|(^PUT )|(^DELETE )", 2);   // |([^ª]\n)
            if (splits.length > 1) {
                String[] strs = requestRow.split(" ", 2);
                System.out.println(strs[0].trim() + " " + strs[1].trim());
            }
        }
    }

    private void runRegexpTest2() {
        String rawRequest = "" +
                "User-Agent: Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4\n" +
                "Cookie: bbsessionhash=ef0382f877cd6743c53e78f2bc3077bc; bblastvisit=1355751890; bblastactivity=0\n" +
                "Accept-Language: en-US,en;q=0.8\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\n" +
                "";
        String[] arr = rawRequest.split("Cookie:");
        System.out.println(Arrays.toString(arr));
    }

    private void runRegexpTest1() {
        //String requestRow = "  POST /some/uri/with?multiple=params&like=this HTTP/1.1   ".trim();
        //String requestRow = "23 POST /some/uri/with?multiple=params&like=this HTTP/1.1 ".trim();
        //String[] results = requestRow.split("(^GET )|(^POST )|(^PUT )|(^DELETE )", 2);
        //System.out.println(Arrays.toString(results));

        String requestRow = "GET /SRV?TestCase019-*;~`'´·!'^+%&/()?_.çö&a=1&b<2&c>3&d=4&e!=5&5<f<7&g<=7&h>=8".trim();
        String[] methodUriPair = requestRow.split(" ", 2);
        if (methodUriPair.length > 1) {
            String[] ifContainsMethodSecondPartIsUri = (methodUriPair[0] + " .").split("(^GET )|(^POST )|(^PUT )|(^DELETE )", 2);
            if (ifContainsMethodSecondPartIsUri.length > 1) {
                System.out.println("METHOD: " + methodUriPair[0]);
                System.out.println("REQUEST: " + methodUriPair[1]);

                String[] uriAndParams = methodUriPair[1].split("\\?", 2);
                if (uriAndParams.length > 1) {
                    System.out.println("URI: " + uriAndParams[0]);
                    System.out.println("params: " + uriAndParams[1]);

                    String[] requestParams = uriAndParams[1].split("&");
                    // requestParams    a=1&b<2&c>3&d=4&e!=5&5<f<7&g<=7&h>=8
                    for (String paramKeyValue : requestParams) {
                        Param param = null;
                        String[] paramArr = null;

                        paramArr = paramKeyValue.split("<=", 2);
                        if (paramArr.length == 2) {
                            param = new Param<String, String>(paramArr[0], paramArr[1], ParamRelation.LE);
                            System.out.println("param:" + param);
                            continue;
                        }

                        paramArr = paramKeyValue.split(">=", 2);
                        if (paramArr.length == 2) {
                            param = new Param<String, String>(paramArr[0], paramArr[1], ParamRelation.GE);
                            System.out.println("param:" + param);
                            continue;
                        }

                        paramArr = paramKeyValue.split("!=", 2);
                        if (paramArr.length == 2) {
                            param = new Param<String, String>(paramArr[0], paramArr[1], ParamRelation.NE);
                            System.out.println("param:" + param);
                            continue;
                        }

                        paramArr = paramKeyValue.split("=", 2);
                        if (paramArr.length == 2) {
                            param = new Param<String, String>(paramArr[0], paramArr[1], ParamRelation.EQ);
                            System.out.println("param:" + param);
                            continue;
                        }

                        paramArr = paramKeyValue.split("(<)*(<)");
                        if (paramArr.length == 3) {
                            param = new Param<String, String>(paramArr[1], paramArr[0], paramArr[2], ParamRelation.BETWEEN);
                            System.out.println("param:" + param);
                            continue;
                        }

                        paramArr = paramKeyValue.split("<", 2);
                        if (paramArr.length == 2) {
                            param = new Param<String, String>(paramArr[0], paramArr[1], ParamRelation.LT);
                            System.out.println("param:" + param);
                            continue;
                        }

                        paramArr = paramKeyValue.split(">", 2);
                        if (paramArr.length == 2) {
                            param = new Param<String, String>(paramArr[0], paramArr[1], ParamRelation.GT);
                            System.out.println("param:" + param);
                        }

                    }

                }
            }
        }

    }
}
