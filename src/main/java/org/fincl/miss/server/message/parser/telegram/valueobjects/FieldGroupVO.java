package org.fincl.miss.server.message.parser.telegram.valueobjects;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.model.FieldGroupModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.util.EnumCode.Charset;

/** 
 * - 전문의 반복부 필드를 가지고 있는 인스턴스 - 
 *
 */

public class FieldGroupVO extends FieldVO {
 
    protected List<List<FieldVO>> fieldVOList = null;
    protected FieldVO lengthFieldVO = null;
    protected boolean isOneLengthGroupVO = false;
    Charset charset = null;
    
    public FieldGroupVO() {
        super();
        fieldVOList = new ArrayList<List<FieldVO>>();
    }

    public List<List<FieldVO>> getFieldVOList() {
        return fieldVOList;
    }
    
    public FieldVO getFieldVO(String id, int index) {
        
        /*
         * 자기자신의 값을 가져오려 할때 하위 필드를 생성하지 않는다.
         */
        if(this.model.getId().equals(id))
            return null;
        
        if(!isOneLengthGroupVO && index > (fieldVOList.size()-1))
        {
            initFieldVOList(index+1);
        }
        else if(isOneLengthGroupVO)
        {
            if(index == 0)
                initFieldVOList(index+1);
            index = 0;
        }
        //System.out.println(index+"=="+isOneLengthGroupVO);
        List<FieldVO> vVO = fieldVOList.get(index);
        FieldVO vo = null;
        for(int i=0;i<vVO.size();i++)
        {
            vo = vVO.get(i);
            if(vo.getModel().getId().equals(id))
                return vo;
        }
        return null;
    }

    public void setFieldVOList(List<List<FieldVO>> fieldVOList) {
        this.fieldVOList = fieldVOList;
    }

    @Override
    public void setValue(String id, Object obj, int index) {
        // TODO Auto-generated method stub
        FieldVO vo = getFieldVO(id, index);
        if(vo != null)
            vo.setValue(id, obj, index);
    }
    
//    public String[] getValues(String id) {
//        int size = fieldVOList.size();
//        String[] temp = new String[size];
//        FieldVO vo = null;
//        for(int index=0;index<size;index++) {
//            vo = getFieldVO(id, index);
//            temp[index] = vo.getValue(id, index);
//        }
//        return temp;
//    }
    
//    public String[] getValues(String id, FunctionModel function)
//    {
//        int size = fieldVOList.size();
//        String[] temp = new String[size];
//        FieldVO vo = null;
//        for(int index=0;index<size;index++) {
//            vo = getFieldVO(id, index);
//            Object obj = null;
//            try {
//                obj = function.execute(vo.getValue(id, index));
//            }catch(Exception e){
//                //e.printStackTrace();
//                LOG.error("FieldGroupVO getValues Error id="+id+" indext="+index, e);
//            }
//            if(obj != null)
//                temp[index] = obj.toString();
//            else
//                temp[index] = null;
//        }
//        return temp;
//    }

