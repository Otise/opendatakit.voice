package org.odk.voice.storage;

import junit.framework.TestCase;

import org.odk.voice.xform.FormHandler;

public class FormLoaderTest extends TestCase {

	public void testGetFormHandler(){
		FormHandler fh = FormLoader.getFormHandler("C:\\widgets.xml", null);
		assertNotNull(fh);
	}
}
