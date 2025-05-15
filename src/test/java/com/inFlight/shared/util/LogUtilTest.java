package com.inFlight.shared.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogUtilTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testInfoLog() {
        LogUtil.info("Test info message");
        assertTrue(outContent.toString().contains("[INFO] Test info message"));
    }

    @Test
    void testWarnLog() {
        LogUtil.warn("Test warning message");
        assertTrue(outContent.toString().contains("[WARN] Test warning message"));
    }

    @Test
    void testLogException() {
        Exception ex = new IllegalArgumentException("Invalid input");
        LogUtil.log(ex);
        String output = errContent.toString();
        assertTrue(output.contains("[ERROR] IllegalArgumentException: Invalid input"));
        assertTrue(output.contains("java.lang.IllegalArgumentException"));
    }

    @Test
    void testLogExceptionWithContext() {
        Exception ex = new NullPointerException("Something was null");
        LogUtil.log("LoginService", ex);
        String output = errContent.toString();
        assertTrue(output.contains("[ERROR] in LoginService: NullPointerException - Something was null"));
        assertTrue(output.contains("java.lang.NullPointerException"));
    }
}