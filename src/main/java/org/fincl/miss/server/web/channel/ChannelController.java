package org.fincl.miss.server.web.channel;

import org.fincl.miss.core.viewresolver.header.HeaderParamEntity;
import org.fincl.miss.core.viewresolver.header.annotation.HeaderParam;
import org.fincl.miss.server.channel.inbound.servlet.InBoundServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("channel/*")
@Controller
public class ChannelController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private InBoundServletHandler inBoundServletHandler;
    
    @RequestMapping(value = "/{channelId}")
    public ResponseEntity<?> handle(@HeaderParam HeaderParamEntity deviceHeader, @PathVariable String channelId, HttpEntity<byte[]> requestEntity) {
        logger.debug("requestEntitya: {}", requestEntity);
        ResponseEntity<?> responseEntity = inBoundServletHandler.servletChannelRead(deviceHeader, channelId, requestEntity);
        logger.debug("responseEntitya: {}", responseEntity);
        return responseEntity;
    }
    
}
