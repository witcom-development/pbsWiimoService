/*
 * Copyright 2002-2010 the original author or authors.
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
package org.fincl.miss.service.server.inbound.rest;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * RestHttpClientTest.java: Functional Test to test the REST HTTP Path usage. This test requires
 * rest-http application running in HTTP environment.
 * 
 * @author Vigil Bose
 */
// @RunWith(SpringJUnit4ClassRunner.class)
public class ServletRestInboundTest {
    
    // private RestTemplate restTemplate;
    // private HttpMessageConverterExtractor<ArrayList> responseExtractor;
    private static Logger logger = Logger.getLogger(ServletRestInboundTest.class);
    
    // @Autowired
    // private Jaxb2Marshaller marshaller;
    // @Autowired
    // private ObjectMapper jaxbJacksonObjectMapper;
    
    // @Before
    // public void setUp() {
    // responseExtractor = new HttpMessageConverterExtractor<ArrayList>(ArrayList.class, restTemplate.getMessageConverters());
    //
    // Map<String, Object> properties = new HashMap<String, Object>();
    // properties.put(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
    // properties.put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    // marshaller.setMarshallerProperties(properties);
    // }
    
    @Test
    public void testMultiOutBoundChannelXML_XML() {
        final String fullUrl = "http://localhost:9080/miss-service/channel/CH0023";
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.setContentType(new MediaType("application", "xml", Charset.forName("UTF-8")));
        
        HttpEntity<String> entity = new HttpEntity<String>("<request><id>testid</id><serviceId>multiOutBoudndService</serviceId><param><field1>abc</field1></param></request>", headers);
        
        ResponseEntity<String> aa = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);
        
        System.out.println(aa.getBody());
    }
    
    @Test
    public void testMultiOutBoundChannelXML_JSON() {
        final String fullUrl = "http://localhost:9080/miss-service/channel/CH0024";
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        
        HttpEntity<String> entity = new HttpEntity<String>("<request><id>testid</id><serviceId>multiOutBoundService</serviceId><param><field1>abc</field1></param></request>", headers);
        
        ResponseEntity<String> aa = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);
        
        System.out.println(aa.getBody());
    }
    
    @Test
    public void testMultiOutBoundChannelJSON_JSON() {
        final String fullUrl = "http://localhost:9080/miss-service/channel/CH0021";
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        
        HttpEntity<String> entity = new HttpEntity<String>("{\"request\":{\"id\":\"testid\",\"serviceId\":\"multiOutBoundService\", \"param\": {\"field1\":\"abc\", \"field2\":[1,2,3,4],\"field3\":[1,2,3,4,5]}}}", headers);
        
        ResponseEntity<String> aa = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);
        
        System.out.println(aa.getBody());
    }
    
    @Test
    public void testMultiOutBoundChannelFORM_JSON() {
        final String fullUrl = "http://localhost:9080/miss-service/channel/CH0025";
        RestTemplate restTemplate = new RestTemplate();
        
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("serviceId", "multiOutBoundService");
        parameters.add("field1", "testfield");
        parameters.add("abcd", "basafg");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        ResponseEntity<String> aa = restTemplate.postForEntity(fullUrl, parameters, String.class);
        
        System.out.println(aa.getBody());
    }
    
    @Test
    public void testMultiOutBoundChannelFORM_XML() {
        final String fullUrl = "http://localhost:9080/miss-service/channel/CH0026";
        RestTemplate restTemplate = new RestTemplate();
        
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("serviceId", "multiOutBoundService");
        parameters.add("field1", "testfield");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        ResponseEntity<String> aa = restTemplate.postForEntity(fullUrl, parameters, String.class);
        
        System.out.println(aa.getBody());
    }
    
    @Test
    public void testMultiOutBoundChannelJSON_XML() {
        final String fullUrl = "http://localhost:9080/miss-service/channel/CH0022";
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        
        HttpEntity<String> entity = new HttpEntity<String>("{\"request\":{\"id\":\"testid\",\"serviceId\":\"multiOutBoundService\", \"param\": {\"field1\":\"abc\", \"field2\":[1,2,3,4],\"field3\":[1,2,3,4,5]}}}", headers);
        
        ResponseEntity<String> aa = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);
        
        System.out.println(aa.getBody());
    }
    
}
