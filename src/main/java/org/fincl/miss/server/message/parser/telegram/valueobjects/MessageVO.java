package org.fincl.miss.server.message.parser.telegram.valueobjects;
  
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceFactory;
import org.fincl.miss.server.message.parser.telegram.model.BodyModel;
import org.fincl.miss.server.message.parser.telegram.model.ChunkModel;
import org.fincl.miss.server.message.parser.telegram.model.DynamicHeaderModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldGroupModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldNodeModel;
import org.fincl.miss.server.message.parser.telegram.model.HeaderModel;
import org.fincl.miss.server.message.parser.telegram.model.MessageModel;
import org.fincl.miss.server.util.EnumCode.Charset;
/** 
 * - 전문을 파싱하고 조립하기 위한 전문별 인스턴스 - 
 */

public class MessageVO implements Message {
    private static final transient Log LOG = LogFactory.getLog(MessageVO.class);
    private String id = null;
    private MessageModel model = null;
    private LinkedHashMap<String, LinkedHashMap<String, FieldVO>> header_fields = null; 
    private LinkedHashMap<String, LinkedHashMap<String, FieldVO>> dynamic_fields = null;
    
    private LinkedHashMap<String, FieldVO> body_fields = null;
    private int messageType = 0; 
    private String messageManagerId;

    private Charset charset = null;
    
    public Charset getCharset(){
        return charset;
    }
    public void setCharset(Charset charset){
        this.charset = charset;
    }
    
    

    public MessageVO(MessageModel model, int messageType) {
        this.model = model;
        this.messageType = messageType;
        header_fields = new LinkedHashMap<String, LinkedHashMap<String, FieldVO>>();
        dynamic_fields = new LinkedHashMap<String, LinkedHashMap<String, FieldVO>>();
        body_fields = new LinkedHashMap<String, FieldVO>();
    }

    public MessageVO(String id, MessageModel model, int messageType) {
        this.id = id;
        this.model = model;
        this.messageType = messageType;
        header_fields = new LinkedHashMap<String, LinkedHashMap<String, FieldVO>>();
        dynamic_fields = new LinkedHashMap<String, LinkedHashMap<String, FieldVO>>();
        body_fields = new LinkedHashMap<String, FieldVO>();
    }

    // 멤 버메소드
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    // Group(반복부) 처리를 위한 메소드

    public List<FieldGroupVO> getGroupVos() {
        List<FieldGroupVO> groups = new ArrayList<FieldGroupVO>();
        Set<String> bodyKeySet = body_fields.keySet();
        Iterator<String> keyItor = bodyKeySet.iterator();
        String key = null;
        while (keyItor.hasNext()) {
            key = keyItor.next();
            if (body_fields.get(key) instanceof FieldGroupVO) {
                groups.add((FieldGroupVO) body_fields.get(key));
            }
        }
        return groups;
    }

    public List<FieldGroupVO> getGroupVosForMerge() {
        List<FieldGroupVO> groups = new ArrayList<FieldGroupVO>();
        Set<String> bodyKeySet = body_fields.keySet();
        Iterator<String> keyItor = bodyKeySet.iterator();
        String key = null;
        while (keyItor.hasNext()) {
            key = keyItor.next();
            if (body_fields.get(key) instanceof FieldGroupVO) {
                FieldGroupVO gvo = (FieldGroupVO) body_fields.get(key);
                if (!((FieldGroupModel) gvo.getModel()).isFixedLength()) groups.add(gvo);
            }
        }
        return groups;
    }

    public Set<String> getAllKeys() {
        LinkedHashMap<String, FieldVO> map = getAllFields();
        return map.keySet();
    }

    public LinkedHashMap<String, FieldVO> getAllFields() {
        LinkedHashMap<String, FieldVO> temp = new LinkedHashMap<String, FieldVO>();

        makeHeaderFields(temp, header_fields); 
        makeHeaderFields(temp, dynamic_fields);

        Set<String> bodyKeySet = body_fields.keySet();
        Iterator<String> keyItor = bodyKeySet.iterator();
        String key = null;
        while (keyItor.hasNext()) {
            key = keyItor.next();
            temp.put(key, body_fields.get(key));
        }
        return temp;
    }

    public LinkedHashMap<String, FieldVO> getHeadersFields() {
        LinkedHashMap<String, FieldVO> temp = new LinkedHashMap<String, FieldVO>();

        makeHeaderFields(temp, header_fields); 
        makeHeaderFields(temp, dynamic_fields);
        return temp;
    }

    public LinkedHashMap<String, FieldVO> getBodyFields() {
        LinkedHashMap<String, FieldVO> temp = new LinkedHashMap<String, FieldVO>();

        Set<String> bodyKeySet = body_fields.keySet();
        Iterator<String> keyItor = bodyKeySet.iterator();
        String key = null;
        while (keyItor.hasNext()) {
            key = keyItor.next();
            temp.put(key, body_fields.get(key));
        }
        return temp;
    }

    private void makeHeaderFields(LinkedHashMap<String, FieldVO> fields, LinkedHashMap<String, LinkedHashMap<String, FieldVO>> headers) {
        Set<String> headerKeySet = headers.keySet();
        Iterator<String> headerKeyItor = headerKeySet.iterator();
        String key = null;
        String childKey = null;
        LinkedHashMap<String, FieldVO> child = null;
        Set<String> childKeySet = null;
        Iterator<String> childKeyItor = null;
        while (headerKeyItor.hasNext()) {
            key = headerKeyItor.next();
            child = headers.get(key);
            childKeySet = child.keySet();
            childKeyItor = childKeySet.iterator();
            while (childKeyItor.hasNext()) {
                childKey = childKeyItor.next();
                fields.put(key + "." + childKey, child.get(childKey));
            }
        }
    }

