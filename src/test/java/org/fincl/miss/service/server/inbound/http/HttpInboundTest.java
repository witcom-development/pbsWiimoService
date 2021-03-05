package org.fincl.miss.service.server.inbound.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpInboundTest {
    
    @Test
    public void test() throws ClientProtocolException, IOException {
        String url = "http://localhost:9925/miss-service/services/rest/invoke";
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            
            // add header
            post.setHeader("User-Agent", "kkkkk");
            
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
            urlParameters.add(new BasicNameValuePair("cn", ""));
            urlParameters.add(new BasicNameValuePair("locale", "111"));
            urlParameters.add(new BasicNameValuePair("locale", "222"));
            urlParameters.add(new BasicNameValuePair("caller", ""));
            urlParameters.add(new BasicNameValuePair("num", "12345"));
            
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            
            String aa = EntityUtils.toString(response.getEntity());
            System.out.println(aa);
        }
        long end = System.currentTimeMillis();
        System.out.println("Elapse : " + (end - start) + "ms");
    }
}