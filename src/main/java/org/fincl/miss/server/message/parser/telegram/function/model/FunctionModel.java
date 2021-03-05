package org.fincl.miss.server.message.parser.telegram.function.model;


import java.math.BigDecimal;
import java.text.DecimalFormat;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.fincl.miss.server.exeption.MessageParserException;
import org.mvel2.MVEL;

/** 
 * - 전문이나 맵핑의 Function 기능을 제공하기 위한 모델이다. -
 * 
 */
 
public class FunctionModel {
    
//    private static final transient Log LOG = LogFactory.getLog(FunctionModel.class);
    private String func_descripter = null;
     
    private String method = null;
    private String key = null;
    private String params = null;
    
    public FunctionModel() {
        
    }
    
    public FunctionModel(String func_descripter) {
        this.func_descripter = func_descripter;
    }
    public String getFunc_descripter() {
        return func_descripter;
    }

    public void setFunc_descripter(String func_descripter) {
        this.func_descripter = func_descripter;
    }
    
    public void parsing() throws MessageParserException{
        if(func_descripter.indexOf("format") > -1)
        {
            
            this.method = "format";
            int start = func_descripter.indexOf("(")+1;
            int end = func_descripter.indexOf(")");
            this.params =  func_descripter.substring(start,end);
  
        }
    }
    
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Object execute(Object obj) {
        if(func_descripter == null || func_descripter.equals(""))
            return obj;
        else if(func_descripter.indexOf("format") > -1)
        {
            parsing();
            return format(obj, params);
        }
        else if(func_descripter.indexOf("toNumberTrim") > -1)
        {
            String str = obj.toString();
            String frac_val = null;
            if(str.indexOf(".") > -1)
            {
                frac_val = str.substring(str.indexOf(".")+1);
//                System.out.println("frac_value="+frac_val);
            }
            
            BigDecimal number = new BigDecimal(str);
            if(frac_val != null && Integer.parseInt(frac_val) == 0)
                return ""+number.longValue();
            else
                return ""+number;
        }
        else if(func_descripter.indexOf("toNumber") > -1)
        {
            BigDecimal number = new BigDecimal(obj.toString());
            
            return ""+number;
        }
        else
            return MVEL.eval(func_descripter, obj);
    }
    
    public String format(Object object, String format) {
        if (object == null)
            return "";
        if (format == null)
            return object.toString(); 
        if (object instanceof String) {
            String s = ((String) object).trim();
            int j = 0;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < format.length(); i++) {
                if (format.charAt(i) == '#') {
                    if (j >= s.length())
                        return sb.toString();
                    sb.append(s.charAt(j++));
                } else {
                    sb.append(format.charAt(i));
                }
            }
            return sb.toString();
        }
        if (object instanceof Number || object instanceof BigDecimal) {
            DecimalFormat df = new DecimalFormat(format);
            return df.format(object); 
        }
        return object.toString();
    }
}
