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

package org.smooks.edi.edisax.v1_1.imports;

import org.smooks.edi.edisax.AbstractEDIParserTestCase;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class EDIParserTest extends AbstractEDIParserTestCase {

    public void test_import_01() throws IOException {
        test("test_imports_01");
    }

    public void test_import_02() throws IOException {
        test("test_imports_02");
    }

    public void test_import_03() throws IOException {
        test("test_imports_03");
    }
}