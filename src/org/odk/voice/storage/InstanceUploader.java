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
package org.odk.voice.storage;

import java.io.File;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import sun.rmi.runtime.Log;


/**
 * Class for uploading completed forms.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Adam Lerer (adam.lerer@gmail.com)
 * 
 */
public class InstanceUploader  {

  private static final int CONNECTION_TIMEOUT = 50000;

  public static final int STATUS_OK = 0;
  public static final int STATUS_ERROR = 1;

  private static org.apache.log4j.Logger log = Logger
  .getLogger(InstanceUploader.class);
  
  private String serverUrl;

  public void setServerUrl(String serverUrl){
    this.serverUrl = serverUrl;
  }
  
  public int uploadInstance(String instancePath){
          // configure connection
          HttpParams params = new BasicHttpParams();
          HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
          HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
          HttpClientParams.setRedirecting(params, false);

          // setup client
          DefaultHttpClient httpclient = new DefaultHttpClient(params);
          HttpPost httppost = new HttpPost(serverUrl);

          // get instance file
          File instanceDir = new File(instancePath);

          // find all files in parent directory
          File[] files = instanceDir.listFiles();
          if (files == null) {
              log.warn("No files to upload in " + instancePath);
          }

          // mime post
          MultipartEntity entity = new MultipartEntity();
          for (int j = 0; j < files.length; j++) {
              File f = files[j];
              if (f.getName().endsWith(".xml")) {
                  // uploading xml file
                  entity.addPart("xml_submission_file", new FileBody(f));
                  log.info("added xml file " + f.getName());
              } else if (f.getName().endsWith(".wav")) {
                  // upload audio file
                  entity.addPart(f.getName(), new FileBody(f));
                  log.info("added audio file" + f.getName());
              } else {
                log.info("unsupported file type, not adding file: " + f.getName());
              }
          }
          httppost.setEntity(entity);

          // prepare response and return uploaded
          HttpResponse response = null;
          try {
              response = httpclient.execute(httppost);
          } catch (ClientProtocolException e) {
              log.error(e);
              return STATUS_ERROR;
          } catch (IOException e) {
              log.error(e);
              return STATUS_ERROR;
          } catch (IllegalStateException e) {
              log.error(e);
              return STATUS_ERROR;
          }

          // check response.
          // TODO: This isn't handled correctly.
          String responseUrl = null;
          Header[] h = response.getHeaders("Location");
          if (h != null && h.length > 0) {
              responseUrl = h[0].getValue();
          } else {
              // something should be done here...
            log.error("Location header was absent");
          }
          int responseCode = response.getStatusLine().getStatusCode();
          log.info("Response code:" + responseCode);


          // verify that your response came from a known server
          if (responseUrl != null && serverUrl.contains(responseUrl) && responseCode == 201) {
              return STATUS_OK;
          }
          return STATUS_ERROR;

  }
}