    // Parsing을 위한 메소드

    public void parsingHeaderMessageBuf(byte[] messageBuf) throws MessageParserException {

        int offset = 0;
        offset = parsingHeader(offset, messageBuf); 
        offset = parsingDynamicHeader(offset, messageBuf);

    }

    public void parsingMessageBuf(byte[] messageBuf) throws MessageParserException {

        int offset = 0;
        offset = parsingHeader(offset, messageBuf); 
        offset = parsingDynamicHeader(offset, messageBuf);
        offset = parsingBody(offset, messageBuf);
    }

    private int parsingHeader(int offset, byte[] messageBuf) throws MessageParserException {
        List<HeaderModel> header_models = model.getHeaders();
        List<FieldModel> fieldModels = null;

        for (int i = 0; i < header_models.size(); i++) {
            HeaderModel header = header_models.get(i);
            fieldModels = header.getFieldModels();
            for (int j = 0; j < fieldModels.size(); j++) {
                FieldModel fieldModel = fieldModels.get(j);
                addHeaderFieldModel(this.header_fields, header, fieldModel);
            }
            offset = setHeaderBytes(0, messageBuf);
        }
        return offset;

    }
    private int parsingDynamicHeader(int offset, byte[] messageBuf) throws MessageParserException {
         String cons_cd = getConsCdOfBytes(offset, messageBuf);
        
        // System.out.println("cons_cd="+cons_cd);
        String dynamicheader_id = getDynamicHeaderId(cons_cd);
        int initOffset = offset;
        List<FieldModel> fieldModels = null;
        if (dynamicheader_id != null) {
            // System.out.println("parsingDynamicHeader dynamicheader_id="+dynamicheader_id);
            DynamicHeaderModel dynamicHeaderModel = model.getDynamic_header(dynamicheader_id);
            dynamicHeaderModel.setUseHeadModel(true);
            fieldModels = dynamicHeaderModel.getFieldModels();
            for (int j = 0; j < fieldModels.size(); j++) {
                FieldModel fieldModel = fieldModels.get(j);
                addHeaderFieldModel(this.dynamic_fields, dynamicHeaderModel, fieldModel);
            }
            offset = setDynamicHeaderBytes(dynamicheader_id, offset, messageBuf);
        }
        if (initOffset == offset) return offset;
        else return parsingDynamicHeader(offset, messageBuf);
    }
    
    
    private String getDynamicHeaderId(String cons_cd) {
        List<DynamicHeaderModel> dynamic_headers = model.getDynamic_headers();
        for (int i = 0; i < dynamic_headers.size(); i++) {
            DynamicHeaderModel dynamic = dynamic_headers.get(i);
            if (dynamic.getCons_cd().equals(cons_cd)) return dynamic.getId();
        }
        return null;
    }

//    private String getCons_cd(String dynamicHeaderId) {
//        List<DynamicHeaderModel> dynamic_headers = model.getDynamic_headers();
//        for (int i = 0; i < dynamic_headers.size(); i++) {
//            DynamicHeaderModel dynamic = dynamic_headers.get(i);
//            if (dynamic.getId().equals(dynamicHeaderId)) return dynamic.getCons_cd();
//        }
//        return null;
//    }
    public boolean hasDynamicHeader(String cons_cd) {
        // TODO Auto-generated method stub
        String dynamicheader_id = getDynamicHeaderId(cons_cd);
        LinkedHashMap<String, FieldVO> fieldMap = dynamic_fields.get(dynamicheader_id);
        if (fieldMap == null) return false;
        else return true;
    }
    
    public void addDynamicHeader(String cons_cd) throws MessageParserException {
        // TODO Auto-generated method stub
        if (hasDynamicHeader(cons_cd)) throw new MessageParserException("XXXXXXXX");
        List<DynamicHeaderModel> dynamic_headers = model.getDynamic_headers();
        // System.out.println("addDynamicHeader(1)===>"+dynamic_headers.size());
        for (int i = 0; i < dynamic_headers.size(); i++) {
            DynamicHeaderModel dynamic = dynamic_headers.get(i);
            if (dynamic.getCons_cd().equals(cons_cd)) {
                List<FieldModel> fieldModels = dynamic.getFieldModels();
                // System.out.println("addDynamicHeader(2)===>"+fieldModels.size());
                for (int j = 0; j < fieldModels.size(); j++) {
                    FieldModel fieldModel = fieldModels.get(j);
                    // System.out.println(fieldModel.getName());
                    addHeaderFieldModel(dynamic_fields, dynamic, fieldModel);
                }
            }
        }
        // throw new DynamicHeaderNotFoundException();
    }
    
    public int setDynamicHeaderBytes(String dynamicheader_id, int offset, byte[] messageBuf) {
        LinkedHashMap<String, FieldVO> fieldMap = dynamic_fields.get(dynamicheader_id);
        Set<String> fieldKeySets = fieldMap.keySet();
        Iterator<String> fieldItor = fieldKeySets.iterator();
        String key = null;
        while (fieldItor.hasNext()) {
            key = fieldItor.next();
            // System.out.println("setDynamicHeaderBytes["+key+"]");
            FieldVO vo = fieldMap.get(key);
            offset = vo.setFieldBuf(offset, messageBuf);
        }
        return offset;
    }
    
    private String getConsCdOfBytes(int offset, byte[] messageBuf) {

        if (messageBuf.length < (offset + 2)) return null;

        byte[] consCdBuf = new byte[2];
        int j = 0;
        for (int i = offset; i < offset + 2; i++) {
            consCdBuf[j] = messageBuf[i];
            j++;
        }
        String cons_cd = new String(consCdBuf);
        // System.out.println("!!!!!!!!!!!getConsCdOfBytes="+cons_cd);
        return cons_cd;
    }

