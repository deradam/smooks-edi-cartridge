package org.smooks.edi.edisax;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smooks.edi.edisax.model.internal.DelimiterType;
import org.smooks.edi.edisax.model.internal.Delimiters;
import org.smooks.edi.edisax.unedifact.UNEdifactInterchangeParser;
import org.smooks.edi.edisax.util.EDIUtils;
import org.smooks.edi.edisax.util.IllegalNameException;
import org.smooks.util.CollectionsUtil;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author bardl
 */
public class EDIUtilsTest {

    @Test
    public void test_with_escape() throws IOException, SAXException {
        String[] test = EDIUtils.split("first?::second??:third", ":", "?");
        String[] expected = new String[]{"first:", "second??", "third"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        test = EDIUtils.split("ATS+hep:iee+hai??+kai=haikai+slut", "+", "?");
        expected = new String[]{"ATS", "hep:iee", "hai??", "kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        test = EDIUtils.split("ATS+hep:iee+hai?#?#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#?#", "kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        test = EDIUtils.split("ATS+#hep:iee+#hai?#?#+#kai=haikai+#slut", "+#", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#?#", "kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS", "hep:iee", "hai??", "kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", null);
        expected = new String[]{"ATS", "hep:iee", "hai??", "kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        // Test restarting escape sequence within escape sequence.
        test = EDIUtils.split("ATS+hep:iee+hai??#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

        // Test restarting delimiter sequence within delimiter sequence.
        test = EDIUtils.split("ATS++#hep:iee+#hai?+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS+", "hep:iee", "hai+#kai=haikai", "slut"};
        assertTrue(equal(test, expected), "Result is [" + output(test) + "] should be [" + output(expected) + "] ");

    }

    @Test
    public void test_without_escape() {
        String[] result = EDIUtils.split(null, "*", null);
        assertTrue(result == null, "Result is [" + output(result) + "] should be [null] ");

        result = EDIUtils.split("", null, null);
        String[] expected = new String[0];
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("abc def", null, null);
        expected = new String[]{"abc", "def"};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("abc def", " ", null);
        expected = new String[]{"abc", "def"};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("abc  def", " ", null);
        expected = new String[]{"abc", "", "def"};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("ab:cd:ef", ":", null);
        expected = new String[]{"ab", "cd", "ef"};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("ab:cd:ef:", ":", null);
        expected = new String[]{"ab", "cd", "ef", ""};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("ab:cd:ef::", ":", null);
        expected = new String[]{"ab", "cd", "ef", "", ""};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split(":cd:ef", ":", null);
        expected = new String[]{"", "cd", "ef"};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split("::cd:ef", ":", null);
        expected = new String[]{"", "", "cd", "ef"};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");

        result = EDIUtils.split(":cd:ef:", ":", null);
        expected = new String[]{"", "cd", "ef", ""};
        assertTrue(equal(result, expected), "Result is [" + output(result) + "] should be [" + output(expected) + "] ");


    }

    @Test
    public void test_concatAndTruncate() {
        Delimiters delims = UNEdifactInterchangeParser.defaultUNEdifactDelimiters;

        assertEquals("ab", EDIUtils.concatAndTruncate(CollectionsUtil.toList("a", "b", "+:+"), DelimiterType.SEGMENT, delims));
        assertEquals("a+:+b", EDIUtils.concatAndTruncate(CollectionsUtil.toList("a", "+:+", "b", "+:+"), DelimiterType.SEGMENT, delims));
        assertEquals("a+:+bc+:+", EDIUtils.concatAndTruncate(CollectionsUtil.toList("a", "+:+", "b", "c+:+"), DelimiterType.SEGMENT, delims));

        assertEquals("ab", EDIUtils.concatAndTruncate(CollectionsUtil.toList("a", "b", "+:+"), DelimiterType.FIELD, delims));
        assertEquals("ab+:+'", EDIUtils.concatAndTruncate(CollectionsUtil.toList("a", "b", "+:+'"), DelimiterType.FIELD, delims));
        assertEquals("ab+:+", EDIUtils.concatAndTruncate(CollectionsUtil.toList("a", "b", "+:+"), DelimiterType.COMPONENT, delims));
    }

    @Test
    public void testEncodeClassName() throws IllegalNameException {
        assertEquals("Address", EDIUtils.encodeClassName("ADDRESS"));
        assertEquals("CustomerAddress", EDIUtils.encodeClassName("CUSTOMER_ADDRESS"));
        assertEquals("CustomerADDRESS", EDIUtils.encodeClassName("Customer_ADDRESS"));
        assertEquals("CustomerAddress", EDIUtils.encodeClassName("Customer_address"));
        assertEquals("Default", EDIUtils.encodeClassName("default"));
        assertEquals("_1CustomerAddressPOBox", EDIUtils.encodeClassName("1CustomerAddressP.O.Box"));
    }

    @Test
    public void testEncodeAttribute() throws IllegalNameException {
        assertEquals("address", EDIUtils.encodeAttributeName("ADDRESS"));
        assertEquals("addRESS", EDIUtils.encodeAttributeName("addRESS"));
        assertEquals("addRESS", EDIUtils.encodeAttributeName("AddRESS"));
        assertEquals("orderId", EDIUtils.encodeAttributeName("orderId"));
        assertEquals("orderId", EDIUtils.encodeAttributeName("order_id"));
        assertEquals("_default", EDIUtils.encodeAttributeName("default"));
        assertEquals("_package", EDIUtils.encodeAttributeName("package"));
        assertEquals("_package", EDIUtils.encodeAttributeName("Package"));
        assertEquals("_1address", EDIUtils.encodeAttributeName("1ADDRESS"));
        assertEquals("_1addressPOBox", EDIUtils.encodeAttributeName("_1addressP.O.Box"));
    }

    private String output(String[] value) {
        if (value == null) {
            return null;
        }

        String result = "{";
        String str;
        for (int i = 0; i < value.length; i++) {
            str = value[i];
            result += "\"" + str + "\"";
            if (i != value.length - 1) {
                result += ", ";
            }
        }
        result += "}";
        return result;
    }

    private static boolean equal(String[] test, String[] expected) {
        if (test.length != expected.length) {
            return false;
        }

        for (int i = 0; i < test.length; i++) {
            if (!test[i].equals(expected[i])) {
                return false;
            }
        }
        return true;
    }
}
