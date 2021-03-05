package org.fincl.miss.server.message.parser.telegram.util;

import java.math.BigDecimal;
import java.util.StringTokenizer;

/** 
 * - 전문 필드의 값 셋팅관련 유틸리티 -
 *  
 */

public class FieldUtil {

    /**
     * 전문의 해당위치의 값을 Char형으로 바꾸는 함수(뒤에 빈칸으로 채운다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static byte[] setChar(String val, int size) {
        if (val == null) val = " ";
        byte[] temp = val.getBytes();
        byte[] retbuf = new byte[size];

        if (temp.length > size) {
            temp = convCuttingLength(val, size);
        }
        for (int i = 0; i < temp.length; i++) {
            retbuf[i] = temp[i];
        }
        for (int i = temp.length; i < size; i++) {
            retbuf[i] = (byte)' ';
        }
        return retbuf;
    }
    public static byte[] setChar(byte[] buffer, int size) {
        if (buffer == null) buffer = new byte[1];
        byte[] temp = buffer;
        byte[] retbuf = new byte[size];

        if (temp.length > size) {
            temp = convCuttingLength(buffer, size);
        }
        for (int i = 0; i < temp.length; i++) {
            retbuf[i] = temp[i];
        }
        for (int i = temp.length; i < size; i++) {
            retbuf[i] = (byte)' ';
        }
        return retbuf;
    }
    /*
     * 2byte 공백 문자열을 제거한다.
     */
    public static String remove2Spaces(String val, int size) {
        byte[] result = new byte[size];
        byte[] temp = val.getBytes();
        int i = 0;
        boolean isInputFirstSpace = false;

        int fld_size = 0;

        if (result.length > temp.length) fld_size = temp.length;
        else fld_size = result.length;

        for (int j = 0; j < fld_size; j++) {
            if (temp[j] != (byte)' ') {
                result[i] = temp[j];
                isInputFirstSpace = false;
                i++;
            } else if (temp[j] == (byte)' ' && !isInputFirstSpace) {
                result[i] = temp[j];
                isInputFirstSpace = true;
                i++;
            }
        }
        for (; i < size; i++) {
            result[i] = (byte)' ';
        }
        return new String(result);
    }