    private int parsingBody(int offset, byte[] messageBuf) throws MessageParserException {
        List<FieldModel> fieldModels = null;
        // 이부분은 차후에 일반화(Generalization) 대상
        // this.id = getFiledValue("STTL_SYS_COPT.INTF_ID").toString();
        this.id = TelegramInterfaceFactory.getMessageID(model.getId()).getId(this);
        BodyModel bodyModel = model.getBody(id, messageType);
        fieldModels = bodyModel.getFieldModels();
        for (int i = 0; i < fieldModels.size(); i++) {
            FieldModel fieldModel = fieldModels.get(i);
            addFieldModel(this.body_fields, fieldModel);
        }
        offset = setBodyBytes(offset, messageBuf);
        return offset;
    }

//    public void parsingMessageBuf(String id, byte[] messageBuf) throws Exception {
//        List<HeaderModel> header_models = model.getHeaders();
//        List<FieldModel> fieldModels = null;
//        int offset = 0;
//        for (int i = 0; i < header_models.size(); i++) {
//            HeaderModel header = header_models.get(i);
//            fieldModels = header.getFieldModels();
//            for (int j = 0; j < fieldModels.size(); j++) {
//                FieldModel fieldModel = fieldModels.get(j);
//                addHeaderFieldModel(this.header_fields, header, fieldModel);
//            }
//            offset = setHeaderBytes(0, messageBuf);
//        }
//        offset = parsingDynamicHeader(offset, messageBuf);
//        // 이부분은 차후에 일반화(Generalization) 대상
//        // this.id = getFiledValue("STTL_SYS_COPT.INTF_ID").toString();
//        this.id = id;
//        BodyModel bodyModel = model.getBody(id, messageType);
//        fieldModels = bodyModel.getFieldModels();
//        for (int i = 0; i < fieldModels.size(); i++) {
//            FieldModel fieldModel = fieldModels.get(i);
//            addFieldModel(this.body_fields, fieldModel);
//        }
//        offset = setBodyBytes(offset, messageBuf);
//
//    }

//    private String getConsCdOfBytes(int offset, byte[] messageBuf) {
//
//        if (messageBuf.length < (offset + 2)) return null;
//
//        byte[] consCdBuf = new byte[2];
//        int j = 0;
//        for (int i = offset; i < offset + 2; i++) {
//            consCdBuf[j] = messageBuf[i];
//            j++;
//        }
//        String cons_cd = new String(consCdBuf);
//        // System.out.println("!!!!!!!!!!!getConsCdOfBytes="+cons_cd);
//        return cons_cd;
//    }

    public int setHeaderBytes(int offset, byte[] messageBuf) {
        Set<String> modelKeys = header_fields.keySet();
        Iterator<String> itor = modelKeys.iterator();
        String key = null;
        while (itor.hasNext()) {
            key = itor.next();
            LinkedHashMap<String, FieldVO> fieldMap = header_fields.get(key);
            Set<String> fieldKeys = fieldMap.keySet();
            Iterator<String> fieldItor = fieldKeys.iterator();
            while (fieldItor.hasNext()) {
                key = fieldItor.next();
                // System.out.println("setHeaderBytes["+key+"]");
                FieldVO vo = fieldMap.get(key);
                offset = vo.setFieldBuf(offset, messageBuf);
            }
        }
        return offset;
    }
 

    public int setBodyBytes(int offset, byte[] messageBuf) {
        // System.out.println(messageBuf.length+"<="+offset);
        Set<String> fieldKeys = body_fields.keySet();
        Iterator<String> itor = fieldKeys.iterator();
        String key = null;
        while (itor.hasNext()) {
            if (messageBuf.length <= offset) return 0;
            key = itor.next();
            // System.out.println("setBodyBytes["+key+"]");
            FieldVO vo = body_fields.get(key);
            offset = vo.setFieldBuf(offset, messageBuf);
        }
        return offset;
    }

    public void defaultSetting() throws MessageParserException {
        List<HeaderModel> headers = model.getHeaders();
        List<FieldModel> fieldModels = null;
        for (int i = 0; i < headers.size(); i++) {
            HeaderModel header = headers.get(i);
            fieldModels = header.getFieldModels();
            for (int j = 0; j < fieldModels.size(); j++) {
                FieldModel fieldModel = fieldModels.get(j);
                addHeaderFieldModel(this.header_fields, header, fieldModel);
            }
        }
        BodyModel bodyModel = model.getBody(id, messageType);
        fieldModels = bodyModel.getFieldModels();
        for (int i = 0; i < fieldModels.size(); i++) {
            FieldModel fieldModel = fieldModels.get(i);
            addFieldModel(this.body_fields, fieldModel);
        }
    }
    
    public void defaultHedaderetting() throws MessageParserException {
        List<HeaderModel> headers = model.getHeaders();
        List<FieldModel> fieldModels = null;
        for (int i = 0; i < headers.size(); i++) {
            HeaderModel header = headers.get(i);
            fieldModels = header.getFieldModels();
            for (int j = 0; j < fieldModels.size(); j++) {
                FieldModel fieldModel = fieldModels.get(j);
                addHeaderFieldModel(this.header_fields, header, fieldModel);
            }
        } 
    }

