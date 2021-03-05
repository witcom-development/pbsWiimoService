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
package org.fincl.miss.service.server.inbound.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * RestHttpClientTest.java: Functional Test to test the REST HTTP Path usage. This test requires
 * rest-http application running in HTTP environment.
 * 
 * @author Vigil Bose
 */
// @RunWith(SpringJUnit4ClassRunner.class)
public class TcpInboundTest {
    
    private static Logger logger = Logger.getLogger(TcpInboundTest.class);
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSendMessage() throws Exception {
        
        String host = "localhost";
        String port = "9991";
        
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new TcpClientInitializer(host, port, null));
            
            // Start the connection attempt.
            ChannelFuture f = b.connect(host, Integer.parseInt(port)).sync();
            
            Channel ch = f.channel();
            
            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            
            Map<String, Integer> field = new LinkedHashMap<String, Integer>();
            
            field.put("STTL_CMRS_YN", 1);
            field.put("STTL_ENCP_DCD", 1);
            field.put("STTL_LNGG_DCD", 2);
            field.put("STTL_VER_DSNC", 3);
            field.put("WHBN_STTL_WRTN_YMD", 8);
            field.put("WHBN_STTL_CRET_SYS_NM", 8);
            field.put("WHBN_STTL_SRN", 14);
            field.put("WHBN_STTL_PGRS_DSNC_NO", 4);
            field.put("RQST_RCV_SVC_ID", 12);
            
            Map<String, String> value = new LinkedHashMap<String, String>();
            
            value.put("STTL_CMRS_YN", "Y");
            value.put("STTL_ENCP_DCD", "1");
            value.put("STTL_LNGG_DCD", "EN");
            value.put("STTL_VER_DSNC", "024");
            value.put("WHBN_STTL_WRTN_YMD", "20150730");
            value.put("WHBN_STTL_CRET_SYS_NM", "T1234890");
            value.put("WHBN_STTL_SRN", "instance0909");
            value.put("WHBN_STTL_PGRS_DSNC_NO", "0909");
            value.put("RQST_RCV_SVC_ID", "SVC_TEST");
            
            ByteBuffer buffer = ByteBuffer.allocate(53);
            buffer.clear();
            Iterator<String> it = value.keySet().iterator();
            
            int offset = 0;
            System.out.println("aaaaaaaaaaaaaa--g--");
            while (it.hasNext()) {
                String sKey = it.next();
                int length = field.get(sKey);
                byte[] bv = new byte[length];
                System.arraycopy(value.get(sKey).getBytes(), 0, bv, 0, value.get(sKey).getBytes().length);
                // value.get(sKey).getBytes();
                
                System.out.println("[" + sKey + "] offset[" + offset + "] length[" + length + "]");
                
                // buffer.put(bv, offset, length);
                buffer.put(bv);
                offset += length;
            }
            
            System.out.println("bbbbb----");
            String msg = new String(buffer.array());
            msg = "NNKO02420150106tkfcpfp1000000000000210000000020150106tkfcpfp1000000000000210000172.18.202.35                                      T20150106103119380N999KFCxxxxxxxx20150106103119380S00000KFCPFPFEPO00026827SN020150106103119380                                    00000000                                       00000                                             KFCPFPPFP                       N20150106tkfcpfp10000000000002100                      OLTKFC                                  100000000000001000SBGRKREG                                                                                                                                                                                        MC000124         0000         1        22                        0000000              000000000000000000000000000000000000000000000 NC000597                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     IO001324CGFCBRGRTKIUPSES037073        B000014707                 201501061031199001 0000                                                                                진치근                             0131928888888888888                                                            000009                                                               0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                                                                                       0000000000000                                        @@";
            System.out.println("msg[" + msg + "]");
            // lastWriteFuture = ch.writeAndFlush(msg.getBytes());
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                // // Sends the received line to the server.
                // String msg = "1234567890-" + i;
                System.out.println("client request message [" + msg + " " + i + "]");
                lastWriteFuture = ch.writeAndFlush(msg.getBytes());
            }
            long end = System.currentTimeMillis();
            
            System.out.println("Elapse : [" + (end - start) + "]ms");
            lastWriteFuture.sync();
            System.out.println("aaaaaaaaaaaaaaaa--------------------1111");
            // f.addListener(ChannelFutureListener.CLOSE);
            f.channel().closeFuture().sync();
            // f.channel().closeFuture().awaitUninterruptibly();
            
            System.out.println("aaaaaaaaaaaaaaaa--------------------end");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            group.shutdownGracefully();
        }
        
        System.out.println("aaaaaaaaaaaaaaaa--------------------shutdownGracefully");
    }
}
