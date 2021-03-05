package org.fincl.miss.server.message.parser.telegram.valueobjects;

 import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldNodeModel;
import org.fincl.miss.server.message.parser.telegram.util.FieldUtil;
import org.fincl.miss.server.message.parser.telegram.util.StringUtil;
import org.fincl.miss.server.util.EnumCode.Charset;

/**
  * - 전문 필드 부분을 가지고 있는 인스턴스 -
  */

public class FieldNodeVO extends FieldVO {		
	
    Charset charset = null;
    
    public Charset getCharset(){
        return charset;
    }
	public void setValue(String id, Object obj, int index) {
	    FieldNodeModel nodeModel = (FieldNodeModel)model;
        
	    if(obj == null){
 
	            obj = new String("");
		  
		} 
	
		int fieldSize = ((FieldNodeModel)model).getLength();
		
		if(nodeModel.getDelim() == null|| "".equals(nodeModel.getDelim().trim()))
		{
			if(nodeModel.getType().equals(TelegramConstant.TELE_FIELD_TYPE_CHAR))
			{ 
//			    if("STTL_CMRS_YN".equals(nodeModel.getId()) && "STTL_SYS_COPT".equals(nodeModel.getHeaderId())){
////			        System.out.print("");
//			    }
			    byte[] buff = StringUtil.convert(obj.toString(),charset.toString());
                if(fieldSize != 0)
                    fieldBuf = FieldUtil.setChar(buff,fieldSize);
                else
                    fieldBuf = FieldUtil.setChar(buff,buff.length); 
                
			}
			else if (nodeModel.getType().equals(TelegramConstant.TELE_FIELD_TYPE_NUM))
			{
				//System.out.println(nodeModel.getName()+"="+nodeModel.getType());
				fieldBuf = FieldUtil.setNum(obj.toString(),fieldSize);
			}
			else if (nodeModel.getType().equals(TelegramConstant.TELE_FIELD_TYPE_DEC))
			{
				fieldBuf = FieldUtil.setDec(obj.toString(),fieldSize,((FieldNodeModel)model).getFrac_len());
			}
		}
		else
		{
			if(nodeModel.getType().equals(TelegramConstant.TELE_FIELD_TYPE_CHAR)){ 
			    fieldBuf = FieldUtil.setChar(obj.toString(),obj.toString().getBytes().length);  
			}
			else if (nodeModel.getType().equals(TelegramConstant.TELE_FIELD_TYPE_NUM))
			{
// 				 System.out.println(nodeModel.getName()+"="+nodeModel.getType()+"=="+obj.toString());
				fieldBuf = FieldUtil.setNum(obj.toString(),obj.toString().getBytes().length);
			}
			else if (nodeModel.getType().equals(TelegramConstant.TELE_FIELD_TYPE_DEC))
			{
				fieldBuf = FieldUtil.setDec(obj.toString(),obj.toString().getBytes().length,((FieldNodeModel)model).getFrac_len());
			}
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		FieldNodeModel nodeModel = (FieldNodeModel)model;
		setValue(nodeModel.getId(), nodeModel.getInit_value(), 0);
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		FieldNodeModel nodeModel = (FieldNodeModel)model;
		if(nodeModel.getDelim() == null || "".equals(nodeModel.getDelim()))
		{
			return fieldBuf;
		}
		else
		{
			byte[] temp = new byte[fieldBuf.length+1];
			System.arraycopy(fieldBuf, 0, temp, 0, fieldBuf.length);
			System.arraycopy(nodeModel.getDelim().getBytes(), 0, temp, fieldBuf.length, 1);
			return temp;
		}
	}
	
	public int setFieldBuf(int offset, byte[] messageBuf) {
		FieldNodeModel nodeModel = (FieldNodeModel)model;
		
		if(nodeModel.getDelim() == null || "".equals(nodeModel.getDelim()))
		{		
			int size = nodeModel.getLength();
			fieldBuf = new byte[size];
			int j=0;
			//System.out.println("FieldNodeVO setFieldBuf="+nodeModel.getKey()+"["+offset+"]["+size+"]["+(offset+size)+"]["+messageBuf.length+"]");		
			if((offset+size) > messageBuf.length)
				return (offset+size); 
			for(int i=offset; i<offset+size; i++)
			{
				fieldBuf[j] = messageBuf[i];
				j++;
			}
			//System.out.println("FieldNodeVO setFieldBuf="+nodeModel.getKey()+"="+new String(fieldBuf)+"["+offset+"]["+size+"]");
			return offset+size; // 다음 offset을 리턴한다.
		}
		else
		{
			int size = 0;
			for(int i=offset;i<messageBuf.length;i++)
			{
				if(messageBuf[i] == nodeModel.getDelim().getBytes()[0])
					break;
				size++;
			}
			fieldBuf = new byte[size];
			int j=0;
			//System.out.println("FieldNodeVO setFieldBuf="+nodeModel.getKey()+"["+offset+"]["+size+"]["+(offset+size)+"]["+messageBuf.length+"]");		
			if((offset+size) > messageBuf.length)
				return (offset+size); 
			for(int i=offset; i<offset+size; i++)
			{
				fieldBuf[j] = messageBuf[i];
				j++;
			}
			//System.out.println("FieldNodeVO setFieldBuf="+nodeModel.getKey()+"="+new String(fieldBuf)+"["+offset+"]["+size+"]");
			return offset+size+1; // delimiter다음 offset을 리턴한다.
		}
	}

	@Override
	public void printLog() {
		// TODO Auto-generated method stub
		FieldNodeModel nodeModel = (FieldNodeModel)model;
		System.out.println(nodeModel.getKey()+"["+nodeModel.getName()+"] value["+new String(fieldBuf)+"] length["+fieldBuf.length+"]");
	}

	@Override
	public void setValue(FieldVO vo) {
		// TODO Auto-generated method stub
		String id = null;
		if(!vo.isGroupVO())
		{
			id = ((FieldModel)vo.getModel()).getId();
			setValue(id, vo.getValue(id, 0), 0);
		}
	}
	
	public String getValue(String id, int index) {
	    String retStr="";
		if(fieldBuf != null) { 
		    retStr = StringUtil.convert(fieldBuf,charset.toString());
		}
        else
			return null;
		return retStr;
	}

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
        
    }


//	@Override
//	public String getStringTrim(String id, int index) {
//		// TODO Auto-generated method stub
//		if(fieldBuf != null)
//			// 이곳에서 function 기능을 추가한다.
//			return new String(fieldBuf).trim();
//		else
//			return null;
//	}
//
//	@Override
//	public String getStringTrim(String id, int index, String nvl) {
//		// TODO Auto-generated method stub
//		if(fieldBuf != null)
//			// 이곳에서 function 기능을 추가한다.
//			return new String(fieldBuf).trim();
//		else
//			return nvl;
//	}
}