    private void addHeaderFieldModel(LinkedHashMap<String, LinkedHashMap<String, FieldVO>> map, ChunkModel model, FieldModel fieldModel) {
        LinkedHashMap<String, FieldVO> modelMap = map.get(model.getId());
        synchronized (model.getId()) {
            if (modelMap == null) {
                modelMap = new LinkedHashMap<String, FieldVO>();
                map.put(model.getId(), modelMap);
            }
        }
        if (fieldModel.isGroup()) {
            FieldGroupVO node = new FieldGroupVO();
            node.setModel(fieldModel);
            node.setMessage(this);
            node.setCharset(charset);
            modelMap.put(fieldModel.getId(), node);
            node.init();
        } else {
            FieldNodeVO node = new FieldNodeVO();
            node.setModel(fieldModel);
            node.setMessage(this);
            node.setCharset(charset);
            modelMap.put(fieldModel.getId(), node);
            node.init();
        }
    }

    private void addFieldModel(LinkedHashMap<String, FieldVO> map, FieldModel fieldModel) {
        if (fieldModel.isGroup()) {
            FieldGroupVO node = new FieldGroupVO();
            node.setModel(fieldModel);
            node.setMessage(this);
            node.setCharset(charset);
            map.put(fieldModel.getKey(), node);
            node.init();
        } else {
            FieldNodeVO node = new FieldNodeVO();
            node.setModel(fieldModel);
            node.setMessage(this);
            node.setCharset(charset);
            map.put(fieldModel.getKey(), node);
            node.init();
        }
    }

 
    public Object getHeaderFieldValue(String headerId, String key) {
         
        return getHeaderFieldValue(headerId, key,0);
    }
    public Object getHeaderFieldValue(String headerId, String key,int idx) {
        // TODO Auto-generated method stub
        FieldVO vo = getHeaderVO(headerId,key, idx);
        String retVal = null;

        if (vo != null) retVal = vo.getValue(vo.getModel().getId(), idx);
        return retVal;
    }
     public Object getHeaderFieldValue(String headerId, String groupName, int gIndex,String key, int sIndex) {
            
            FieldGroupVO fieldGroupVO = (FieldGroupVO)getHeaderFieldGroupVO(headerId,groupName,gIndex);
            
            return fieldGroupVO.getValue(key, sIndex);
    
        }
    
    public Object getFieldValue(String key) {
        // TODO Auto-generated method stub
        FieldVO vo = getVO(key, 0);
        String retVal = null;

        if (vo != null) retVal = vo.getValue(vo.getModel().getId(), 0);
        return retVal;
    }

    public Object getFieldValue(String key, int index) {
        // TODO Auto-generated method stub
        FieldVO vo = getVO(key, index);
        String retVal = null;

        if (vo != null) retVal = vo.getValue(vo.getModel().getId(), index);
        return retVal;
    }

    
    public Object getFieldValue(String groupName, int gIndex,String key, int sIndex) {
        
        FieldGroupVO fieldGroupVO = (FieldGroupVO)getFieldGroupVO(groupName,gIndex);
        
        return fieldGroupVO.getValue(key, sIndex);
        // TODO Auto-generated method stub
//        FieldVO vo = getVO(key, index);
//        String retVal = null;
//
//        if (vo != null) retVal = vo.getValue(vo.getModel().getId(), index);
//        return retVal;
        
    }
     
    public byte[] getMessageHeaderBytes() throws MessageParserException{
        // TODO Auto-generated method stub
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Set<String> keySets = header_fields.keySet();
        Iterator<String> itor = keySets.iterator();
        String key = null;
        while (itor.hasNext()) {
            key = itor.next();
            LinkedHashMap<String, FieldVO> modelMap = header_fields.get(key);
            Set<String> modelKeySets = modelMap.keySet();
            Iterator<String> modelItor = modelKeySets.iterator();
            while (modelItor.hasNext()) {
                key = modelItor.next();
                try {
                    bao.write(modelMap.get(key).getBytes());
                }
                catch (IOException e) {
                    throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
                }
            }
        } 
        return bao.toByteArray();
    }
    public byte[] getMessageBytes() throws MessageParserException {
        // TODO Auto-generated method stub
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Set<String> keySets = header_fields.keySet();
        Iterator<String> itor = keySets.iterator();
        String key = null;
        try {
            while (itor.hasNext()) {
                key = itor.next();
                LinkedHashMap<String, FieldVO> modelMap = header_fields.get(key);
                Set<String> modelKeySets = modelMap.keySet();
                Iterator<String> modelItor = modelKeySets.iterator();
                while (modelItor.hasNext()) {
                    key = modelItor.next();
                      bao.write(modelMap.get(key).getBytes());
                   
                }
            } 
            
            keySets = dynamic_fields.keySet();
            itor = keySets.iterator();
            while (itor.hasNext()) {
                key = itor.next();
                bao.write(getDynamicHeaderBytes(key));
            }
            
            bao.write(getBodyBytes());
        }
        catch (IOException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
        }
       
        return bao.toByteArray();
    }
 
    private byte[] getDynamicHeaderBytes(String dynamicheader_id) throws MessageParserException {

        DynamicHeaderModel dynamic_model = model.getDynamic_header(dynamicheader_id);
        if(dynamic_model.getLength_field_id()!=null){
            FieldModel lengthFieldModel = dynamic_model.getFieldModel(dynamic_model.getLength_field_id());
            int len = getDynamicHeaderBytesLength(dynamicheader_id);
            setHeaderFieldValue(dynamicheader_id, lengthFieldModel.getKey(), "" + len);
        }
 
        

        LinkedHashMap<String, FieldVO> modelMap = dynamic_fields.get(dynamicheader_id);
        Set<String> modelKeySets = modelMap.keySet();
        Iterator<String> modelItor = modelKeySets.iterator();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        String key = null;
        while (modelItor.hasNext()) {
            key = modelItor.next();
            try {
                bao.write(modelMap.get(key).getBytes());
            }
            catch (IOException e) {
                throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
            }
        }
        byte[] returnBytes = bao.toByteArray();
        // System.out.println("getDynamicHeaderBytes="+lengthFieldModel.getKey()+"="+len+"="+returnBytes.length);
        return returnBytes;
    }
    
