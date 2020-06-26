package com.sean.lib_code_java;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void ipStringText() {
//        String ip = "192.168.1.73:8081";
        String ip = "http://192.168.1.73:8081";
        String resultIp = RegexPatternHelper.getIpFromString(ip);
        assertEquals("192.168.1.73", resultIp);
    }
}