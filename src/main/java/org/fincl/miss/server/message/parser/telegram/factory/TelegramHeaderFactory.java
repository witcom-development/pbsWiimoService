package org.fincl.miss.server.message.parser.telegram.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_HEADERVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_HEADER_FIELDVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TelegramQueryInfo;
import org.fincl.miss.server.message.parser.telegram.model.ChunkModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldGroupModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldNodeModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * - DB 에서 body Field 정보를 생성 -
 * 
 */
@Component
@Scope("prototype")
public class TelegramHeaderFactory implements Serializable {
     
    /**
     * 
     */
    private static final long serialVersionUID = 1755375200570219457L;
 
    // TelegramHazelInstance instance = null;
    //
    // private static Map<String, TelegramHeaderFactory> configMap = null;
    // static {
    // configMap = TelegramHazelInstance.getTelegramHeaderMap("HEADER_MAP");
    // }
    
    private List<FieldModel> fieldList = null;
    
    private ChunkModel chunk = null;
    
    public void setFieldList(List<FieldModel> fieldList) {
        this.fieldList = fieldList;
    }
    
    private TelegramHeaderFactory() {
    }
    
    public synchronized static TelegramHeaderFactory getInstance(ChunkModel chunkModel, int type) throws MessageParserException {
 
        String singletonKey = chunkModel.getId();
        TelegramHeaderFactory rcf = TelegramHazelInstance.getTelegramHeaderMap().get(singletonKey);
        
        if (rcf == null) {
            rcf = ApplicationContextSupport.getBean(TelegramHeaderFactory.class);
            // rcf.makeFileds(chunkModel);
            rcf.setChunk(chunkModel);
            TelegramHazelInstance.getTelegramHeaderMap().put(singletonKey, rcf);
        }
        return rcf;
    }
    
    public synchronized static void reLoadHeaderInstance(String headerId) throws MessageParserException { 
        String singletonKey = headerId;  
        TelegramHeaderFactory rcf = TelegramHazelInstance.getTelegramHeaderMap().get(singletonKey);
        if (rcf != null) {
            TelegramHazelInstance.getTelegramHeaderMap().remove(singletonKey);
        } 
    }
    public ChunkModel getChunk() {
        return chunk;
    }
    
    public void setChunk(ChunkModel chunk) {
        this.chunk = chunk;
    }
    