    private int getDynamicHeaderBytesLength(String dynamicheader_id) throws MessageParserException {
        LinkedHashMap<String, FieldVO> modelMap = dynamic_fields.get(dynamicheader_id);
        Set<String> modelKeySets = modelMap.keySet();
        Iterator<String> modelItor = modelKeySets.iterator();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        String key = null;
        while (modelItor.hasNext()) {
            key = modelItor.next();
            try {
                bao.write(modelMap.get(key).getBytes());
            }
            catch (IOException e) {
                throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
            }
        }
        byte[] returnBytes = bao.toByteArray();
        DynamicHeaderModel dynamic_model = model.getDynamic_header(dynamicheader_id);
        int len = returnBytes.length + dynamic_model.getLength_gap();
        // RooibosLog.info.println(dynamicheader_id+"[returnBytes.length]="+returnBytes.length);
        // RooibosLog.info.println(dynamicheader_id+"[dynamic_model.getLength_gap()]="+dynamic_model.getLength_gap());
        // RooibosLog.info.println(dynamicheader_id+"[len]="+len);
        return len;
    }
    
    private byte[] getBodyBytes() throws MessageParserException {

        BodyModel bodyModel = model.getBody(id, messageType);
        FieldModel lengthFieldModel= null;
 
        lengthFieldModel = bodyModel.getFieldModel(bodyModel.getLength_field_id());
 

        if (lengthFieldModel != null) {
            int len = getBodyBytesLength();
            setFieldValue(lengthFieldModel.getKey(), "" + len);
        }
        Set<String> bodyKeySets = body_fields.keySet();
        Iterator<String> bodyKeyItor = bodyKeySets.iterator();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        String key = null;
        while (bodyKeyItor.hasNext()) {
            key = bodyKeyItor.next();
            try {
                bao.write(body_fields.get(key).getBytes());
            }
            catch (IOException e) {
                throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
            }
        }
        byte[] returnBytes = bao.toByteArray();
        // System.out.println("getDynamicHeaderBytes="+lengthFieldModel.getKey()+"="+len+"="+returnBytes.length);
        return returnBytes;
    }

    private byte[] getBodyBytes(String prefix) throws MessageParserException {

        LOG.info("1" + prefix);
        BodyModel bodyModel = model.getBody(id, messageType);
        LOG.info("2" + prefix);
        FieldModel lengthFieldModel = bodyModel.getFieldModel(bodyModel.getLength_field_id());
        if (lengthFieldModel != null) {
            int len = getBodyBytesLength();
            setFieldValue(lengthFieldModel.getKey(), "" + len);
        }
        LOG.info("3" + prefix);
        Set<String> bodyKeySets = body_fields.keySet();
        Iterator<String> bodyKeyItor = bodyKeySets.iterator();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        String key = null;
        LOG.info("4" + prefix);
        while (bodyKeyItor.hasNext()) {
            key = bodyKeyItor.next();
            try {
                bao.write(body_fields.get(key).getBytes());
            }
            catch (IOException e) {
                throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
            }
        }
        LOG.info("5" + prefix);
        byte[] returnBytes = bao.toByteArray();
        LOG.info("6" + prefix);

        if (lengthFieldModel != null) {
            int len = returnBytes.length;
            setFieldValue(lengthFieldModel.getKey(), "" + len);
        }
        LOG.info("7" + prefix);
        // System.out.println("getDynamicHeaderBytes="+lengthFieldModel.getKey()+"="+len+"="+returnBytes.length);
        return returnBytes;
    }

    private int getBodyBytesLength() throws MessageParserException {

        Set<String> bodyKeySets = body_fields.keySet();
        Iterator<String> bodyKeyItor = bodyKeySets.iterator();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        String key = null;
        while (bodyKeyItor.hasNext()) {
            key = bodyKeyItor.next();
            try {
                bao.write(body_fields.get(key).getBytes());
            }
            catch (IOException e) {
                throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
            }
        }
        byte[] returnBytes = bao.toByteArray();
        BodyModel bodyModel = model.getBody(id, messageType);
        int len = returnBytes.length + bodyModel.getLength_gap();
        // System.out.println("LSM("+messageType+")==>"+bodyModel.getId()+" LEN:"+returnBytes.length+" GAP:"+bodyModel.getLength_gap());
        return len;
    }

    public String getMessageId() {
        // TODO Auto-generated method stub
        return id;
    }
    
    public void setHeaderFieldValue(String headerId, String key, Object obj) {
        setHeaderFieldValue(headerId,key,obj,0);
    }
    public void setHeaderFieldValue(String headerId, String key, Object obj,int index){
        setHeaderFieldValue(headerId, key, obj,index,0);
    }
    
