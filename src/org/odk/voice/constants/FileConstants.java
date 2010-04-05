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


/**
 * Note: ODK Voice has moved to MySQL as a storage medium (rather than files), 
 * so these constants are mostly deprecated. However, data is still also written 
 * to files for backup.
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
     * Path to instance data storage
     */
    public static final String INSTANCES_PATH = FILE_BASE + File.separator + "instances";
    
    /**
     * Temp path
     */
    public static final String CACHE_PATH = FILE_BASE + File.separator + "cache";
    
    /**
     * Log file location
     */
    public static final String LOG_FILE = FILE_BASE + File.separator + "logs" + 
    File.separator + "odk-voice.log";
    
    public static final String CORPUS_PATH = "corpus";
    
    /**
     * See CurrentPromptServlet
     */
    public static final String CURRENT_RECORD_PROMPT_PATH = CACHE_PATH + File.separator + "currentPrompt";
    
    /**
     * Max size of an uploaded instance, in bytes.
     */
    public static final int MAX_FILE_SIZE = 10000000;

    
}
