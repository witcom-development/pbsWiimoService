package org.fincl.miss.server.message.parser.telegram.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IOVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IO_FIELDVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TelegramQueryInfo;
import org.fincl.miss.server.message.parser.telegram.model.ChunkModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldGroupModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldNodeModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
 
/**
 * - DB 에서 header Field 정보를 생성 -
 * 
 */
@Component
@Scope("prototype")
public class TelegramBodyFactory implements Serializable {
 
    /**
     * 
     */
    private static final long serialVersionUID = 6646553996220668009L;
    
    // private static Map<String, TelegramBodyFactory> configMap = null;
    // static {
    // configMap = TelegramHazelInstance.getTelegramBodyMap("BODY_MAP");
    // }
    //
    
    private List<FieldModel> fieldList = null;
    
    private ChunkModel chunk = null;
    
    public void setFieldList(List<FieldModel> fieldList) {
        this.fieldList = fieldList;
    }
    
    public TelegramBodyFactory() {
    }
    
    public synchronized static TelegramBodyFactory getInstance(ChunkModel chunkModel, int type) throws MessageParserException {
        
        // ID 별로 factory 를 구현한다.
        String singletonKey = chunkModel.getId(); // IF ID //FEPO000268271
        
        TelegramBodyFactory rcf = TelegramHazelInstance.getTelegramBodyMap().get(singletonKey);
        
        if (rcf == null) {
            rcf = ApplicationContextSupport.getBean(TelegramBodyFactory.class);
            // rcf.makeFileds(chunkModel);
            rcf.setChunk(chunkModel);
            TelegramHazelInstance.getTelegramBodyMap().put(singletonKey, rcf);
        }
        return rcf;
    }
    public synchronized static void reLoadBodyInstance(String ifId) throws MessageParserException { 
        String singletonKey = ifId; // IF ID 
        TelegramBodyFactory rcf = TelegramHazelInstance.getTelegramBodyMap().get(singletonKey);
        if (rcf != null) {
            TelegramHazelInstance.getTelegramBodyMap().remove(singletonKey);
        } 
    }
//    public synchronized static ChunkModel getBodyChunk(String ifId) throws Exception { 
//        String singletonKey = ifId; // IF ID 
//        TelegramBodyFactory rcf = TelegramHazelInstance.getTelegramBodyMap("BODY_MAP").get(singletonKey+1);  //1 은 outBound 임
// 
//        if(rcf==null) return null;
//        return rcf.getChunk();
//        
//    }
    
    public ChunkModel getChunk() {
        return chunk;
    }
    
    public void setChunk(ChunkModel chunk) {
        this.chunk = chunk;
    }
    
    public List<FieldModel> makeFileds(ChunkModel chunkModel, int type) throws MessageParserException {
        String singletonKey = chunkModel.getId();
   
        synchronized (singletonKey) {
            if (fieldList != null) {
                setBasicChunkInfo(chunkModel);
                return fieldList;
            }
            else {
                fieldList = new ArrayList<FieldModel>();
                createFieldModel(chunkModel, type);
            }
        } 
 
        return fieldList;
    }
    
    private void setBasicChunkInfo(ChunkModel chunkModel) throws MessageParserException {
        chunkModel.setName(chunk.getName());
        chunkModel.setLength_field_id(chunk.getLength_field_id());
        chunkModel.setLength_gap(chunk.getLength_gap());
        chunkModel.setDelim(chunk.getDelim());
        chunkModel.setVersion(chunk.getVersion());
//        chunkModel.setTxSystem(chunk.getTxSystem());
//        chunkModel.setTxType(chunk.getTxType());
    }
    
    private void createFieldModel(ChunkModel chunkModel, int type) throws MessageParserException {
        
            
        setBodyAttribute(chunkModel,type);// 헤더정보
        // inbound
        // outBound
        // 필드의 상세정보를 저장 한다.
        
        // chunkModel.getId() = id+messageType
        String id = chunkModel.getId();
        TB_IFS_TLGM_IO_FIELDVo vo = new TB_IFS_TLGM_IO_FIELDVo();
        vo.setIfId(id);
        vo.setIoType(type);
        vo.setIoDepth(1);
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        List<TB_IFS_TLGM_IO_FIELDVo> bodyFieldList = ti.getBodyFieldList(vo);
        
        setFieldInfo(bodyFieldList, chunkModel, type);
 
    }
    