    public void setHeaderFieldValue(String headerId, String key, Object obj,int index1,int index2){
        // TODO Auto-generated method stub
        FieldVO vo = getHeaderVO(headerId,key, index1);
        vo.setCharset(charset);
        // System.out.println("MessageVO.setFieldValue"+vo);
        if (vo != null) vo.setValue(vo.getModel().getId(), obj, index2);
    }

    
 
//    private FieldVO getTestVO(String key, int index) {
//        FieldVO  vo = body_fields.get(key);
//
//            if (vo != null && vo instanceof FieldGroupVO) {
//
//                  
//                 vo = getGroupVO( vo, getNextKeyString(key), index);
//            }
//
//        } else vo = body_fields.get(key);
//
//        return vo;
//    }W
 
//    public void setFieldValue(String groupName1, int index1, String groupName2, int index2, String key,int index3,Object obj){
//        // TODO Auto-generated method stub\
//        FieldVO  vo1 = body_fields.get(groupName1);
// 
//        FieldGroupVO vo2 = (FieldGroupVO) vo1;
// 
//        FieldGroupVO vo3 = (FieldGroupVO) vo2.getFieldVO(groupName2, index2);
//        
//        FieldVO vo  =   vo3.getFieldVO(key, index3);
//  
// 
//        if (vo != null) vo.setValue(vo.getModel().getId(), obj, 0);
//    }
    
//    private FieldVO getGroupVO2(FieldVO vo, String key, int index) {
//        
//        FieldGroupVO gvo = (FieldGroupVO) vo;
//         
////        System.out.println("여기====>>"+key + "==="+getNextKeyString(key) +"                           "+id);
//        
//        FieldVO rvo = gvo.getFieldVO(key, index);
//        
//        if (rvo instanceof FieldGroupVO) {
//            return rvo;
//        }
//        return rvo;
//    }
    
    public void setFieldValue(String key, Object obj) {
        // TODO Auto-generated method stub
        FieldVO vo = getVO(key, 0);
        // System.out.println("MessageVO.setFieldValue"+vo);
        if (vo != null) vo.setValue(vo.getModel().getId(), obj, 0);
    }

    public void setFieldValue(String key, Object obj, int index) {
        // TODO Auto-generated method stub
        FieldVO vo = getVO(key, index);
        // System.out.println("MessageVO.setFieldValue"+vo);
        if (vo != null) vo.setValue(vo.getModel().getId(), obj, index);
    }
  
    private FieldVO getHeaderVO(String headerId,String key, int index) {
        FieldVO vo = null; 
        LinkedHashMap<String, FieldVO>loc_Fields = null; 
        
        if(header_fields.get(headerId)!=null){
            loc_Fields = header_fields.get(headerId);
        }else{
            loc_Fields = dynamic_fields.get(headerId); 
        } 
        if(loc_Fields==null)
             return null;
        
        if (key.indexOf(".") > 0) {
            String field_id = getKeyString(key);
            vo = loc_Fields.get(field_id);
            
            if (vo != null && vo instanceof FieldGroupVO) { 
                vo = getHeaderGroupVO(  vo, getNextKeyString(key), index);
            }
        }else{
            vo = loc_Fields.get(key);
        } 
        return vo;
    }
    private FieldVO getVO(String key, int index) {
        FieldVO vo = null;
        if (key.indexOf(".") > 0) {
            String header_id = getKeyString(key);
            String field_id = getKeyString(getNextKeyString(key));
            
//            System.out.println("header_id="+header_id+",  field_id="+field_id +"       "+getNextKeyString(key));
           
            if (header_fields.get(header_id) != null) vo = header_fields.get(header_id).get(field_id);
 
            if (vo == null) vo = body_fields.get(header_id);

            if (vo != null && vo instanceof FieldGroupVO) {

                  
                 vo = getGroupVO( vo, getNextKeyString(key), index);
            }

        } else vo = body_fields.get(key);

        return vo;
    }
 
    public FieldVO getFieldVO(String key) {
      FieldVO vo = null;
      String header_id = getKeyString(key);
      String field_id = getKeyString(getNextKeyString(key));
      // System.out.println("header_id="+header_id+",  field_id="+field_id);
      if (header_fields.get(header_id) != null) vo = header_fields.get(header_id).get(field_id);

      if (vo == null) vo = body_fields.get(header_id);
      return vo;
    }
    
    
    public FieldVO getHeaderFieldGroupVO(String headerId, String key) {
        return getHeaderFieldGroupVO(headerId, key,0);
    }
    public FieldVO getHeaderFieldGroupVO(String headerId,String key,int index) {
        
        FieldVO vo = null;
        if (key.indexOf(".") > 0) { 
            String field_id = getKeyString(key);
 
            if (header_fields.get(headerId) != null) vo = header_fields.get(headerId).get(field_id);
  
            if (vo != null && vo instanceof FieldGroupVO) { 
                 vo = getGroupFieldVO(vo, getNextKeyString(key), index);
            }

        } 
        else vo = header_fields.get(headerId).get(key);
        return vo;
    }
    
    public FieldVO getFieldGroupVO(String key) {
        return getFieldGroupVO(key,0);
    }
    public FieldVO getFieldGroupVO(String key,int index) {
 
        FieldVO vo = null;
        if (key.indexOf(".") > 0) {
            String header_id = getKeyString(key);
            String field_id = getKeyString(getNextKeyString(key));
 
            if (header_fields.get(header_id) != null) vo = header_fields.get(header_id).get(field_id);
 
            if (vo == null) vo = body_fields.get(header_id);

            if (vo != null && vo instanceof FieldGroupVO) { 
                 vo = getGroupFieldVO( vo, getNextKeyString(key), index);
            }

        } else vo = body_fields.get(key);
        return vo;
    }


    private FieldVO getGroupFieldVO(FieldVO vo, String key, int index) {
       
        
        FieldGroupVO gvo = (FieldGroupVO) vo;
        
        String id = getNextKeyString(key);
        
//        System.out.println("여기====>>"+key + "==="+getNextKeyString(key) +"                           "+id);
        
        FieldVO rvo = gvo.getFieldVO(id, index);
        
        if (rvo instanceof FieldGroupVO) {
            return rvo;
        }
        else{
            return getGroupFieldVO(rvo, id, index);
        } 
    }
    
