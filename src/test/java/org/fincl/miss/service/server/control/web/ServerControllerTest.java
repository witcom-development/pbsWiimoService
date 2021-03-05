package org.fincl.miss.service.server.control.web;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class ServerControllerTest {
    
    HttpClient client = null;
    HttpPost post = null;
    String prefixUrl = "http://localhost:8080/miss-service/server/";
    
    @Before
    public void setup() {
        client = HttpClientBuilder.create().build();
    }
    
    @After
    public void result() throws ClientProtocolException, IOException {
        HttpResponse httpResponse = client.execute(post);
        System.out.println("Response Code : " + httpResponse.getStatusLine().getStatusCode());
        
        String response = EntityUtils.toString(httpResponse.getEntity());
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(response)));
    }
    
    @Test
    public void testGetInBoundChannelList() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "getInBoundChannelList.json");
        
    }
    
    @Test
    public void testGetInBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "getInBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("serverId", "test1"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testAddInBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "addInBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        urlParameters.add(new BasicNameValuePair("port", "9990"));
        urlParameters.add(new BasicNameValuePair("protocolType", "TCP"));
        urlParameters.add(new BasicNameValuePair("ssl", "false"));
        urlParameters.add(new BasicNameValuePair("autoStart", "true"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testRemoveInBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "removeInBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("serverId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testGetInBoundChannelRunningStatus() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "getInBoundChannelRunningStatus.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("serverId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testStartupInBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "startupInBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("serverId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testShutdownInBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "shutdownInBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("serverId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    // //
    @Test
    public void testGetOutBoundChannelList() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "getOutBoundChannelList.json");
        
    }
    
    @Test
    public void testGetOutBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "getOutBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testAddOutBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "addOutBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        urlParameters.add(new BasicNameValuePair("host", "127.0.0.1"));
        urlParameters.add(new BasicNameValuePair("port", "9910"));
        urlParameters.add(new BasicNameValuePair("protocolType", "TCP"));
        urlParameters.add(new BasicNameValuePair("ssl", "false"));
        urlParameters.add(new BasicNameValuePair("autoStart", "true"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testRemoveOutBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "removeOutBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testGetOutBoundChannelRunningStatus() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "getOutBoundChannelRunningStatus.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testStartupOutBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "startupOutBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
    
    @Test
    public void testShutdownOutBoundChannel() throws ClientProtocolException, IOException {
        post = new HttpPost(prefixUrl + "shutdownOutBoundChannel.json");
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("channelId", "test9"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
    }
}