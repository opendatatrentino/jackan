/*
 * Copyright 2015 Trento Rise.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.jackan.test.ckan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.jackan.CkanClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This client include features that probably... just don't work
 *
 * @author David Leoni
 * @since 0.4.1
 */
class ExperimentalCkanClient extends CkanClient {

    public ExperimentalCkanClient(String URL, String token, HttpHost proxy) {
        super(URL, token, proxy);
    }

    /**
     * Uploads a file using file storage api, which I think is deprecated. As of
     * Aug 2015, coesn't work neither with demo.ckan.org nor dati.trentino
     *
     * Adapted from
     * https://github.com/Ontodia/openrefine-ckan-storage-extension/blob/c99de78fd605c4754197668c9396cffd1f9a0267/src/org/deri/orefine/ckan/StorageApiProxy.java#L34
     */
    public String uploadFile(String fileContent, String fileLabel) {
        HttpResponse formFields = null;
        try {
            String filekey = null;
            HttpClient client = new DefaultHttpClient();

            //	get the form fields required from ckan storage
            // notice if you put '3/' it gives not found :-/
            String formUrl = getCatalogUrl() + "/api/storage/auth/form/file/" + fileLabel;
            HttpGet getFormFields = new HttpGet(formUrl);
            getFormFields.setHeader("Authorization", getCkanToken());
            formFields = client.execute(getFormFields);
            HttpEntity entity = formFields.getEntity();
            if (entity != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                entity.writeTo(os);

                //now parse JSON
                //JSONObject obj = new JSONObject(os.toString());
                JsonNode obj = new ObjectMapper().readTree(os.toString());

                //post the file now
                String uploadFileUrl = getCatalogUrl() + obj.get("action").asText();
                HttpPost postFile = new HttpPost(uploadFileUrl);
                postFile.setHeader("Authorization", getCkanToken());
                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.STRICT);

                //JSONArray fields = obj.getJSONArray("fields");
                Iterator<JsonNode> fields = obj.get("fields").elements();
                while (fields.hasNext()) {
                    JsonNode fieldObj = fields.next();
                    //JSONObject fieldObj = fields.getJSONObject(i);
                    String fieldName = fieldObj.get("name").asText();
                    String fieldValue = fieldObj.get("value").asText();
                    if (fieldName.equals("key")) {
                        filekey = fieldValue;
                    }
                    mpEntity.addPart(fieldName, new StringBody(fieldValue, "multipart/form-data", Charset.forName("UTF-8")));
                }

                /*
                 for (int i = 0; i < fields.length(); i++) {
                 //JSONObject fieldObj = fields.getJSONObject(i);
                 JSONObject fieldObj = fields.getJSONObject(i);
                 String fieldName = fieldObj.getString("name");
                 String fieldValue = fieldObj.getString("value");
                 if (fieldName.equals("key")) {
                 filekey = fieldValue;
                 }
                 mpEntity.addPart(fieldName, new StringBody(fieldValue, "multipart/form-data", Charset.forName("UTF-8")));                    
                 }
                 */
                //	assure that we got the file key
                if (filekey == null) {
                    throw new RuntimeException("failed to get the file key from CKAN storage form API. the response from " + formUrl + " was: " + os.toString());
                }

                //the file should be the last part
                //hack... StringBody didn't work with large files
                mpEntity.addPart("file", new ByteArrayBody(fileContent.getBytes(Charset.forName("UTF-8")), "multipart/form-data", fileLabel));

                postFile.setEntity(mpEntity);

                HttpResponse fileUploadResponse = client.execute(postFile);

                //check if the response status code was in the 200 range
                if (fileUploadResponse.getStatusLine().getStatusCode() < 200 || fileUploadResponse.getStatusLine().getStatusCode() >= 300) {
                    throw new RuntimeException("failed to add the file to CKAN storage. response status line from " + uploadFileUrl + " was: " + fileUploadResponse.getStatusLine());
                }
                return getCatalogUrl() + "/storage/f/" + filekey;

                //return CKAN_STORAGE_FILES_BASE_URI + filekey;
            }
            throw new RuntimeException("failed to get form details from CKAN storage. response line was: " + formFields.getStatusLine());
        }
        catch (IOException ioe) {
            throw new RuntimeException("failed to upload file to CKAN Storage ", ioe);
        }
    }

    public static ExperimentalCkanClient of(CkanClient client) {
        return new ExperimentalCkanClient(client.getCatalogUrl(), client.getCkanToken(), client.getProxy());
    }
}