    private FieldVO getHeaderGroupVO( FieldVO vo, String key, int index) {
        
        FieldGroupVO gvo = (FieldGroupVO) vo;
        
        String id = getKeyString(key);
        
//        System.out.println("여기====>>"+key + "==="+getNextKeyString(key) +"                           "+id);
        
        FieldVO rvo = gvo.getFieldVO(id, index);
        
        if (rvo instanceof FieldGroupVO) {
            return getHeaderGroupVO(  rvo, getNextKeyString(key), index);
        }
        return rvo;
    }
    
    private FieldVO getGroupVO(FieldVO vo, String key, int index) {
       
        
        FieldGroupVO gvo = (FieldGroupVO) vo;
        
        String id = getKeyString(key);
        
//        System.out.println("여기====>>"+key + "==="+getNextKeyString(key) +"                           "+id);
        
        FieldVO rvo = gvo.getFieldVO(id, index);
        
        if (rvo instanceof FieldGroupVO) {
            return getGroupVO(rvo, getNextKeyString(key), index);
        }
        return rvo;
    }

    private String getKeyString(String key) {
        int offset = key.indexOf(".");
        String nextKey = null;
        if (offset > 0) nextKey = key.substring(0, offset);
        else nextKey = key;
        return nextKey;
    }

    private String getNextKeyString(String key) {
        int offset = key.indexOf(".");
        String nextKey = null;
        if (offset > 0) nextKey = key.substring(offset + 1);
        else nextKey = key;
        return nextKey;
    }

    public void printDebug() {
        LOG.info(header_fields); 
        LOG.info(body_fields);
    }
 

    public void printLog() throws MessageParserException {
        // TODO Auto-generated method stub
        Set<String> keySets = header_fields.keySet();
        Iterator<String> itor = keySets.iterator();
        String key = null;
        while (itor.hasNext()) {
            key = itor.next();
            LinkedHashMap<String, FieldVO> modelMap = header_fields.get(key);
            Set<String> modelKeySets = modelMap.keySet();
            Iterator<String> modelItor = modelKeySets.iterator();
            while (modelItor.hasNext()) {
                key = modelItor.next();
                modelMap.get(key).printLog();
            }
        }
        keySets = dynamic_fields.keySet();
        itor = keySets.iterator();
        while (itor.hasNext()) {
            key = itor.next();
            printDynamicHeader(key);
        }

        printBody();
    }
    private void printDynamicHeader(String dynamicheader_id) throws MessageParserException {

        DynamicHeaderModel dynamic_model = model.getDynamic_header(dynamicheader_id);
        if(dynamic_model.getLength_field_id()!=null){
            FieldModel lengthFieldModel = dynamic_model.getFieldModel(dynamic_model.getLength_field_id()); 
            int len = getDynamicHeaderBytesLength(dynamicheader_id); 
            setHeaderFieldValue(dynamicheader_id,lengthFieldModel.getKey(), "" + len);
        }
        LinkedHashMap<String, FieldVO> modelMap = dynamic_fields.get(dynamicheader_id);
        Set<String> modelKeySets = modelMap.keySet();
        Iterator<String> modelItor = modelKeySets.iterator();
        String key = null;
        while (modelItor.hasNext()) {
            key = modelItor.next();
            modelMap.get(key).printLog();
        }
    }

    private void printBody() throws MessageParserException {

        BodyModel bodyModel = model.getBodyNoSync(id, messageType);

        if (bodyModel.getLength_field_id() != null && !"".equals(bodyModel.getLength_field_id())) {
            FieldModel lengthFieldModel = bodyModel.getFieldModel(bodyModel.getLength_field_id());
            if(lengthFieldModel != null) {
                int len = getBodyBytesLength();
                setFieldValue(lengthFieldModel.getKey(), "" + len);
            }
        }
        Set<String> bodyKeySets = body_fields.keySet();
        Iterator<String> bodyKeyItor = bodyKeySets.iterator();
        String key = null;
        while (bodyKeyItor.hasNext()) {
            key = bodyKeyItor.next();
            body_fields.get(key).printLog();
        }
    }
    
    public String getHeadersString() throws MessageParserException {
        // TODO Auto-generated method stub
        byte[] headersBytes = getHeaders();
        return new String(headersBytes);
    }
 

    public String getBodyString() throws MessageParserException {
        // TODO Auto-generated method stub
        byte[] bodyBytes = getBodyBytes();
        return new String(bodyBytes);
    }
    
    public byte[] getHeaders() throws MessageParserException {
        // TODO Auto-generated method stub
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Set<String> keySets = header_fields.keySet();
        Iterator<String> itor = keySets.iterator();
        String key = null;
        while (itor.hasNext()) {
            key = itor.next();
            LinkedHashMap<String, FieldVO> modelMap = header_fields.get(key);
            Set<String> modelKeySets = modelMap.keySet();
            Iterator<String> modelItor = modelKeySets.iterator();
            while (modelItor.hasNext()) {
                key = modelItor.next();
                try {
                    bao.write(modelMap.get(key).getBytes());
                }
                catch (IOException e) {
                    throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
                }
            }
        }
        
        return bao.toByteArray();
    }

    public byte[] getBody() throws MessageParserException {
        // TODO Auto-generated method stub
        return getBodyBytes();
    }

    public byte[] getBody(String prefix) throws MessageParserException {
        // TODO Auto-generated method stub
        return getBodyBytes(prefix);
    }

    public void setBodyBytes(byte[] bodyMessage) throws MessageParserException {
        // TODO Auto-generated method stub
        Set<String> fieldKeys = body_fields.keySet();
        Iterator<String> itor = fieldKeys.iterator();
        String key = null;
        int offset = 0;
        while (itor.hasNext()) {
            key = itor.next();
            // System.out.println("setBodyBytes["+key+"]");
            FieldVO vo = body_fields.get(key);
            offset = vo.setFieldBuf(offset, bodyMessage);
        }

    }
 
