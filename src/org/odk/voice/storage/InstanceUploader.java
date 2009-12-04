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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.db.DbAdapter.InstanceBinary;


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
  
  public int uploadInstance(int instanceId){
          // configure connection
          HttpParams params = new BasicHttpParams();
          HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
          HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
          HttpClientParams.setRedirecting(params, false);

          // setup client
          DefaultHttpClient httpclient = new DefaultHttpClient(params);
          HttpPost httppost = new HttpPost(serverUrl);

//          // get instance file
//          File instanceDir = new File(instancePath);
//
//          // find all files in parent directory
//          File[] files = instanceDir.listFiles();
//          if (files == null) {
//              log.warn("No files to upload in " + instancePath);
//          }

          // mime post
          MultipartEntity entity = new MultipartEntity();
          
          // using file storage
//          for (int j = 0; j < files.length; j++) {
//              File f = files[j];
//              if (f.getName().endsWith(".xml")) {
//                  // uploading xml file
//                  entity.addPart("xml_submission_file", new FileBody(f,"text/xml"));
//                  log.info("added xml file " + f.getName());
//              } else if (f.getName().endsWith(".wav")) {
//                  // upload audio file
//                  entity.addPart(f.getName(), new FileBody(f, "audio/wav"));
//                  log.info("added audio file" + f.getName());
//              } else {
//                log.info("unsupported file type, not adding file: " + f.getName());
//              }
//          }
          
          // using database storage
          
          DbAdapter dba = null;
          try {
            dba = new DbAdapter();
            byte[] xml = dba.getInstanceXml(instanceId);
            if (xml == null) {
              log.error("No XML for instanceId " + instanceId);
              return STATUS_ERROR;
            }
            entity.addPart("xml_submission_file", new InputStreamBody(new ByteArrayInputStream(xml),"text/xml"));
            List<InstanceBinary> binaries = dba.getBinariesForInstance(instanceId);
            for (InstanceBinary b : binaries) {
              entity.addPart(b.name, new InputStreamBody(new ByteArrayInputStream(b.binary), b.mimeType));
            }
          } catch (SQLException e) {
            log.error("SQLException uploading instance", e);
            return STATUS_ERROR;
          } finally {
            dba.close();
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
