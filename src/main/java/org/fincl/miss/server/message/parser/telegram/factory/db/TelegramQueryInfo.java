package org.fincl.miss.server.message.parser.telegram.factory.db;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 
 * - 전문을 처리하기 위한 쿼리 클래스
 * 
 *
 */
@Component   
public class TelegramQueryInfo {
 
        @Autowired
        private TelegramMapper telegramMapper;
        public List<TB_IFS_TLGM_IO_FIELDVo> getBodyFieldList(TB_IFS_TLGM_IO_FIELDVo vo ){
            return telegramMapper.getBodyFieldList(vo);
        }
        public TB_IFS_TLGM_IOVo getBodyIo(TB_IFS_TLGM_IOVo vo ){
            return telegramMapper.getBodyIo(vo);
        }


        public List<TB_IFS_TLGM_IO_FIELDVo> getBodyFieldDeepsList(TB_IFS_TLGM_IO_FIELDVo vo ){
            return telegramMapper.getBodyFieldDeepsList(vo);
        }
        public List<TB_IFS_TLGM_HEADER_FIELDVo> getHeaderFieldList(TB_IFS_TLGM_HEADER_FIELDVo vo ){
            return telegramMapper.getHeaderFieldList(vo);
        }
        public TB_IFS_TLGM_HEADERVo getHeader(TB_IFS_TLGM_HEADERVo vo ){
            return telegramMapper.getHeader(vo);
        }
        public List<TB_IFS_TLGM_HEADER_FIELDVo> getHeaderFieldDeepsList(TB_IFS_TLGM_HEADER_FIELDVo vo ){
            return telegramMapper.getHeaderFieldDeepsList(vo);
        }
        public TB_IFS_TLGM_IFVo getInterfaceInfo(TB_IFS_TLGM_IFVo vo ){
            return telegramMapper.getInterfaceInfo(vo);
        } 
        
        public int modifyParseInterfaceInfo(TB_IFS_TLGM_IFVo vo){
            return telegramMapper.modifyParseInterfaceInfo(vo); 
        }
        
        public List<TB_IFS_TLGM_HEADER_RELVo> getHeaderRelList(){
            return telegramMapper.getHeaderRelList();
        }  
        public List<TB_IFS_TLGM_HEADERVo> getHeaderRelationeList(TB_IFS_TLGM_HEADERVo vo){
            return telegramMapper.getHeaderRelationeList(vo);
        }  
        public List<TB_IFS_TLGM_HEADERVo> getDynamicHeaderList(TB_IFS_TLGM_HEADERVo vo){
            return telegramMapper.getDynamicHeaderList(vo);
        }  
        
        
        
        
        
        ///////////////////////////////////////////////////
        public TB_IFS_TLGM_IOVo getParseBodyIo(TB_IFS_TLGM_IOVo vo ){
            return telegramMapper.getParseBodyIo(vo);
        }
        
        public int deleteParseBodyIoHist(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.deleteParseBodyIoHist(vo);
        }
        public int deleteParseBodyIoFieldHist(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.deleteParseBodyIoFieldHist(vo);
        } 
        
        public int insertParseBodyIoHistBackup(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.insertParseBodyIoHistBackup(vo);
        }
        public int insertParseBodyIoFieldHistBackup(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.insertParseBodyIoFieldHistBackup(vo);
        } 
        
        public int insertParseBodyIoFieldWork(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.insertParseBodyIoFieldWork(vo);
        } 
        
        public int modifyParseBodyIoInfo(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.modifyParseBodyIoInfo(vo);
        }
        public int modifyParseBodyIoWorkInfo(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.modifyParseBodyIoWorkInfo(vo);
        }
        public int deleteParseBodyIoField(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.deleteParseBodyIoField(vo);
        }
        
        public int insertParseBodyIoField(HashMap<String, List<TB_IFS_TLGM_IO_FIELDVo>>  map){
            return telegramMapper.insertParseBodyIoField(map);
        } 
        public int deleteParseBodyIoFieldWork(TB_IFS_TLGM_IOVo vo){
            return telegramMapper.deleteParseBodyIoFieldWork(vo);
        }
        public int modifyParseChannelTx(TB_IFS_TLGM_IFVo vo){
            return telegramMapper.modifyParseChannelTx(vo);
        }
        
 }          