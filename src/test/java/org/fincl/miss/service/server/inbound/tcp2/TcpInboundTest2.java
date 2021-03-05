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
package org.fincl.miss.service.server.inbound.tcp2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * RestHttpClientTest.java: Functional Test to test the REST HTTP Path usage. This test requires
 * rest-http application running in HTTP environment.
 * 
 * @author Vigil Bose
 */
// @RunWith(SpringJUnit4ClassRunner.class)
public class TcpInboundTest2 {
    
    private static Logger logger = Logger.getLogger(TcpInboundTest2.class);
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSendMessage() {
        
        String host = "125.133.65.244";
        String port = "60011";
        
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new TcpClientInitializer2(host, port, null));
            
            // Start the connection attempt.
            ChannelFuture f = b.connect(host, Integer.parseInt(port)).sync();
            
            Channel ch = f.channel();
            
            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            
            
            // 대여 대기
            ByteBuffer buffer = ByteBuffer.allocate(22);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4372"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("14"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("01"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
        /*
            // GPS 전송
            ByteBuffer buffer = ByteBuffer.allocate(53);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("0300"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("15"));
            buffer.put(DatatypeConverter.parseHexBinary("03"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("04")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("01"));//21+32
            buffer.put(DatatypeConverter.parseHexBinary("37494835"));//4
            buffer.put(DatatypeConverter.parseHexBinary("12702056"));
            buffer.put(DatatypeConverter.parseHexBinary("37493581"));
            buffer.put(DatatypeConverter.parseHexBinary("12806843"));
            buffer.put(DatatypeConverter.parseHexBinary("37494835"));
            buffer.put(DatatypeConverter.parseHexBinary("12702056"));
            buffer.put(DatatypeConverter.parseHexBinary("37493581"));
            buffer.put(DatatypeConverter.parseHexBinary("12806843"));
        */    
           /* 
            // 비밀번호 인증
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4372"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("13"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("03030303")); // 4
            */
            
           /* 
            // 카드인증
            ByteBuffer buffer = ByteBuffer.allocate(21);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4372"));//2
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            buffer.put(DatatypeConverter.parseHexBinary("04"));//1
            buffer.put(DatatypeConverter.parseHexBinary("02"));//1
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("16")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("2222445343237869")); // 8
             */
            
        /*    
         // 대여
            ByteBuffer buffer = ByteBuffer.allocate(26);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4370"));//2
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            buffer.put(DatatypeConverter.parseHexBinary("02"));//1
            buffer.put(DatatypeConverter.parseHexBinary("07"));//1
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("03030303")); // 4
            buffer.put(DatatypeConverter.parseHexBinary("01"));//1
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            */
            
            /*
            // 주기적인 상태보고
            ByteBuffer buffer = ByteBuffer.allocate(28);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("435F"));//2
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            buffer.put(DatatypeConverter.parseHexBinary("11"));//1
            buffer.put(DatatypeConverter.parseHexBinary("02"));//1
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("00")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("01"));//1
            buffer.put(DatatypeConverter.parseHexBinary("0001"));//2
            buffer.put(DatatypeConverter.parseHexBinary("0001"));//2
            buffer.put(DatatypeConverter.parseHexBinary("0001"));//2
            buffer.put(DatatypeConverter.parseHexBinary("EC"));//1
            
            */
            /*
         // 반납
            ByteBuffer buffer = ByteBuffer.allocate(26);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("436F"));//2
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            buffer.put(DatatypeConverter.parseHexBinary("03"));//1
            buffer.put(DatatypeConverter.parseHexBinary("03"));//1
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("111C"));//2
            buffer.put(DatatypeConverter.parseHexBinary("0A1E"));//2
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            buffer.put(DatatypeConverter.parseHexBinary("00"));//1
            */
            /*
         // 점검완료// 고장신고
            ByteBuffer buffer = ByteBuffer.allocate(20);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4370"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("06"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            */
            /*
            // 관리자 이동
            ByteBuffer buffer = ByteBuffer.allocate(25);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("437C"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("05"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("12345671234567")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("0101")); // 2
            buffer.put(DatatypeConverter.parseHexBinary("0101")); // 2
            */
         /*  
            // 다운로드 서버 접속
            ByteBuffer buffer = ByteBuffer.allocate(18);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4350"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("55"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("0101")); // 2
            buffer.put(DatatypeConverter.parseHexBinary("0201")); // 2
            buffer.put(DatatypeConverter.parseHexBinary("0301")); // 2
            
            */
            /*
         // f/w 다운로드 
            ByteBuffer buffer = ByteBuffer.allocate(15);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4350"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("58"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("00")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("0019")); // 2
            */
            
            
            /*
            // f/w 다운로드 완료
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4300"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("73"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("00")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("0909")); // 2
            */
            
            /*
         // f/w 업데이트 완료
            ByteBuffer buffer = ByteBuffer.allocate(15);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4300"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("76"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            buffer.put(DatatypeConverter.parseHexBinary("0909")); // 2
            */
            
            
           /* 
            ByteBuffer buffer = ByteBuffer.allocate(15);
            buffer.clear();
            buffer.put(DatatypeConverter.parseHexBinary("4300"));
            buffer.put(DatatypeConverter.parseHexBinary("00"));
            buffer.put(DatatypeConverter.parseHexBinary("67"));
            buffer.put(DatatypeConverter.parseHexBinary("02"));
            buffer.put(DatatypeConverter.parseHexBinary("01020304050607")); // 7
            buffer.put(DatatypeConverter.parseHexBinary("0909")); // 2
            buffer.put(DatatypeConverter.parseHexBinary("01")); // 1
            */
            
            
            
            
            byte[] gg = buffer.array();
            System.out.println(new String(buffer.array()));
            System.out.println(DatatypeConverter.printHexBinary(buffer.array()));
            lastWriteFuture = ch.writeAndFlush(gg);
            
//            lastWriteFuture = ch.writeAndFlush(gg);
//            lastWriteFuture = ch.writeAndFlush(gg);
            
            long end = System.currentTimeMillis();
            
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