    public List<FieldModel> makeFileds(ChunkModel chunkModel, int type) throws MessageParserException{
        String singletonKey = chunkModel.getId();
      
        synchronized (singletonKey) {
            if (fieldList != null) {
                setBasicChunkInfo(chunkModel);
                return fieldList;
            }
            else {
                fieldList = new ArrayList<FieldModel>();
                // /DB 처리한다. ㅌㅌㅌㅌㅌㅌ
                createFieldModel(chunkModel);
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
    
    private void createFieldModel(ChunkModel chunkModel) throws MessageParserException {
     
            
        setHeaderAttribute(chunkModel);// 헤더정보
        
        // 필드의 상세정보를 저장 한다.
        TB_IFS_TLGM_HEADER_FIELDVo vo = new TB_IFS_TLGM_HEADER_FIELDVo();
        vo.setHeaderId(chunkModel.getId());
        vo.setHeaderDepth(1);
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        List<TB_IFS_TLGM_HEADER_FIELDVo> headerFieldList = ti.getHeaderFieldList(vo);
        
        setFieldInfo(headerFieldList, chunkModel);
        
       
    }
    
    // 필드의 대표정보를 저장 한다.
    private void setHeaderAttribute(ChunkModel chunkModel) {
        TB_IFS_TLGM_HEADERVo vo = new TB_IFS_TLGM_HEADERVo();
        vo.setHeaderId(chunkModel.getId());
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        vo = ti.getHeader(vo);
        
        chunkModel.setName(vo.getHeaderName());
        // chunkModel.setLength_gap()//먼지 모르겠음
        chunkModel.setDelim(vo.getDelimiterCd());
        chunkModel.setVersion(vo.getHeaderVersion());
        
        // NamedNodeMap attrNodeList = node.getAttributes();
        // if (attrNodeList != null && attrNodeList.getLength() > 0) {
        // int attrListInx = 0;
        // for (int sizeInx = attrNodeList.getLength(); attrListInx < sizeInx; attrListInx++) {
        //
        // } else if (attrNodeList.item(attrListInx).getNodeName().equals("name")) {
        // chunkModel.setName(attrNodeList.item(attrListInx).getNodeValue());
        // } else if (attrNodeList.item(attrListInx).getNodeName().equals("length_field_id")) {
        // chunkModel.setLength_field_id(attrNodeList.item(attrListInx).getNodeValue());
        // } else if (attrNodeList.item(attrListInx).getNodeName().equals("length_gap")) {
        // chunkModel.setLength_gap(attrNodeList.item(attrListInx).getNodeValue());
        // } else if (attrNodeList.item(attrListInx).getNodeName().equals("delim")) {
        //
        // chunkModel.setDelim(attrNodeList.item(attrListInx).getNodeValue());
        // } else if (attrNodeList.item(attrListInx).getNodeName().equals("version")) {
        // chunkModel.setVersion(attrNodeList.item(attrListInx).getNodeValue());
        // }
        // }
        //
        // }
    }
    
    // private void setFieldInfo(Node node, ChunkModel chunkModel) throws Exception {
    
    private void setFieldInfo(List<TB_IFS_TLGM_HEADER_FIELDVo> headerFieldList, ChunkModel chunkModel) throws MessageParserException {
        
        for (int i = 0; i < headerFieldList.size(); i++) {
            TB_IFS_TLGM_HEADER_FIELDVo vo = headerFieldList.get(i);
            if (vo.getDataType().equals(TelegramConstant.TELE_FIELD_TYPE_ARRAY)) {
                
                // TODO Auto-generated method stub
                FieldGroupModel group = new FieldGroupModel();
                group.setParent(chunkModel);
                fieldList.add(group);
                
                group.setId(vo.getHeaderField());
                group.setName(vo.getHeaderFieldName());
                group.setLength_filed_id(vo.getDataArrayField());
                group.setTeleType(TelegramConstant.TELE_HEADER_TYPE);
                group.setHeaderId(vo.getHeaderId());
                setGroupInfo(chunkModel, group, vo);
            }
            else {
                FieldModel model = new FieldNodeModel();
                model.setParent(chunkModel);
                model.setTeleType(TelegramConstant.TELE_HEADER_TYPE);
                model.setHeaderId(vo.getHeaderId());
                setFieldModel(vo, model);
                if (chunkModel.getDelim() != null && !"".equals(chunkModel.getDelim())) model.setDelim(chunkModel.getDelim());
                fieldList.add(model);
            }
        }
        // String childNodeName = node.getNodeName();
        // FieldModel model = null;
        // NamedNodeMap attrNodeList = null;
        // if (childNodeName.equals("field")) {
        // model = new FieldNodeModel();
        // model.setParent(chunkModel);
        // if (chunkModel.getDelim() != null) model.setDelim(chunkModel.getDelim());
        // attrNodeList = node.getAttributes();
        // if (attrNodeList != null && attrNodeList.getLength() > 0) {
        // int attrListInx = 0;
        // for (int sizeInx = attrNodeList.getLength(); attrListInx < sizeInx; attrListInx++) {
        // setFieldModel(node, attrNodeList, attrListInx, model);
        // }
        // fieldList.add(model);
        // }
        // } else if (childNodeName.equals("group")) {
        // setGroupInfo(node, null, chunkModel);
        // }
        // if(node.hasChildNodes())
        // createFieldModel(node, node.getParentNode().getNodeName());
    }
    
    private void setGroupInfo(ChunkModel chunkModel, FieldGroupModel group, TB_IFS_TLGM_HEADER_FIELDVo groupVo ) throws MessageParserException {
        
        // 그룹정보 셋팅
        TB_IFS_TLGM_HEADER_FIELDVo vo = new TB_IFS_TLGM_HEADER_FIELDVo();
        vo.setHeaderId(groupVo.getHeaderId());
        vo.setHeaderDepth(groupVo.getHeaderDepth() + 1);
        vo.setParentHeaderOrder(groupVo.getHeaderOrder());
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        List<TB_IFS_TLGM_HEADER_FIELDVo> headerFieldList = ti.getHeaderFieldDeepsList(vo);
        
        for (int k = 0; k < headerFieldList.size(); k++) {
            TB_IFS_TLGM_HEADER_FIELDVo childvo = headerFieldList.get(k);
            if (childvo.getDataType().equals(TelegramConstant.TELE_FIELD_TYPE_ARRAY)) {
                FieldGroupModel model = new FieldGroupModel();
                model.setId(childvo.getHeaderField());
                model.setName(childvo.getHeaderFieldName());
                model.setLength_filed_id(childvo.getDataArrayField());
                model.setTeleType(TelegramConstant.TELE_HEADER_TYPE);
                model.setHeaderId(vo.getHeaderId());
                group.addFieldModel(model);
                model.setParent(group);
                setGroupInfo(chunkModel, model, childvo);
            }
            else {
                FieldNodeModel model = new FieldNodeModel();
                model.setParent(group);
                model.setTeleType(TelegramConstant.TELE_HEADER_TYPE);
                model.setHeaderId(vo.getHeaderId());
                setFieldModel(childvo, model);
                group.addFieldModel(model);
            }
        }
    }
    
    private void setFieldModel(TB_IFS_TLGM_HEADER_FIELDVo vo, FieldModel model) throws MessageParserException {
        
        model.setId(vo.getHeaderField());
        ((FieldNodeModel) model).setName(vo.getHeaderFieldName());
        ((FieldNodeModel) model).setType(vo.getDataType());
        ((FieldNodeModel) model).setLength(vo.getDataLength());
        ((FieldNodeModel) model).setTeleType(TelegramConstant.TELE_HEADER_TYPE);
        ((FieldNodeModel) model).setHeaderId(vo.getHeaderId());
        ((FieldNodeModel) model).setInit_value(vo.getDefaultDataValue());
        ((FieldNodeModel) model).setFrac_len(vo.getDataSize()); 
    }
    
}