    public void initFieldVOList(int index) {
        int addSize = index - fieldVOList.size();
        FieldGroupModel groupModel = (FieldGroupModel)model;
        List<FieldVO> row = null;
        List<FieldModel> modelList = groupModel.getFieldModelList();
        FieldModel fieldModel = null;
        for(int i=0; i<addSize; i++)
        {
            row = new ArrayList<FieldVO>();         
            for(int j=0;j<modelList.size();j++)
            {
                fieldModel = modelList.get(j);
                if (fieldModel.isGroup()) {
                    FieldGroupVO node = new FieldGroupVO();         
                    node.setModel(fieldModel);
                    node.setMessage(message); 
                    node.setCharset(charset);
                    row.add(node);
                    //node.init();        
                } else {
                    FieldNodeVO node = new FieldNodeVO();           
                    node.setModel(fieldModel);
                    node.setMessage(message);
                    node.setCharset(charset);
                    row.add(node);
                    node.init();
                }
            }
            fieldVOList.add(row);
            
        }
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub
        // 이곳에 반복부 초기화하는 로직을 구현한다.
        if(fieldVOList == null)
            fieldVOList = new ArrayList<List<FieldVO>>();
        FieldGroupModel groupModel = (FieldGroupModel)model;
        String length_field_id = groupModel.getLength_filed_id();
        
        Object strLength = null;
        if(TelegramConstant.TELE_HEADER_TYPE.equals(groupModel.getTeleType())){//헤더이면 
            if(groupModel.getParent().getKey() != null && !groupModel.getParent().getKey().equals("")  && !groupModel.getParent().getKey().equals(groupModel.getHeaderId())){
                length_field_id = groupModel.getParent().getKey().substring(groupModel.getHeaderId().length()+1)+"."+length_field_id;
            }
            
            if(length_field_id != null && !"".equals(length_field_id)){
                
                //부모가 배열이면 부모 배열 갯수를 얻어와야한다.  
                if(groupModel.getParent() instanceof FieldGroupModel && !groupModel.getParent().getKey().equals(groupModel.getHeaderId())){
      
                    
                    FieldGroupModel parentModel = (FieldGroupModel)groupModel.getParent();
                    Object parentSize = null;
                    parentSize = message.getHeaderFieldValue(groupModel.getHeaderId(), parentModel.getLength_filed_id());
   
                    int pSize = Integer.parseInt(parentSize.toString());
                    for(int i=pSize -1;0<=i;i--){
                        strLength = message.getHeaderFieldValue(groupModel.getHeaderId(), length_field_id,i);
                        if(!("".equals(strLength )||"".equals(strLength.toString().trim() )  || "00".equals(strLength ))){                        
                            break;
                        }
                    }
//                    System.out.println(pSize+"-  "+parentSize+" ----"+strLength +"==="+length_field_id);
//                    System.out.println("strLength==="+strLength);
                }else{//부모가 배열이 아니라서 idx=0 번째를 가져온다 1개만 나온다.
                     strLength = message.getHeaderFieldValue(groupModel.getHeaderId(), length_field_id,0);
                }
            } 
        }else{
            if(groupModel.getParent().getKey() != null && !groupModel.getParent().getKey().equals("")){
                length_field_id = groupModel.getParent().getKey()+"."+length_field_id;
            }
            
            if(length_field_id != null && !"".equals(length_field_id)){
                
                //부모가 배열이면 부모 배열 갯수를 얻어와야한다.  
                if(groupModel.getParent() instanceof FieldGroupModel){
 
                    FieldGroupModel parentModel = (FieldGroupModel)groupModel.getParent();
                    Object parentSize = null;
                    parentSize = message.getFieldValue(parentModel.getLength_filed_id());
                    
                    int pSize = Integer.parseInt(parentSize.toString());
                    for(int i=pSize -1;0<=i;i--){
                        strLength = message.getFieldValue( length_field_id,i);
                        
                        if(!("".equals(strLength )||"".equals(strLength.toString().trim() )  || "00".equals(strLength ))){                        
                            break;
                        }
                    }
//                    System.out.println("strLength==="+strLength);
                }else{//부모가 배열이 아니라서 idx=0 번째를 가져온다 1개만 나온다.
                    
                    strLength = message.getFieldValue( length_field_id,0);
                  
                }
            }
        }
        
        
       
        
         
//        System.out.println("FieldGroup Length Field ["+length_field_id+"]["+strLength+"]");
//        System.out.println("FieldGroup["+groupModel.getId()+"] Length Field ["+length_field_id+"]["+strLength+"]");
  
        int len = 0;
        if(strLength != null && !"".equals(strLength.toString().trim())){
            
//             System.out.println("****"+length_field_id+"****"+strLength);
//            try {
//                message.printLog();
//            }
//            catch (Exception e) { 
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            len = Integer.parseInt(strLength.toString().trim());
        }else if (groupModel.isFixedLength())
            len = groupModel.getMax();
        else
        {  
            if(isOneLengthGroupVO)
                len = 1; // GroupType인경우 처리 
            isOneLengthGroupVO = true;
             
        }
        //System.out.println("FieldGroup["+groupModel.getId()+"] Length Field ["+length_field_id+"]["+strLength+"]["+len+"]");
        List<FieldVO> row = null;
        List<FieldModel> modelList = groupModel.getFieldModelList();
        FieldModel fieldModel = null;
        for(int i=0; i<len; i++)
        {
            row = new ArrayList<FieldVO>();         
            for(int j=0;j<modelList.size();j++)
            {
                fieldModel = modelList.get(j);
                if (fieldModel.isGroup()) {
                    FieldGroupVO node = new FieldGroupVO();         
                    node.setModel(fieldModel);
                    node.setMessage(message);
                    node.setCharset(charset);
                    row.add(node);
                   // node.init();        
                } else {
                    FieldNodeVO node = new FieldNodeVO();           
                    node.setModel(fieldModel);
                    node.setMessage(message);
                    node.setCharset(charset);
                    row.add(node);
                    node.init();
                }
            }
            fieldVOList.add(row);           
        }
    }