    /**
     * 전문의 해당위치의 값을 Num형으로 바꾸는 함수(앞에 0으로 채운다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static byte[] setNumOriginal(String val, int size) {
        if (val == null || val.trim().equals("")) val = "0";
        byte[] temp = val.getBytes();
        byte[] retbuf = new byte[size];

        if (temp.length > size) {
            temp = convCuttingLength(val, size);
            // throw new FormatException("[setNumValue]길이보다 큰값을 셋팅하였습니다.");
        }
        for (int i = 0; i < (size - temp.length); i++) {
            retbuf[i] = (byte)'0';
        }

        int j = 0;
        for (int i = (size - temp.length); i < size; i++) {
            retbuf[i] = temp[j];
            j++;
        }
        return retbuf;
    }

    /**
     * 전문의 해당위치의 값을 Num형으로 바꾸는 함수(앞에 0으로 채운다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static byte[] setNum(String val, int size) {
        if (val == null || val.trim().equals("")) val = "0";
        byte[] temp = val.getBytes();
        byte[] retbuf = new byte[size];

        if (temp.length > size) {
            temp = convCuttingLength(val, size);
            // throw new FormatException("[setNumValue]길이보다 큰값을 셋팅하였습니다.");
        }

        int i = 0;
        int j = 0;
        int remainSize = size - temp.length;

        if (temp[0] == (byte)'-') {
            retbuf[0] = (byte)'-';
            i = 1;
            j = 1;
            remainSize++;
            // System.out.println("-입력");
        } else if (temp[0] == (byte)'+') {
            retbuf[0] = (byte)'0';
            i = 1;
            j = 1;
            remainSize++;
        }

        for (; i < remainSize; i++) {
            retbuf[i] = (byte)'0';
            // System.out.println("0채움"+(char)retbuf[i]);
        }

        for (i = remainSize; i < size; i++) {
            // System.out.println("복사"+(char)temp[j]);
            retbuf[i] = temp[j];
            j++;
        }
        return retbuf;
    }

    /**
     * 전문의 해당위치의 값을 Num형으로 바꾸는 함수(앞에 +/- 를 붙인다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static void setNumPlusMinus(byte[] buf, int size, String val) {
        if (val == null || val.trim().equals("")) val = "0";
        byte[] temp = val.trim().getBytes();
        buf = new byte[size];

        if (temp.length > size) {
            temp = convCuttingLength(val, size);
        }

        char sign = '+';
        int j = 0;

        if (temp[0] == (byte)'-') sign = '-';

        buf[0] = (byte)sign;

        for (int i = 1; i < (size - temp.length); i++) {
            buf[i] = (byte)'0';
        }

        for (int i = (size - temp.length); i < size; i++) {
            if (temp[j] == (byte)'-' || temp[j] == (byte)'+') temp[j] = (byte)'0';
            buf[i] = temp[j];
            j++;
        }
    }

    /**
     * 전문중 해당위치의 값을 숫자형으로 가져오는 함수 앞에 + 기호 및 0으로 채워진 값을 없앤다.
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param byte[] inbuf - 전문bytes
     * 
     * @return byte[] - 값
     * 
     * @exception
     */
    public static String getNumPlusMinus(byte[] val) {
        String result = "";
        result = new String(val).trim();
        try {
            result = String.valueOf(new BigDecimal(result));
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 소수점 자리수를 포함한 전문을 리턴한다.
     * 
     * <PRE></PRE>
     * 
     * @param String
     *            val - 입력숫자문자열
     * @param int fraction_length - 소수점 자리수
     * @param byte[] inbuf - 전문bytes
     * 
     * @return byte[] - 값
     * 
     * @exception
     */
    /**
     * 전문의 해당위치의 값을 Irt형으로 바꾸는 함수(앞에 +/- 를 붙인다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static byte[] setDec(String val, int size, int point) {
        // 소수점 앞자리는 왼쪽 패팅 소수점은 오른쪽 패딩~~!!
        if (val == null || val.trim().equals("")) val = "0.0";

        // 숫자검증로직 추가
        BigDecimal check = new BigDecimal(val);
        val = check.toPlainString();
        // val = check.toString();
        // System.out.println("val="+val);

        if (val.indexOf(".") == 0) val = "0" + val;

        StringTokenizer stz = new StringTokenizer(val, ".");
        if (stz.countTokens() == 1) {
            val = val + ".0";
            stz = new StringTokenizer(val, ".");
        }
        String body = stz.nextToken();
        String frac = stz.nextToken();

        // System.out.println("body="+body);
        // System.out.println("frac="+frac);

        byte[] buf = new byte[size];
        byte[] bufBody = body.getBytes();
        byte[] bufFrac = frac.getBytes();

        int signPos = 0;
        if (bufBody[0] == (byte)'-') {
            buf[0] = (byte)'-';
            signPos = 1;
        } else if (bufBody[0] == (byte)'+') {
            buf[0] = (byte)'0';
            signPos = 1;
        }
        // System.out.println("size-point-bufBody.length-pos="+(size-point-bufBody.length+signPos-1));

        int pos = signPos;
        for (; pos < (size - point - bufBody.length + signPos - 1); pos++) {
            buf[pos] = (byte)'0';
        }

        int numSize = pos + bufBody.length - signPos;
        // System.out.println("numSize="+numSize);
        int j = 0;
        for (; pos < numSize; pos++) {
            // System.out.println("pos=" + pos + " j=" + j + " numSize=" +
            // numSize + " bufBody.length=" + bufBody.length + "[" + (char)
            // bufBody[j] + "]");
            if (bufBody[j] == (byte)'-' || bufBody[j] == (byte)'+') {
                j++;
                pos--;
            } else {
                buf[pos] = bufBody[j];
                j++;
            }
        }
        buf[pos] = (byte)'.';
        pos++;

        int posSize = point - bufFrac.length;
        j = 0;
        for (; pos < size; pos++) {
            if (j < bufFrac.length) {
                buf[pos] = bufFrac[j];
                j++;
            } else if (posSize > 0) {
                buf[pos] = (byte)'0';
                posSize--;
            }
        }

        return buf;
    }

    /**
     * 전문중 해당위치의 값을 숫자형으로 가져오는 함수 앞에 + 기호 및 0으로 채워진 값을 없앤다. 또한, 호스트는 소수점이 안들오기
     * 때문에 소수점을 이하로 나누어 리턴한다.
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param byte[] inbuf - 전문bytes
     * @param int point - 소수점이하 자리수
     * 
     * @return byte[] - 값
     * 
     * @exception
     */
    public static String getDec(byte[] val, int point) {
        String result = "";
        int decpoint = 1;

        result = new String(val).trim();
        BigDecimal dresult = new BigDecimal(result);

        for (int i = 0; i < point; i++)
            decpoint = decpoint * 10;

        try {

            result = String.valueOf(dresult.divide(new BigDecimal(decpoint), BigDecimal.ROUND_DOWN));
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 전문중 해당위치의 값을 숫자형으로 가져오는 함수 앞에 + 기호 및 0으로 채워진 값을 없앤다. 또한, 이율등 해당 소수점이 있는
     * 경우도 나누어 리턴한다.
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param byte[] inbuf - 전문bytes
     * @param int point - 소수점이하 자리수
     * 
     * @return byte[] - 값
     * 
     * @exception
     */
    public static String getIrt(byte[] val, int size, int point) {
        String result = "";
        int decpoint = 1;

        result = new String(val).trim();
        BigDecimal dresult = new BigDecimal(result);

        for (int i = 0; i < point; i++)
            decpoint = decpoint * 10;

        try {
            result = String.valueOf(dresult.divide(new BigDecimal(decpoint), point, BigDecimal.ROUND_DOWN));
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 전문의 해당위치의 값을 Irt형으로 바꾸는 함수(앞에 +/- 를 붙인다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static void setIrt(byte[] buf, int size, String val, int point) {
        // 소수점 앞자리는 왼쪽 패팅 소수점은 오른쪽 패딩~~!!
        if (val == null || val.equals("")) val = "0";

        StringTokenizer stz = new StringTokenizer(val, ".");
        val = stz.nextToken();
        int numSize = size - point;
        byte[] temp = val.getBytes();
        buf = new byte[size];

        char sign = '+';
        int j = 0;
        if (temp[0] == (byte)'-') sign = '-';

        buf[0] = (byte)sign;

        // 소수점 앞자리는 왼쪽 패팅
        for (int i = 1; i < (numSize - temp.length); i++) {
            buf[i] = (byte)'0';
        }

        for (int i = (numSize - temp.length); i < numSize; i++) {
            if (temp[j] == (byte)'-' || temp[j] == (byte)'+') temp[j] = (byte)'0';
            buf[i] = temp[j];
            j++;
        }
    }

    /**
     * 전문의 해당위치의 값을 LChar형으로 바꾸는 함수(앞에 빈칸으로 채운다.)
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static void setLChar(byte[] buf, int size, String val) {
        if (val == null) val = " ";
        byte[] temp = val.getBytes();
        buf = new byte[size];

        if (temp.length > size) {
            temp = convCuttingLength(val, size);
            // throw new FormatException("[setNumValue]길이보다 큰값을 셋팅하였습니다.");
        }
        for (int i = 0; i < (size - temp.length); i++) {
            buf[i] = (byte)' ';
        }

        int j = 0;
        for (int i = (size - temp.length); i < size; i++) {
            buf[i] = temp[j];
            j++;
        }
    }

    /**
     * 전문의 해당위치의 값을 날짜형으로 바꾸는 함수
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static byte[] setDate(String val, int size) {
        String tempval = null;

        if (val == null || val.trim().length() < 8) {
            if (size == 10) tempval = "    -  -  ";
            else tempval = " ";
        } else {
            if (size == 10) tempval = getHyphenatedDate(val);
            else tempval = val.trim();
        }
        return setChar(tempval, size);
    }

    /**
     * 전문중 해당위치의 값을 날짜형으로 가져오는 함수
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param byte[] inbuf - 전문bytes
     * 
     * @return byte[] - 값
     * 
     * @exception
     */
    public static String getDate(byte[] inbuf) {
        String tempval = new String(inbuf);
        tempval = getUnHyphenDate(tempval.trim());
        if (tempval.equals("11110101") || tempval.equals("99991231")) tempval = "";
        return tempval;
    }

    /**
     * 전문의 해당위치의 값을 시간형으로 바꾸는 함수
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param String
     *            val - 값
     * 
     * @return void
     * 
     * @exception FormatException
     *                - 값이 길이보다 크면 Exception발생
     */
    public static byte[] setTime(byte[] buf, int size, String val) {
        if (val.length() < 6) val = new String("000000");
        else if (val.length() > 6) val = val.substring(0, 6);
        String tempval = val.substring(0, 2) + ":" + val.substring(2, 4) + ":" + val.substring(4, 6);
        return setChar(tempval, size);
    }

    /**
     * 전문중 해당위치의 값을 날짜형으로 가져오는 함수
     * 
     * <PRE></PRE>
     * 
     * @param int soff - Offset (시작위치)
     * @param int size - 길이
     * @param byte[] inbuf - 전문bytes
     * 
     * @return byte[] - 값
     * 
     * @exception
     */
    public static String getTimeValue(byte[] inbuf) {
        String val = new String(inbuf);
        String tempval = val.substring(0, 2) + val.substring(3, 5) + val.substring(6, 8);
        return tempval;
    }

    // 문자열을 길이만큼 자른다.
    public static byte[] convCuttingLength(String Lst_String, int size) {
        int idx = 0;

        byte[] string = Lst_String.getBytes();
        byte[] converted = new byte[size];

        for (idx = 0; idx < size; idx++) {
            if (string[idx] < (byte)0) /* 한글의 경우 */
            {
                if ((idx + 1) >= size) {
                    converted[idx] = (byte)0x20; // 빈칸으로 채움
                } else {
                    converted[idx] = string[idx];
                    idx++;
                    converted[idx] = string[idx];
                }
            } else {
                converted[idx] = string[idx];
            }
        }
        return converted;
    }
    // 문자열을 길이만큼 자른다.
    public static byte[] convCuttingLength(byte[] lst_Byte, int size) {
        int idx = 0;

        byte[] string = lst_Byte;
        byte[] converted = new byte[size];

        for (idx = 0; idx < size; idx++) {
            if (string[idx] < (byte)0) /* 한글의 경우 */
            {
                if ((idx + 1) >= size) {
                    converted[idx] = (byte)0x20; // 빈칸으로 채움
                } else {
                    converted[idx] = string[idx];
                    idx++;
                    converted[idx] = string[idx];
                }
            } else {
                converted[idx] = string[idx];
            }
        }
        return converted;
    }
    /*
     * Util 성 메소드 모음
     */
    private static String getHyphenatedDate(String toDate) {
        if (toDate == null || toDate.trim().equals("") || Integer.parseInt(toDate) == 0 || toDate.length() != 8) {
            return "0000-00-00"; // 차세대 날짜형식 요건에 맞춤
        }
        return toDate.substring(0, 4) + "-" + toDate.substring(4, 6) + "-" + toDate.substring(6, 8);
    }

    private static String getUnHyphenDate(String toDate) {
        if (toDate == null || toDate.trim().equals("") || Integer.parseInt(toDate) == 0 || toDate.length() != 10) {
            return "0000-00-00"; // 차세대 날짜형식 요건에 맞춤
        }
        return toDate.substring(0, 4) + toDate.substring(5, 7) + toDate.substring(8, 10);
    }
}
