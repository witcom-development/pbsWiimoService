package org.fincl.miss.server.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class CharsetConvertUtil {
    public static byte[] convert(byte[] source, Charset sourceCharset, Charset targetCharset) throws IOException {
        // String strSource = new String(source, sourceCharset);
        // System.out.println("strSource1 [" + strSource + "][" + strSource.getBytes().length + "]");
        // ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
        // requestOutputStream.write(strSource.getBytes(targetCharset));
        // System.out.println("aag>" + requestOutputStream.toString(targetCharset.name()));
        // return requestOutputStream.toByteArray();
        CharBuffer cb = CharBuffer.wrap(new String(source, sourceCharset).toCharArray());
        ByteBuffer bf = targetCharset.encode(cb);
        byte[] rt = new byte[bf.remaining()];
        bf.get(rt);
        // System.arraycopy(rt, 0, groupName, 0, rt.length);
        
        // bf.
        return rt;
        
    }
}
