package com.connectedworldservices.nectr.v2.api.rest;

import static java.lang.String.format;
import static java.lang.System.getenv;
import static java.lang.System.out;
import static org.springframework.util.StringUtils.isEmpty;

import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public abstract class AbstractTest {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            out.print("Test: " + colour(description.getMethodName(), ANSI_CYAN) + ": ");
        }

        @Override
        protected void succeeded(Description description) {
            out.println(colour("PASSED", ANSI_GREEN));
        }

        @Override
        protected void failed(Throwable e, Description description) {
            out.println(colour("FAILED", ANSI_RED) + " [" + (isEmpty(e.getMessage()) ? e.getClass().getCanonicalName() : e.getMessage()) + "]");
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            out.println(colour("SKIPPED!", ANSI_YELLOW));
        }
    };

    private static String colour(String value, String colour) {
        if (!isJenkinsEnvironment()) {
            return format("%s%s%s", colour, value, ANSI_RESET);
        }
        return value;
    }

    public static boolean isJenkinsEnvironment() {
        return getenv("JENKINS_HOME") != null;
    }
}
