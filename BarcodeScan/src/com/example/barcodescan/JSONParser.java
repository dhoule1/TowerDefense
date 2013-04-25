package com.example.barcodescan;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JSONParser {
	
	private AppPreferences preferences;
	
	public JSONParser(Context context) {
		preferences = new AppPreferences(context);
	}

    public JSONObject getForJSON(String url, List<NameValuePair> parameters, Header[] headers) {
        JSONObject responseJSON = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();

            if (parameters != null) {
                String parameterString = URLEncodedUtils.format(parameters, "utf-8");
                url += "?" + parameterString;
            }

            HttpGet httpGet = new HttpGet(url);

            if (headers != null) {
                httpGet.setHeaders(headers);
            }

            httpGet.addHeader("Accept", "application/json");

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();

            String output = EntityUtils.toString(responseEntity);
            System.out.println("responseEntity: " +output);

            responseJSON = new JSONObject(output);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responseJSON;
    }

	public JSONObject postForJSON(String url, MultipartEntity multipartEntity, Header[] headers) {
        JSONObject responseJSON = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(multipartEntity);

            if (headers != null) {
                httpPost.setHeaders(headers);
            }

            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Authorization", "Token token=\""+preferences.getAPIKey()+"\"");

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            String output = EntityUtils.toString(responseEntity);
            System.out.println("responseEntity: " +output);

            responseJSON = new JSONObject(output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responseJSON;
	}
}