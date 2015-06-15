package com.freelancer.whatsbeef.dao;

import com.freelancer.whatsbeef.beans.Response;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



public class DataRetriever {



    public static Response getPrograms(int offset) throws Exception{
        final String rootURL = "http://whatsbeef.net/wabz/guide.php?start="+offset;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(rootURL);

        HttpResponse httpResponse = client.execute(get);
        HttpEntity entity = httpResponse.getEntity();
        String responseText = EntityUtils.toString(entity);

        Gson gson = new Gson();
        Response response = gson.fromJson(responseText,Response.class);
        return response;
    }

}
