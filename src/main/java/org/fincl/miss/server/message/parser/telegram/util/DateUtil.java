package org.fincl.miss.server.message.parser.telegram.util;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

/** 
 * - 전문의 날짜 처리 관련 유틸리티 from IB종 
 */

public class DateUtil {
    
    public final static String dateType0 = "yyyyMM";
    public final static String dateType1 = "yyyyMMdd";
    public final static String dateType2 = "yyyy-MM-dd";
    public final static String dateType3 = "yyyy/MM/dd";
    public final static String TimeStampType1 = "yyyyMMddHHmmssSSS";
    public final static String TimeStampType2 = "yyyy-MM-dd-HH:mm:ss:SSS";
    public final static String TimeStampType3 = "yyyy-MM-dd HH:mm:ss:SSS";
    public final static String TimeStampType4 = "yyMMddHHmmssSSS";
    public final static String TimeStampType5 = "HHmmssSSS";
    
    public final static String timeType1 = "HHmmss";
    public final static String timeType2 = "HH:mm:ss";
    public final static String dateY ="yyyy";
    public final static String dateM ="MM";
    public final static String dateD ="dd";
    
    /** 적용종료일자 9999-12-31 */
    public final static String lastDate = "99991231";
    
    /** 적용종료개월수 99999 */
    public final static String lastMonth = "99999";
    
    private static final long MILLISECOND_OF_DAY = 24 * 60 * 60 * 1000;

    // GMT를 기준으로 9시간후
    private static final int GMT_OFFSET = 9 * 60 * 60 * 1000;

    // private static final Locale CURRENT_LOCALE = Locale.KOREA;

    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    private static final SimpleTimeZone SIMPLE_TIME_ZONE = new SimpleTimeZone(GMT_OFFSET, "KST");

    static Calendar cal_s;
    static Calendar cal_e;

    /**
     * Private constructor, to prevent construction.
     */
    private DateUtil() {} // constructor
    
    /**
     * YYYYmmdd의 날짜 형식을 YYYY-mm-dd형식으로 변환
     * @param strDate
     * @return
     */
    public static String addFormat(String strDate) {
        String strReturn = "";
        
        if (strDate != null && strDate.length() == 8) {
            strReturn =  strDate.substring(0, 4) + "-" + strDate.substring(4, 6) +"-" + strDate.substring(6, 8);  
        } else {
            strReturn = strDate;
        }
        return strReturn;
    }
    
    /**
     * YYYYmmdd의 날짜 형식을 YYYY년 mm월 dd일 형식으로 변환
     * @param strDate
     * @return
     */
    public static String addFormatHAN(String strDate) {
        String strReturn = "";
        
        if (strDate != null && strDate.length() == 8) {
            strReturn =  strDate.substring(0, 4) + "년 " + strDate.substring(4, 6) + "월 " + strDate.substring(6, 8) + "일";  
        } else {
            strReturn = strDate;
        }
        return strReturn;
    }
    
    /**
     * check date string validation with the default format "yyyyMMdd".
     * 날짜 스트링(YYYYmmdd)을 입력받아서 Date 객체로 변환
     * @param s date string you want to check with default format "yyyyMMdd".
     * @return date java.util.Date
     */
    public static java.util.Date string2Date(String yyyyMMdd) 
        throws java.text.ParseException 
    {
        return string2Date(yyyyMMdd, dateType1);
    }

    /**
     * check date string validation with an user defined format.
     * 날짜 스트링과 포멧을 입력받아서 Date 객체로 변환
     * @param s date string you want to check.
     * @param format string representation of the date format. 
     *        For example, "yyyy-MM-dd".
     * @return date java.util.Date
     */
    public static java.util.Date string2Date(String s, String format) 
        throws java.text.ParseException 
    {
        if ( s == null ||s.trim().equals(""))
            return null;
            //throw new java.text.ParseException("date string to check is null",0);
        if ( format == null )
            return null;
            //throw new java.text.ParseException("format string to check date is null", 0);

        java.text.SimpleDateFormat formatter =
            new java.text.SimpleDateFormat (format, java.util.Locale.KOREA);
        java.util.Date date = null;
        try {
            date = formatter.parse(s);
        }
        catch(java.text.ParseException e) {
            throw new java.text.ParseException(" wrong date:\"" + s +
                "\" with format \"" + format + "\"", 0);
        }
        return date;
    }
    
