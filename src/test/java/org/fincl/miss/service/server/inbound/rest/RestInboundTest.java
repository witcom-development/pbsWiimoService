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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
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
public class RestInboundTest {
    
    // private RestTemplate restTemplate;
    // private HttpMessageConverterExtractor<ArrayList> responseExtractor;
    private static Logger logger = Logger.getLogger(RestInboundTest.class);
    
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
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testGetEmployeeAsXml() throws Exception {
        
        final String fullUrl = "http://localhost:9921/miss-service/services/rest/invoke?format=xml";
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverterList = restTemplate.getMessageConverters();
        
        String result = restTemplate.getForObject(fullUrl, String.class);
        
        System.out.println("result!!!!:::" + result);
    }
    
    @Test
    public void testGetEmployeeAsJson() throws Exception {
        
        final String fullUrl = "http://localhost:8080/miss-service/services/rest/invoke?format=json";
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverterList = restTemplate.getMessageConverters();
        
        // Set HTTP Message converter using a JSON implementation.
        MappingJacksonHttpMessageConverter jsonMessageConverter = new MappingJacksonHttpMessageConverter();
        
        // Add supported media type returned by BI API.
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(new MediaType("application", "json"));
        jsonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        messageConverterList.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverterList);
        
        List list = restTemplate.getForObject(fullUrl, ArrayList.class);
        
        System.out.println(list);
    }
    
    @Test
    public void getServiceStatusXML() {
        final String fullUrl = "http://localhost:9930/miss-service/services/rest/invoke";
        RestTemplate restTemplate = new RestTemplate();
        // List<HttpMessageConverter<?>> messageConverterList = restTemplate.getMessageConverters();
        //
        // // Set HTTP Message converter using a JSON implementation.
        // MappingJacksonHttpMessageConverter jsonMessageConverter = new MappingJacksonHttpMessageConverter();
        //
        // // Add supported media type returned by BI API.
        // List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        // supportedMediaTypes.add(new MediaType("application", "json"));
        // jsonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        // messageConverterList.add(jsonMessageConverter);
        // restTemplate.setMessageConverters(messageConverterList);
        
        // List list = restTemplate.getForObject(fullUrl, ArrayList.class);
        
        Map m = new HashMap();
        m.put("servcieId", "SVR_CTRL_GET_STATUS");
        
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("serviceId", "SVR_CTRL_GET_STATUS");
        
        parameters.add("paramServiceId", "SVR_CTRL_START_SERVCIE");
        
        // Map m = new HashMap();
        
        // ResponseEntity<String> rst = restTemplate.postForEntity(fullUrl, m, String.class);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.setContentType(new MediaType("application", "xml", Charset.forName("UTF-8")));
        
        HttpEntity<String> entity = new HttpEntity<String>("<sn>ggggg</sn>", headers);
        
        ResponseEntity<String> aa = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);
        
        System.out.println(aa.getBody());
    }
    
    @Test
    public void getServiceStatusJSON() {
        final String fullUrl = "http://localhost:9930/miss-service/services/rest/invoke";
        RestTemplate restTemplate = new RestTemplate();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            // List<HttpMessageConverter<?>> messageConverterList = restTemplate.getMessageConverters();
            //
            // // Set HTTP Message converter using a JSON implementation.
            // MappingJacksonHttpMessageConverter jsonMessageConverter = new MappingJacksonHttpMessageConverter();
            //
            // // Add supported media type returned by BI API.
            // List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
            // supportedMediaTypes.add(new MediaType("application", "json"));
            // jsonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
            // messageConverterList.add(jsonMessageConverter);
            // restTemplate.setMessageConverters(messageConverterList);
            
            // List list = restTemplate.getForObject(fullUrl, ArrayList.class);
            
            Map m = new HashMap();
            m.put("sn", "abcd");
            m.put("caller", "강민관");
            
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
            parameters.add("serviceId", "SVR_CTRL_GET_STATUS");
            
            parameters.add("paramServiceId", "SVR_CTRL_START_SERVCIE");
            
            String ssss = restTemplate.postForObject(fullUrl, m, String.class);
            
            System.out.println(ssss);
        }
        long end = System.currentTimeMillis();
        System.out.println("Elapse : " + (end - start) + "ms");
        
        logger.debug("dddd");
    }
    
}
