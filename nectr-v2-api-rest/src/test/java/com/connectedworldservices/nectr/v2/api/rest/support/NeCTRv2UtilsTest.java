package com.connectedworldservices.nectr.v2.api.rest.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.support.NeCTRv2Utils;

public class NeCTRv2UtilsTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void should_return_false_when_double_quoted_value_is_null() {
        assertFalse(NeCTRv2Utils.isDoubleQuoted(null));
    }

    @Test
    public void should_return_false_when_double_quoted_value_is_not_double_quoted() {
        assertFalse(NeCTRv2Utils.isDoubleQuoted("test"));
    }

    @Test
    public void should_return_false_when_double_quoted_value_is_single_quoted() {
        assertFalse(NeCTRv2Utils.isDoubleQuoted("'test'"));
    }

    @Test
    public void should_return_false_when_double_quoted_value_has_double_quote_on_left_only() {
        assertFalse(NeCTRv2Utils.isDoubleQuoted("\"test"));
    }

    @Test
    public void should_return_false_when_double_quoted_value_has_double_quote_on_right_only() {
        assertFalse(NeCTRv2Utils.isDoubleQuoted("test\""));
    }

    @Test
    public void should_return_true_when_double_quoted_value_is_double_quoted() {
        assertTrue(NeCTRv2Utils.isDoubleQuoted("\"test\""));
    }

    @Test
    public void should_return_false_when_single_quoted_value_is_null() {
        assertFalse(NeCTRv2Utils.isSingleQuoted(null));
    }

    @Test
    public void should_return_false_when_single_quoted_value_is_not_single_quoted() {
        assertFalse(NeCTRv2Utils.isSingleQuoted("test"));
    }

    @Test
    public void should_return_false_when_single_quoted_value_is_double_quoted() {
        assertFalse(NeCTRv2Utils.isSingleQuoted("\"test\""));
    }

    @Test
    public void should_return_false_when_single_quoted_value_has_single_quote_on_left_only() {
        assertFalse(NeCTRv2Utils.isSingleQuoted("'test"));
    }

    @Test
    public void should_return_false_when_single_quoted_value_has_single_quote_on_right_only() {
        assertFalse(NeCTRv2Utils.isSingleQuoted("test'"));
    }

    @Test
    public void should_return_true_when_single_quoted_value_is_single_quoted() {
        assertTrue(NeCTRv2Utils.isSingleQuoted("'test'"));
    }

    @Test
    public void should_return_null_when_unquote_value_is_null() {
        assertNull(NeCTRv2Utils.unquote(null));
    }

    @Test
    public void should_return_same_object_when_unquote_value_is_not_a_string() {
        Integer expected = new Integer(1);

        assertEquals(expected, NeCTRv2Utils.unquote(expected));
    }

    @Test
    public void should_return_same_string_when_unquote_value_is_not_quoted() {
        String expected = "test";

        assertEquals(expected, NeCTRv2Utils.unquote(expected));
    }

    @Test
    public void should_return_same_unquoted_string_when_unquote_value_is_single_quoted() {
        String expected = "test";

        assertEquals(expected, NeCTRv2Utils.unquote("'test'"));
    }

    @Test
    public void should_return_same_unquoted_string_when_unquote_value_is_double_quoted() {
        String expected = "test";

        assertEquals(expected, NeCTRv2Utils.unquote("\"test\""));
    }
}
