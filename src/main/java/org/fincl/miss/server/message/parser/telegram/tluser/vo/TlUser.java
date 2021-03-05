package org.fincl.miss.server.message.parser.telegram.tluser.vo;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class TlUser {
	private ArrayList<TlUserField> fields;

	public ArrayList<TlUserField> getFields() {
		return fields;
	}

	public void setFields(String str) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			fields = mapper.readValue(str, new TypeReference<ArrayList<TlUserField>>() {});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (fields == null)
			fields = new ArrayList<TlUserField>();
	}

}