    /**
     * <pre>
     * YYYYMMDD 형식을 받아서 Calendar를 리턴
     * </pre>
     * 
     * @param strDate
     * @return calendar
     */
    public static Calendar toCalendar(String strDate) {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(4, 6));
        int day = Integer.parseInt(strDate.substring(6, 8));

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month - 1, day, 0, 0, 0);

        return calendar;
    }// END OF toCalendar()

    /**
     * YYYYMMDD 형식으로 날짜를 리턴한다.
     * @return
     */
    public static String getCurrentDate() {

        Calendar calendar = Calendar.getInstance(SIMPLE_TIME_ZONE);

        return getFormatDate(calendar, DEFAULT_DATE_FORMAT);
    }// END OF getCurrentDate()

    /**
     * 특정 포멧으로 날짜를 리턴한다.
     * 
     * @param strDateFormat
     * @return
     */
    public static String getCurrentDate(String strDateFormat) {

        Calendar calendar = Calendar.getInstance(SIMPLE_TIME_ZONE);

        return getFormatDate(calendar, strDateFormat);
    }// END OF getCurrentDate()

    /**
     * calDate를 특정 포멧으로 리턴한다.
     * 
     * @param calDate
     * @param strDateFormat
     * @return
     */
    public static String getFormatDate(Calendar calDate, String strDateFormat) {
        Date date = calDate.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);

        simpleDateFormat.setTimeZone(SIMPLE_TIME_ZONE);

        return simpleDateFormat.format(date);
    }// END OF getFormatDate()

    /**
     * 날짜(YYYYmmdd)형식의 문자열을 이용해서 요일을 리턴한다. 0:SUN ... 6:SAT
     * 
     * @param strDate
     * @return
     */
    public static int getDayOfWeek(String strDate) {

        Calendar calendar = toCalendar(strDate);

        return calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0.SUN ... 6.SAT
    }// END OF getDayOfWeek()

    /**
     * 날짜(YYYYmmdd)형식의 문자열과 날짜필드와 offset을 입력 받아서 날짜를 더한다.
     * @param strDate
     * @param intDateField - Calendar.DATE, Calendar.YEAR
     * @param intOffSet
     * @return
     */
    public static String getAddDate(String strDate, int intDateField, int intOffSet) {
        
        String strReturnDate = strDate;
        if(!(strDate == null || "".equals(strDate))) {
            Calendar calendar = toCalendar(strDate);
            calendar.add(intDateField, intOffSet);
            strReturnDate = getFormatDate(calendar, DEFAULT_DATE_FORMAT);
        }

        return strReturnDate;
    }// END OF getAddDate()

    /**
     * 두 날짜(YYYYmmdd)형식의 문자열의 일수를 구한다.
     * 
     * @param strDateFrom
     * @param strDateTo
     * @return 일수
     */
    public static int getDayTerm(String strDateFrom, String strDateTo) {

        Calendar calendarFrom = toCalendar(strDateFrom);
        Calendar calendarTo = toCalendar(strDateTo);

        long lngFrom = calendarFrom.getTimeInMillis();
        long lngTo = calendarTo.getTimeInMillis();

        return (int) ((lngTo - lngFrom) / MILLISECOND_OF_DAY);
    }// END OF getDayTerm()
    
    
    /**
     * 날짜(YYYYmmdd)형식의 문자열을 이용해서 해당월의 1일
     * 
     * @param String strDate
     * @return  String
     */
    public static String getFirstDate(String strDate) {

        Calendar calendar = toCalendar(strDate.substring(0, 6) + "01");

        return getFormatDate(calendar, DEFAULT_DATE_FORMAT);
    }// END OF getDayTerm()

    
    /**
     * 날짜(YYYYmmdd)형식의 문자열을 이용해서 해당월의 마지막일
     * 
     * @param String strDate
     * @return  String
     */
    public static String getLastDate(String strDate) {

        Calendar calendar = toCalendar(strDate.substring(0, 6) + "01");

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        return getFormatDate(calendar, DEFAULT_DATE_FORMAT);
    }// END OF getDayTerm()
    
    /**
     * 해당월 전월 1일
     * 
     * @param String strDate
     * @return  String
     */
    public static String getLastMonthFirstDate(String strDate) {
        Calendar calendar = toCalendar(strDate.substring(0, 6) + "01");
        
        calendar.add(Calendar.MONTH, -1);
        
        return getFormatDate(calendar, DEFAULT_DATE_FORMAT);
    }
    
    /**
     * 해당월 전월 마지막일
     * 
     * @param String strDate
     * @return  String
     */
    public static String getLastMonthLastDate(String strDate) {
        Calendar calendar = toCalendar(strDate.substring(0, 6) + "01");
        
        calendar.add(Calendar.DATE, -1);
        
        return getFormatDate(calendar, DEFAULT_DATE_FORMAT);
    }
    
    /**
     * <pre>
     * 특정월의 차이 GET!!!
     * </pre>
     * 
     * @param strDateFrom
     * @param strDateTo
     * @return month
     */
    public static int getMonthTerm(String strDateFrom, String strDateTo) {
        int month = 0;

        strDateFrom = strDateFrom.substring(0,6)+"01";
        strDateTo   = strDateTo.substring(0,6)+"01";
        
        String tmpAddDate = strDateFrom;
        int intAddMonth = 0;
        
        while (Integer.parseInt(tmpAddDate) < Integer.parseInt(strDateTo)) {
            tmpAddDate = getAddDate(strDateFrom, Calendar.MONTH, ++intAddMonth);

            if(Integer.parseInt(tmpAddDate) == Integer.parseInt(strDateTo)) {
                break;
            }
        }// END OF while LOOP
        
        month = intAddMonth;
        
        return month;
    }// END OF getMonthTerm();
    
    /**
     * <pre>
     * 특정일자의 차이(개월(000)&amp;일(00)) GET!!!
     * </pre>
     * 
     * @param strDateFrom
     * @param strDateTo
     * @return monthDay
     */
    public static String getMonthDayTerm(String strDateFrom, String strDateTo) {
        String monthDay = "";

        String tmpAddDate = strDateFrom;
        String tmpBefDate = strDateFrom;
        int intAddMonth = 0;
        int intAddDay = 0;

        // while(Integer.parseInt(tmpDateFrom)<Integer.parseInt(strDateTo)) {
        while (Integer.parseInt(tmpAddDate) < Integer.parseInt(strDateTo)) {
            tmpAddDate = getAddDate(strDateFrom, Calendar.MONTH, ++intAddMonth);

            if (Integer.parseInt(tmpAddDate) < Integer.parseInt(strDateTo)) {

            } else if (Integer.parseInt(tmpAddDate) == Integer.parseInt(strDateTo)) {
                break;
            } else if (Integer.parseInt(tmpAddDate) > Integer.parseInt(strDateTo)) {
                intAddMonth--;
                intAddDay = getDayTerm(tmpBefDate, strDateTo);
                break;
            }

            tmpBefDate = tmpAddDate;
        }// END OF while LOOP
;

        monthDay = StringUtil.lpad(intAddMonth,3,'0') + StringUtil.lpad(intAddDay,2,'0');

        return monthDay;
    }// END OF getMonthDayTerm();

    /**
     * <pre>
     * 특정일자의 차이개월수 GET!!!
     * </pre>
     * 
     * @param strDateFrom
     * @param strDateTo
     * @param iRoundMode : -1(절사), 0(반올림), 1(절상)
     * @return iMonthCnt
     */
    public static int getMonthTerm(String strDateFrom, String strDateTo, int iRoundMode) {
        int iMonthCnt = 0;
        int iDayCnt = 0;
        double dblMonthCntRaw = 0.0d;
        
        String strMonthDayTerm = getMonthDayTerm(strDateFrom, strDateTo);
        
        iMonthCnt = Integer.parseInt(strMonthDayTerm.substring(0,3));
        iDayCnt   = Integer.parseInt(strMonthDayTerm.substring(3,5));
        
        dblMonthCntRaw = iMonthCnt + (iDayCnt/30.0d);
        
        if(iRoundMode == -1) {      //절사
            iMonthCnt = (int)Math.floor(dblMonthCntRaw);
        }else if(iRoundMode == 0) { //반올림
            iMonthCnt = (int)Math.round(dblMonthCntRaw);
        }else if(iRoundMode == 1) { //절상
            iMonthCnt = (int)Math.ceil(dblMonthCntRaw);
        }
        
        return iMonthCnt;
    }//END OF getMonthTerm()
        
        
    /**
     * <pre>
     * GET CURRENT DATETIME yyyyMMdd:HH:mm:ss.SSS 형태의 String으로 현재 날짜 반환
     * </pre>
     * 
     */
    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();

        java.util.Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd:HH:mm:ss.SSS");

        return simpleDateFormat.format(date);
    }// END OF getCurrentDateTime()

    /**
     * <pre>
     * GET CURRENT DATETIME yyyyMMdd:HH:mm:ss.SSS 형태의 String으로 현재 날짜 반환
     * </pre>
     * 
     */
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();

        java.util.Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        return simpleDateFormat.format(date);
    }// END OF getCurrentDateTime()
    
    
    /**
     * 현재 시각을 HHmmss형식으로 반환한다.
     * @return
     */
    public static String getCurrentTimeHHmmss() {
        Calendar calendar = Calendar.getInstance();

        java.util.Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeType1);

        return simpleDateFormat.format(date);
    }// END OF getCurrentDateTime()
    
    
    /**
     * 현재 시각을 HHmmss형식으로 반환한다.
     * @return
     */
    public static String getCurrentTime(String format) {
        Calendar calendar = Calendar.getInstance();

        java.util.Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.format(date);
    }// END OF getCurrentDateTime()

    /**
     * <pre>
     * SET TIME POINT
     * 
     * 전역변수 cal_s에 현재 timestamp 를 저장한다.
     * </pre>
     * 
     * @param strFlag - S는 시작지점, E는 종료지점
     * 
     */
    public static void savePoint(String strFlag) {
        if ("S".equals(strFlag)) {
            cal_s = Calendar.getInstance();
        } else if ("E".equals(strFlag)) {
            cal_e = Calendar.getInstance();
        }
    }

    /**
     * <pre>
     * GET DATE DIFF
     * 
     * strDay + &quot;일 &quot; + strHour + &quot;시간 &quot; + strMin + &quot;분 &quot; + strSec + &quot;초&quot; 형식으로 반환한다.
     * 
     * savePoint를 호출하지 않았을 경우에는 &quot;??일 ??시간 ??분 ??초&quot; 의 에러스트링을 리턴하며
     * 
     * savePoint(&quot;E&quot;) 를 호출하지 않았을 경우 현재 시점을 savePoint(&quot;E&quot;) 로 세팅하고
     * 
     * 시작시점과 종료시점의 시간을 리턴한다.
     * 
     * EX) savePoint를("S"); 
     *     ...
     *     getDateDiff();
     * </pre>
     * 
     */
    public static String getDateDiff() throws Exception {

        if (cal_s == null) {
            return "??일 ??시간 ??분 ??초";
        }

        if (cal_e == null) {
            savePoint("E");
        }

        long datedif;

        try {

            datedif = Math.abs((long) ((cal_e.getTimeInMillis() - cal_s.getTimeInMillis()) / 1000));

            long ss = datedif % 60;
            long tot_mm = (long) (datedif / 60);
            long mm = tot_mm % 60;
            long tot_hh = (long) (tot_mm / 60);
            long hh = tot_hh % 24;
            long day = (long) (tot_hh / 24);

            String strDay = "";
            String strHour = "";
            String strMin = "";
            String strSec = "";

            if (day < 10)
                strDay = "0" + day;
            else
                strDay = "" + day;
            if (hh < 10)
                strHour = "0" + hh;
            else
                strHour = "" + hh;
            if (mm < 10)
                strMin = "0" + mm;
            else
                strMin = "" + mm;
            if (ss < 10)
                strSec = "0" + ss;
            else
                strSec = "" + ss;

            return strDay + "일 " + strHour + "시간 " + strMin + "분 " + strSec + "초";

        } catch (Exception e) {
            return "??일 ??시간 ??분 ??초";
        }
    }
    
    /**
     * Get days between 2 calendars
     * @param start
     * @param end
     * @return
     */
    static public int getDiffDays(Calendar start, Calendar end)
    {
        Calendar startCal = Calendar.getInstance();
        startCal.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));
        
        long nTime = endCal.getTime().getTime() - startCal.getTime().getTime();
        
        return (int) (nTime / (1000 * 60 * 60 * 24)) + 1;
    }
    
    /**
     * Get days between 2 dates
     * @param start
     * @param end
     * @return
     */
    public static int getDiffDays(Date start, Date end)
    {
        return getDiffDays(Date2Calendar(start), Date2Calendar(end));
    }
    
    /**
     * Date to calendar
     * @param date
     * @return
     */
    public static Calendar Date2Calendar(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    
    /**
     * 해당 포멧에 따른 오늘 날짜를 리턴
     * 
     * @param date
     * @param format
     * @return String
     */
    public static String dateFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 오늘 날짜를 리턴
     * 
     * @return Date
     */
    public static Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal.getTime();
    }

    /**
     * 오늘 날짜를 리턴한다. 
     * 
     * @return String   YYYYMMDD
     */
    public static String getTodayDateYMD() {
        Calendar cal = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(cal.getTime());
    }
    
    /**
     * 오늘 날짜를 리턴한다. 
     * 
     * @return String   YYYYMM
     */
    public static String getTodayDateYM() {
        Calendar cal = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateType0);
        return sdf.format(cal.getTime());
    }
    
    /**
     * 오늘 날짜를 한글로 리턴한다. 
     * 
     * @return String   YYYY년 MM월 DD일
     */
    public static String getTodayDateYMDHan() {
        Calendar cal = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        return sdf.format(cal.getTime());
    }
    
    /**
     * 오늘 날짜와 시간분을 한글로 리턴한다. 
     * 
     * @return String   YYYY년 MM월 DD일 xx시 xx분
     */
    public static String getTodayDateYMDHMHan() {
        Calendar cal = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ");
        return sdf.format(cal.getTime());
    }


    /**
     * 
     * 넘겨받은 Date param에서 연도만 리턴
     * 
     * @param date
     * @return int
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 해당 Date에서 amount 만큼 연을 더해서 리턴
     * 
     * @param Date 
     * @param int
     * @return Date
     */
    public static Date addYear(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, amount);
        return cal.getTime();
    }

    /**
     * 년수를 더한다
     * @param date
     * @param addYear
     * @return  yyyyMMdd
     */
    public static String addYears(String date, int addYear)
    {
        return addYears(date, addYear, DEFAULT_DATE_FORMAT);
    }

    /**
     * 년수를 더한다
     * @param date
     * @param addYear
     * @param format
     * @return  지정된 포멧 스트링
     */
    public static String addYears(String date, int addYear, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
        Date formattedDate = formatValidDate(date, format);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(formattedDate);
        calendar.add(1, addYear);
        formattedDate = calendar.getTime();
        
        return formatter.format(formattedDate);
    }
    
    /**
     * 해당 Date가 유효한 날짜 형식인지 return
     * 
     * @param String   날짜형식인지 확인하고자하는 스트링
     * @return boolean
     */
    public static boolean isValidDate(String strDate) {
        
        if (strDate == null || strDate.length() != 8) {
            return false;
        }
        
        int year  = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(4, 6));
        int day   = Integer.parseInt(strDate.substring(6, 8));
        
        if (month > 12) {
            return false;
        }
        
        int totalDays = 0;
        
        switch (month) {
        
        case 1 :
            totalDays   = 31;
            break;
        case 2 :
            if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                totalDays   = 29;
            } else {
                totalDays   = 28;
            }
            break;
        case 3 :
            totalDays   = 31;
            break;
        case 4 :
            totalDays   = 30;
            break;
        case 5 :
            totalDays   = 31;
            break;
        case 6 :
            totalDays   = 30;
            break;
        case 7 :
            totalDays   = 31;
            break;
        case 8 :
            totalDays   = 31;
            break;
        case 9 :
            totalDays   = 30;
            break;
        case 10 :
            totalDays   = 31;
            break;
        case 11 :
            totalDays   = 30;
            break;
        case 12 :
            totalDays   = 31;
            break;
        }
        
        if (day > totalDays)
        {
            return  false;
        }

        return  true;
    }

    /**
     * return add day to date strings
     * @param String date string
     * @param int 더할 일수
     * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 일수 더하기
     *           형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
     */
    public static String addDays(String yyyyMMdd, int day) throws java.text.ParseException {
        return addDays(yyyyMMdd, day, dateType1);
    }

    /**
     * return add day to date strings with user defined format.
     * @param String date string
     * @param int 더할 일수
     * @param format string representation of the date format. For example, "yyyy-MM-dd".
     * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 일수 더하기
     *           형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
     */
    public static String addDays(String s, int day, String format) throws java.text.ParseException{
        java.text.SimpleDateFormat formatter =
            new java.text.SimpleDateFormat (format, java.util.Locale.KOREA);
        java.util.Date date = string2Date(s, format);
        
        date.setTime(date.getTime() + ((long)day * 1000 * 60 * 60 * 24));
        return formatter.format(date);
    }    
    
    /**
     * 해당 Date에서 amount 만큼 월을 더해서 리턴
     * 
     * @param date
     * @param addMonth
     * @return Date
     */
    public static String addMonths(String date, int addMonth)
    {
        return addMonths(date, addMonth, dateType1);
    }

    /**
     * 해당 Date에서 amount 만큼 월을 더해서 리턴
     * 
     * @param date
     * @param amount
     * @return Date
     */
    public static String addMonths(String date, int addMonth, String format){

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
        Date formattedDate = formatValidDate(date, format);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.KOREA);
        int year = Integer.parseInt(yearFormat.format(formattedDate));
        int month = Integer.parseInt(monthFormat.format(formattedDate));
        int day = Integer.parseInt(dayFormat.format(formattedDate));
        month += addMonth;
        if(addMonth > 0)
            while(month > 12) 
            {
                month -= 12;
                year++;
            }
        else
            while(month <= 0) 
            {
                month += 12;
                year--;
            }
        DecimalFormat fourDf = new DecimalFormat("0000");
        DecimalFormat twoDf = new DecimalFormat("00");
        String tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month)) + String.valueOf(twoDf.format(day));

        return formatter.format(formatValidDate(tempDate, DEFAULT_DATE_FORMAT));
    }

    public static Date formatValidDate(String date)
    {
        return formatValidDate(date, DEFAULT_DATE_FORMAT);
    }

    public static Date formatValidDate(String date, String format)
    {
        if(date == null || format == null)
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
        Date formattedDate = null;

        try
        {
            formattedDate = formatter.parse(date);
        }
        catch(ParseException e)
        {
        }
        return formattedDate;
    }
    
    /**
     * 해당 연도에 1월 1일 0시 0분 0초의 Date 리턴
     * 
     * @param year
     * @return Date
     */
    // CommonPccs에서 사용
    public static Date getDate(int year) {

        Calendar cal = Calendar.getInstance();
        // yy, mm, dd, hh, mm, ss
        cal.set(year, 0, 1, 0, 0, 0);

        return cal.getTime();
    }

    /**
     * 시스템 날짜에 해당하는 분기를 리턴.
     * 
     * @return String
     */
    public static String getQuarter()
    {
        String yyyyMm = getTodayDateYM();
        String mm = yyyyMm.substring(4, 6);
        String quarter = new String();
        if (mm.equals("01") || mm.equals("02") || mm.equals("03"))
        {
            quarter = "1";
        }
        else if (mm.equals("04") || mm.equals("05") || mm.equals("06"))
        {
            quarter = "2";
        }
        else if (mm.equals("07") || mm.equals("08") || mm.equals("09"))
        {
            quarter = "3";
        }
        else if (mm.equals("10") || mm.equals("11") || mm.equals("12"))
        {
            quarter = "4";
        }
        return quarter;
    }
    
    /**
     * Get a week string by week day and type
     * @param nWeek
     * @param type      유형  0:대문자   1:소문자   2:약어
     * @return
     */
    public static String getWeekString(int nWeek, int type)
    {
        String sWeek = new String();
        switch (nWeek)
        {
            case 1:
                sWeek = (type == 0) ? "SUNDAY" : (type == 1) ? "Sunday" : "Sun";
                break;
            case 2:
                sWeek = (type == 0) ? "MONDAY" : (type == 1) ? "Monday" : "Mon";
                break;
            case 3:
                sWeek = (type == 0) ? "TUESDAY" : (type == 1) ? "Tuesday" : "Tue";
                break;
            case 4:
                sWeek = (type == 0) ? "WEDNSDAY" : (type == 1) ? "Wednsday" : "Wed";
                break;
            case 5:
                sWeek = (type == 0) ? "THURSDAY" : (type == 1) ? "Thursday" : "Thur";
                break;
            case 6:
                sWeek = (type == 0) ? "FRIDAY" : (type == 1) ? "Friday" : "Fri";
                break;
            case 7:
                sWeek = (type == 0) ? "SATURDAY" : (type == 1) ? "Saturday" : "Sat";
                break;
        }
        
        return sWeek;
    }
    
    public static void main(String[] args)
    {
        
    }
}

