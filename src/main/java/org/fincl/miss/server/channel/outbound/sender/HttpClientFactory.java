package org.fincl.miss.server.channel.outbound.sender;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientFactory {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private PoolingHttpClientConnectionManager httpConnectionManager;
    
    private int socketTimeout = 50;
    
    private int connectTimeout = 50;
    
    public HttpClient getClient(int timeout) {
        this.socketTimeout = timeout;
        this.connectTimeout = timeout;
        
        return getClient();
    }
    
    public HttpClient getClient() {
        SSLContextBuilder builder = SSLContexts.custom();
        try {
            builder.loadTrustMaterial(null, new TrustStrategy() {
                
                @Override
                public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                    // TODO Auto-generated method stub
                    return true;
                }
            });
        }
        catch (NoSuchAlgorithmException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (KeyStoreException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        SSLContext sslContext = null;
        try {
            sslContext = builder.build();
        }
        catch (KeyManagementException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (NoSuchAlgorithmException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }
            
            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }
            
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
            
            @Override
            public void verify(String arg0, java.security.cert.X509Certificate arg1) throws SSLException {
                
            }
        });
        
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
        if (httpConnectionManager == null) {
            httpConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        }
        
        httpConnectionManager.setMaxTotal(200);
        httpConnectionManager.setDefaultMaxPerRoute(20);
        
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout * 1000).setConnectTimeout(connectTimeout * 1000).setExpectContinueEnabled(false).setStaleConnectionCheckEnabled(true).build();
        
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
        // HttpClient httpPoolClient = HttpClients.custom().setConnectionManager(cm).setRedirectStrategy(redirectStrategy).build();
        HttpClient httpPoolClient = HttpClientBuilder.create().setConnectionManager(httpConnectionManager).setDefaultRequestConfig(requestConfig).setRedirectStrategy(redirectStrategy).build(); // 이것으로 해야 301/302이에 대한 Redirect 전략이 적용됨.
        
        return httpPoolClient;
    }
    
    public PoolingHttpClientConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }
    
    public void setHttpConnectionManager(PoolingHttpClientConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }
    
}
