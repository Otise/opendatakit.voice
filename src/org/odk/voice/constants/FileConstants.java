/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.voice.constants;

import java.io.File;
import java.util.Properties;


/**
 * File constants.
 * 
 * @author Adam Lerer (adam.lerer@gmail.com)
 * 
 */
public class FileConstants {
  
    
    public static final String FILE_BASE = System.getProperty("user.home") + File.separator + "odkvoicedata";

    /**
     * Forms storage path
     */
    public static final String FORMS_PATH = FILE_BASE + File.separator + "forms";

    /**
     * Prompt audio storage path
     */
    public static final String PROMPT_AUDIO_PATH = FILE_BASE + File.separator + "audio";
    /**
     * Temp path
     */
    public static final String CACHE_PATH = FILE_BASE + File.separator + "cache";
    
    /**
     * See CurrentPromptServlet
     */
    public static final String CURRENT_RECORD_PROMPT_PATH = CACHE_PATH + File.separator + "currentPrompt";
    
    public static final int MAX_FILE_SIZE = 10000000;

    public static final String INSTANCES_PATH = FILE_BASE + File.separator + "instances";
    
    public static final String UPLOAD_URL = "http://open-data-kit.appspot.com/submission";
}
