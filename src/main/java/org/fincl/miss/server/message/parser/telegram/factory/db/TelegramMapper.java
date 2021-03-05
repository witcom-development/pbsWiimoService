package org.fincl.miss.server.message.parser.telegram.factory.db;

import java.util.HashMap;
import java.util.List;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("TelegramMapper")
public interface TelegramMapper {
 
    
	public TB_IFS_TLGM_HEADERVo getHeader(TB_IFS_TLGM_HEADERVo vo); 
    public List<TB_IFS_TLGM_HEADER_RELVo> getHeaderRelList(); 
    public List<TB_IFS_TLGM_HEADER_FIELDVo> getHeaderFieldList(TB_IFS_TLGM_HEADER_FIELDVo vo); 
    public List<TB_IFS_TLGM_HEADER_FIELDVo> getHeaderFieldDeepsList(TB_IFS_TLGM_HEADER_FIELDVo vo) ;
    public List<TB_IFS_TLGM_HEADERVo> getHeaderRelationeList(TB_IFS_TLGM_HEADERVo vo);
    public List<TB_IFS_TLGM_HEADERVo> getDynamicHeaderList(TB_IFS_TLGM_HEADERVo vo);
    
    public List<TB_IFS_TLGM_IO_FIELDVo> getBodyFieldList(TB_IFS_TLGM_IO_FIELDVo vo);    
    public TB_IFS_TLGM_IOVo getBodyIo(TB_IFS_TLGM_IOVo vo) ;
     
    public List<TB_IFS_TLGM_IO_FIELDVo> getBodyFieldDeepsList(TB_IFS_TLGM_IO_FIELDVo  vo); 
     
    public TB_IFS_TLGM_IFVo getInterfaceInfo(TB_IFS_TLGM_IFVo vo); 

  
    
 


 
     
    
     

    
    public TB_IFS_TLGM_IOVo getParseBodyIo(TB_IFS_TLGM_IOVo vo) ; 
    public int modifyParseInterfaceInfo(TB_IFS_TLGM_IFVo vo);
    public int deleteParseBodyIoHist(TB_IFS_TLGM_IOVo vo);
    public int deleteParseBodyIoFieldHist(TB_IFS_TLGM_IOVo vo); 
    public int insertParseBodyIoHistBackup(TB_IFS_TLGM_IOVo vo);
    public int insertParseBodyIoFieldHistBackup(TB_IFS_TLGM_IOVo vo);
    public int modifyParseBodyIoInfo(TB_IFS_TLGM_IOVo vo);
    public int modifyParseBodyIoWorkInfo(TB_IFS_TLGM_IOVo vo);
    public int deleteParseBodyIoField(TB_IFS_TLGM_IOVo vo);
    public int insertParseBodyIoField(HashMap<String, List<TB_IFS_TLGM_IO_FIELDVo>>  map);
    public int deleteParseBodyIoFieldWork(TB_IFS_TLGM_IOVo vo);
    public int insertParseBodyIoFieldWork(TB_IFS_TLGM_IOVo vo);
    public int modifyParseChannelTx(TB_IFS_TLGM_IFVo vo);
      
    
}
