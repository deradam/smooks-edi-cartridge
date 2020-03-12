/*
	Milyn - Copyright (C) 2006 - 2010

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.edi.edisax;

import org.milyn.io.StreamUtils;
import org.milyn.resource.URIResourceLocator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public abstract class AbstractEDIParserTestCase {

    protected void test(String testpack) throws IOException {
        String packageName = getClass().getPackage().getName().replace('.', '/');
        String mappingModel = "/" + packageName + "/" + testpack + "/edi-to-xml-mapping.xml";
        InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-input.txt")));
        String expected = new String(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/expected.xml"))).trim();
        MockContentHandler contentHandler = new MockContentHandler();

        expected = removeCRLF(expected);
        try {
            EDIParser parser = new EDIParser();
            String mappingResult = null;

            parser.setContentHandler(contentHandler);
            parser.setMappingModel(EDIParser.parseMappingModel(mappingModel, URIResourceLocator.extractBaseURI(mappingModel)));
            parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
            parser.parse(new InputSource(input));

            mappingResult = contentHandler.xmlMapping.toString().trim();
            mappingResult = removeCRLF(mappingResult);
            if (!mappingResult.equals(expected)) {
                System.out.println("Expected: \n[" + expected + "]");
                System.out.println("Actual: \n[" + mappingResult + "]");
                assertEquals(expected, mappingResult, "Testpack [" + testpack + "] failed.");
            }
        } catch (SAXException e) {
            String exceptionMessage = e.getClass().getName() + ":" + e.getMessage();

            exceptionMessage = removeCRLF(exceptionMessage);
            if (!exceptionMessage.equals(expected)) {
                assertEquals(expected, exceptionMessage, "Unexpected exception on testpack [" + testpack + "].  ");
            }
        } catch (EDIConfigurationException e) {
            assert false : e;
        }
    }

    protected void testEDIParseException(String testpack, String segmentNodeName, int segmentNumber) throws IOException, EDIParseException {
        InputStream input = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-input.txt")));
        InputStream mapping = new ByteArrayInputStream(StreamUtils.readStream(getClass().getResourceAsStream(testpack + "/edi-to-xml-mapping.xml")));

        MockContentHandler contentHandler = new MockContentHandler();

        try {
            EDIParser parser = new EDIParser();

            parser.setContentHandler(contentHandler);
            parser.setMappingModel(EDIParser.parseMappingModel(mapping));
            parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
            parser.parse(new InputSource(input));

            assertTrue(false, "Test case should thow an EdiParseException.");

        } catch (EDIParseException e) {
            if (segmentNodeName == null) {
                assertTrue(e.getErrorNode() == null, "EDIParseException should contain no MappingNode.");
            } else if (e.getErrorNode() == null) {
                throw e;
            } else {
                assertEquals(e.getErrorNode().getXmltag(), segmentNodeName, "EDIParseException should contain the MappingNode with xmltag [" + segmentNodeName + "]. Instead it contains [" + e.getErrorNode().getXmltag() + "].");
            }
            assertEquals(e.getSegmentNumber(), segmentNumber, "EDIParseException should contain the segmentNumber [" + segmentNumber + "]. Instead it contains [" + e.getSegmentNumber() + "].");

        } catch (Exception e) {
            assert false : e;
        }
    }


    private String removeCRLF(String string) throws IOException {
        return StreamUtils.trimLines(new StringReader(string)).toString();
    }
}