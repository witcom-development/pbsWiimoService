package org.fincl.miss.server.message.parser.telegram.valueobjects;

import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.message.parser.telegram.model.Model;
import org.fincl.miss.server.util.EnumCode.Charset;

/**
 * - 전문 필드정보를 가지고 있는 인스턴스 -
 */

public abstract class FieldVO {

	protected byte[] fieldBuf = null;
	protected Model model = null;
	protected MessageVO message = null;

	public Model getModel() {
		return model;
	}
	
	public boolean isGroupVO()  {
		return ((FieldModel)model).isGroup();
	}

	public void setModel(Model model) {
		this.model = model;
	}		

	public void setMessage(MessageVO message) {
		this.message = message;
	}

	public byte[] getFieldBuf() {
		return fieldBuf;
	}

	public void setFieldBuf(byte[] fieldBuf) {
		this.fieldBuf = fieldBuf;
	}		
	public abstract void setCharset(Charset charset);
	public abstract void setValue(FieldVO vo);
	public abstract String getValue(String id, int index);
//	public abstract String getStringTrim(String id, int index);
//	public abstract String getStringTrim(String id, int index, String nvl);
	public abstract void setValue(String id, Object obj, int index);
	public abstract void init();
	public abstract byte[] getBytes() throws MessageParserException;
	public abstract int setFieldBuf(int offset, byte[] messageBuf);
	public abstract void printLog();
}