    public String getString(String key) {
        // TODO Auto-generated method stub
        Object obj = getFieldValue(key);
        if (obj != null) return obj.toString();
        else return "";
    }

    public String getString(String key, int index) {
        // TODO Auto-generated method stub
        Object obj = getFieldValue(key, index);
        if (obj != null) return obj.toString();
        else return "";
    }

    public String getStringTrim(String key) {
        // TODO Auto-generated method stub
        return getString(key).trim();
    }

    public String getStringTrim(String key, int index) {
        // TODO Auto-generated method stub
        return getString(key, index).trim();
    }

    public String getMessageString() throws MessageParserException {
        // TODO Auto-generated method stub
        return new String(getMessageBytes());
    }
    
 

    public String getStringTrim(String key, String nvl) {
        // TODO Auto-generated method stub
        if (getFieldValue(key) != null) return getStringTrim(key);
        else return nvl;
    }

    public String getStringTrim(String key, int index, String nvl) {
        // TODO Auto-generated method stub
        if (getFieldValue(key, index) != null) return getStringTrim(key, index);
        else return nvl;
    }

    public String getHeaderString(String headerName) throws MessageParserException {
        // TODO Auto-generated method stub
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        LinkedHashMap<String, FieldVO> modelMap = header_fields.get(headerName);
  
//        if (modelMap == null) 
//             throw new MessageParserException(,null"");

        Set<String> modelKeySets = modelMap.keySet();
        Iterator<String> modelItor = modelKeySets.iterator();
        String key = null;
        while (modelItor.hasNext()) {
            key = modelItor.next();
            try {
                bao.write(modelMap.get(key).getBytes());
            }
            catch (IOException e) {
                throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
            }
        }

        return new String(bao.toByteArray());
    }
 
    public String getMessageManagerId() {
        // TODO Auto-generated method stub
        return messageManagerId;
    }

    public void setMessageManagerId(String messageManagerId) {
        this.messageManagerId = messageManagerId;
    }

    public String getBodyDefineVersion() {
        // TODO Auto-generated method stub
        BodyModel bodyModel = model.getBody(id, messageType);
        return bodyModel.getVersion();
    }

 
    public String getWriterName() {
        // TODO Auto-generated method stub
        BodyModel bodyModel = model.getBody(id, messageType);
        return bodyModel.getWriterName();
    }

    public void essentialValueHeaderCheck() throws MessageParserException {
        Set<String> keySets = header_fields.keySet();
        Iterator<String> itor = keySets.iterator();
        String key = null;
        while (itor.hasNext()) {
            key = itor.next();
            LinkedHashMap<String, FieldVO> modelMap = header_fields.get(key);
            Set<String> modelKeySets = modelMap.keySet();
            Iterator<String> modelItor = modelKeySets.iterator();
            while (modelItor.hasNext()) {
                key = modelItor.next();
                
                if(modelMap.get(key).getModel() instanceof FieldNodeModel ){
                    FieldNodeModel fieldModel = (FieldNodeModel)modelMap.get(key).getModel();
                    if("Y".equals(fieldModel.getEssentialYn())){ 
                        String val= getEssentialValueObject(modelMap.get(key).getBytes(), fieldModel.getType()); 
                        if(val==null){
                            throw new MessageParserException(ErrorConstant.TELEGRAM_ESSENTIAL_FIELD_VALUE_ERROR,"HEADER=key["+key+"]"); //필수값이 입력되지 않았습니다.
                        }
                    } 
                } 
            }
        } 
    }
    
    
    public void essentialValueCheck() throws MessageParserException { 
        
        essentialValueHeaderCheck();
        
        BodyModel bodyModel = model.getBody(id, messageType);
        FieldModel lengthFieldModel = bodyModel.getFieldModel(bodyModel.getLength_field_id());
        if (lengthFieldModel != null) {
            int len = getBodyBytesLength();
            setFieldValue(lengthFieldModel.getKey(), "" + len);
        }
        Set<String> bodyKeySets = body_fields.keySet();
        Iterator<String> bodyKeyItor = bodyKeySets.iterator(); 
        String key = null;
        while (bodyKeyItor.hasNext()) {
            key = bodyKeyItor.next();
           
            if(body_fields.get(key).getModel() instanceof FieldNodeModel ){
                FieldNodeModel fieldModel = (FieldNodeModel)body_fields.get(key).getModel();
                if("Y".equals(fieldModel.getEssentialYn())){ 
                    String val = getEssentialValueObject(body_fields.get(key).getBytes(), fieldModel.getType()); 
                    if(val==null){
                        throw new MessageParserException(ErrorConstant.TELEGRAM_ESSENTIAL_FIELD_VALUE_ERROR,"BODY=key["+key+"]"); //필수값이 입력되지 않았습니다.
                    }
                } 
            } 
        }
 
    }

     private String getEssentialValueObject(byte[] buff, String type) { 
         String strVal = new  String(buff).trim();
         if(type.equals(TelegramConstant.TELE_FIELD_TYPE_CHAR) && "".equals((new String(buff).trim()))){ 
             if("".equals(strVal)){
                 return null;
             } 
         }else if (type.equals(TelegramConstant.TELE_FIELD_TYPE_NUM)){ 
             Long lVal = Long.valueOf(strVal); 
             if(lVal==0){
                 return null;
             } 
         }else if (type.equals(TelegramConstant.TELE_FIELD_TYPE_DEC)){
             BigDecimal bVar = new BigDecimal(strVal);
             if(bVar.compareTo(BigDecimal.valueOf(0))==0){
                 return null;
             }  
         }
         return strVal;
     }
    
    
}