    // 필드의 대표정보를 저장 한다.
    private void setBodyAttribute(ChunkModel chunkModel,int type) {
        String id = chunkModel.getId();
        
  
        TB_IFS_TLGM_IOVo vo = new TB_IFS_TLGM_IOVo();
        vo.setIfId(id);
        vo.setMessageType(type);
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        vo = ti.getBodyIo(vo);
         
        chunkModel.setName(vo.getIoName());
        chunkModel.setDelim(vo.getDelimiterCd());
        chunkModel.setVersion(vo.getIoVersion());
        
    }
    
    // private void setFieldInfo(Node node, ChunkModel chunkModel) throws Exception {
    
    private void setFieldInfo(List<TB_IFS_TLGM_IO_FIELDVo> bodyFieldList, ChunkModel chunkModel, int type) throws MessageParserException {
        
        for (int i = 0; i < bodyFieldList.size(); i++) {
            TB_IFS_TLGM_IO_FIELDVo vo = bodyFieldList.get(i);
            if (vo.getDataType().equals(TelegramConstant.TELE_FIELD_TYPE_ARRAY)) {
                
                // TODO Auto-generated method stub
                FieldGroupModel group = new FieldGroupModel();
                group.setTeleType(TelegramConstant.TELE_BODY_TYPE);
                group.setParent(chunkModel);
                fieldList.add(group); 
                group.setId(vo.getIoField());
                group.setName(vo.getIoFieldName());
                group.setLength_filed_id(vo.getDataArrayField());
                setGroupInfo(chunkModel, group, vo, vo.getIoDepth(), type);
            }
            else {
                FieldModel model = new FieldNodeModel();
                model.setParent(chunkModel);
                model.setTeleType(TelegramConstant.TELE_BODY_TYPE); 
                setFieldModel(vo, model, type);
                if (chunkModel.getDelim() != null && !"".equals(chunkModel.getDelim())) model.setDelim(chunkModel.getDelim());
                fieldList.add(model);
            }
        }
    }
    
    private void setGroupInfo(ChunkModel chunkModel, FieldGroupModel group, TB_IFS_TLGM_IO_FIELDVo groupVo, int HeaderDepth, int type) throws MessageParserException {
        
        // 그룹정보 셋팅
        
        TB_IFS_TLGM_IO_FIELDVo vo = new TB_IFS_TLGM_IO_FIELDVo();
        vo.setIoId(groupVo.getIoId());
        vo.setIoType(type);
        vo.setIoDepth(HeaderDepth + 1);
        vo.setParentIoOrder(groupVo.getIoOrder());
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        List<TB_IFS_TLGM_IO_FIELDVo> bodyFieldList = ti.getBodyFieldDeepsList(vo);
        
        for (int k = 0; k < bodyFieldList.size(); k++) {
            TB_IFS_TLGM_IO_FIELDVo childvo = bodyFieldList.get(k);
            if (childvo.getDataType().equals(TelegramConstant.TELE_FIELD_TYPE_ARRAY)) {
                FieldGroupModel model = new FieldGroupModel();
                model.setId(childvo.getIoField());
                model.setName(childvo.getIoFieldName());
                model.setLength_filed_id(childvo.getDataArrayField());
                model.setTeleType(TelegramConstant.TELE_BODY_TYPE);
                group.addFieldModel(model);
                model.setParent(group);
                setGroupInfo(chunkModel, model, childvo, childvo.getIoDepth(), type);
            }
            else {
                FieldNodeModel model = new FieldNodeModel();
                model.setParent(group);
                model.setTeleType(TelegramConstant.TELE_BODY_TYPE);
                setFieldModel(childvo, model, type);
                group.addFieldModel(model);
            }
        }
    }
    
    private void setFieldModel(TB_IFS_TLGM_IO_FIELDVo vo, FieldModel model, int type) throws MessageParserException {
        
        model.setId(vo.getIoField());
        ((FieldNodeModel) model).setName(vo.getIoFieldName());
        ((FieldNodeModel) model).setType(vo.getDataType());
        ((FieldNodeModel) model).setLength(vo.getDataLength());
        ((FieldNodeModel) model).setTeleType(TelegramConstant.TELE_BODY_TYPE);
        ((FieldNodeModel) model).setInit_value(vo.getDefaultDataValue()); 
        ((FieldNodeModel) model).setEssentialYn(vo.getEssentialYn()); 
        ((FieldNodeModel) model).setFrac_len(vo.getDataSize()); 
    }
    
}
