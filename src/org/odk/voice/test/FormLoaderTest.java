package org.odk.voice.test;

import junit.framework.TestCase;

import org.odk.voice.storage.FormLoader;
import org.odk.voice.xform.FormHandler;

public class FormLoaderTest extends TestCase {

	public void testGetFormHandler(){
		FormHandler fh = FormLoader.getFormHandler("C:\\widgets.xml", null);
		assertNotNull(fh);
	}
}