    @Override
    public byte[] getBytes() throws MessageParserException {
        // TODO Auto-generated method stub
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        FieldVO field = null;
        for(int i=0;i<fieldVOList.size();i++)
            for(int j=0;j<fieldVOList.get(i).size();j++)
            {
                field = fieldVOList.get(i).get(j);
                try {
                    bao.write(field.getBytes());
                }
                catch (IOException e) {
                    throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);//스트림을 쓰는중 에러가 발생하였습니다.
                }
            }
        return bao.toByteArray();
    }

    public void setLengthFieldVO(FieldVO lengthFieldVO) {
        this.lengthFieldVO = lengthFieldVO;
    }
    
    public int setFieldBuf(int offset, byte[] messageBuf) {
        init();
        FieldVO field = null;
        //System.out.println("FieldGroupVO setFieldBuf="+model.getId()+"="+fieldVOList.size()+"="+offset);
        for(int i=0;i<fieldVOList.size();i++)
            for(int j=0;j<fieldVOList.get(i).size();j++)
            {
                field = fieldVOList.get(i).get(j);
                offset = field.setFieldBuf(offset, messageBuf);
            }
        return offset;
    }

    @Override
    public void printLog() {
        // TODO Auto-generated method stub      
        FieldVO field = null;
        for(int i=0;i<fieldVOList.size();i++)
            for(int j=0;j<fieldVOList.get(i).size();j++)
            {
                field = fieldVOList.get(i).get(j);
                field.printLog();
            }
    }

    @Override
    public void setValue(FieldVO vo) {
        // TODO Auto-generated method stub
        FieldGroupVO gvo = null;
        FieldVO src = null;
        FieldVO target = null;
        if(vo.isGroupVO())
        {
            gvo = (FieldGroupVO)vo;
            List<List<FieldVO>> fields = gvo.getFieldVOList();
            for(int i=0;i<fields.size();i++)
            {
                if(fieldVOList.size() <= i)
                    init();
                for(int j=0;j<fields.get(i).size();j++)
                {
                    src = fields.get(i).get(j);                 
                    target = fieldVOList.get(i).get(j);
                    target.setValue(src);
                }
            }
        }
    }

    @Override
    public String getValue(String id, int index) {
        // TODO Auto-generated method stub
        FieldVO vo = getFieldVO(id, index);
        if(vo != null)
            return vo.getValue(id, index);
        else
            return null;
    }
    
    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
        
    }
    
//
//    @Override
//    public String getStringTrim(String id, int index) {
//        // TODO Auto-generated method stub
//        FieldVO vo = getFieldVO(id, index);
//        if(vo != null)
//            return vo.getValue(id, index).trim();
//        else
//            return null;
//    }
//
//    @Override
//    public String getStringTrim(String id, int index, String nvl) {
//        // TODO Auto-generated method stub
//        FieldVO vo = getFieldVO(id, index);
//        if(vo != null)
//            return vo.getValue(id, index).trim();
//        else
//            return nvl;
//    }
	
}
