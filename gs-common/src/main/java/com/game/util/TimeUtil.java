package com.game.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具
 */
public class TimeUtil {

    public static int getDayDiff(Date date, Date newDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String val1 = format.format(date);
            date = format.parse(val1);

            String val2 = format.format(newDate);
            newDate = format.parse(val2);

            return (int) ((date.getTime() - newDate.getTime()) / (1000L * 3600 * 24));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取系统距1970年1月1日总毫秒
     *
     * @return
     */
    public static long getSysCurTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 系统默认时间（时间字段的初始化，2000年为起点）
     *
     * @return
     */
    public static Date getInitDate() {
        return parseDate("2000-01-01 00:00:00");
    }

    /**
     * 获取指定日期距1970年1月1日总秒
     *
     * @param date
     * @return
     */
    public static long getDateToSeconds(Date date) {
        return getCalendar(date).getTimeInMillis() / 1000;
    }

    /**
     * date转long表示方式
     *
     * @param date
     * @return
     */
    public static long getDateToMillis(Date date) {
        return getCalendar(date).getTimeInMillis();
    }

    /**
     * 当前从零点开始的秒数
     *
     * @return
     */
    public static int getCurrentTotal() {
        int hour = getCalendar().get(Calendar.HOUR_OF_DAY);
        int min = getCalendar().get(Calendar.MINUTE);
        int sec = getCalendar().get(Calendar.SECOND);

        if (hour == 0 && min == 0 && sec == 0) {
            return 24 * 3600;
        }

        return hour * 3600 + min * 60 + sec;
    }

    /**
     * 当前从零点开始的秒数
     *
     * @param time
     * @return
     */
    public static int getCurrentTotal(String time) {
        int[] temp = StringSplitUtil.splitToInt(time, "\\:");

        int hour = temp.length > 0 ? temp[0] : 0;
        int min = temp.length > 1 ? temp[1] : 0;
        int sec = temp.length > 2 ? temp[2] : 0;

        return hour * 3600 + min * 60 + sec;
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public static int getCurrentHour() {
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获 取当前分钟
     *
     * @return
     */
    public static int getCurrentMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @return
     */
    public static int getCurrentSecond() {
        return getCalendar().get(Calendar.SECOND);
    }

    /**
     * 获取当前天
     */
    public static int getCurrentDay() {
        return getCalendar().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前Date
     *
     * @return
     */
    public static Date getCurrentDate() {
        Calendar cal = getCalendar();
        return new Date(cal.getTimeInMillis());
    }

    /**
     * 指定的毫秒long值转成Timestamp类型
     *
     * @param value
     * @return
     */
    public static java.sql.Timestamp getMillisToDate(long value) {
        return new Timestamp(value);
    }

    /**
     * 当前系统时间增加值
     *
     * @param type
     * @param value
     * @return
     */
    public static Date addSystemCurTime(int type, int value) {
        Calendar cal = getCalendar();
        switch (type) {
            case Calendar.DATE:// 增加天数
                cal.add(Calendar.DATE, value);
                break;
            case Calendar.HOUR:// 增加小时
                cal.add(Calendar.HOUR, value);
                break;
            case Calendar.MINUTE:// 增加分钟
                cal.add(Calendar.MINUTE, value);
                break;
            case Calendar.SECOND:// 增加秒
                cal.add(Calendar.SECOND, value);
                break;
            case Calendar.MILLISECOND:// 增加毫秒
                cal.add(Calendar.MILLISECOND, value);
                break;
            default:
                break;
        }
        return new Date(cal.getTimeInMillis());
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String getDateFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ctime = format.format(date);
        return ctime;
    }

    /**
     * 获取当前时间的格式化
     *
     * @param dateFormat
     * @return
     */
    public static String getCurrentDate(String dateFormat) {
        return getDateFormat(new Date(), dateFormat);
    }

    /**
     * 获得日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getDate(String date) throws ParseException {
        return formartDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获得日期yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date formartDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date val = format.parse(date);
            return val;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 时间区间的比较（HH:ss:mm格式）
     *
     * @param beginTime
     * @param endTime
     * @param compareTime
     * @return
     */
    public static boolean timeCheckIn(String beginTime, String endTime, String compareTime) {
        int[] begins = StringSplitUtil.splitToInt(beginTime, "\\:");
        int[] ends = StringSplitUtil.splitToInt(endTime, "\\:");
        int[] compares = StringSplitUtil.splitToInt(compareTime, "\\:");

        int bH = begins.length > 0 ? begins[0] : 0;
        int bM = begins.length > 1 ? begins[1] : 0;
        int bS = begins.length > 2 ? begins[2] : 0;

        int eH = ends.length > 0 ? ends[0] : 0;
        int eM = ends.length > 1 ? ends[1] : 0;
        int eS = ends.length > 2 ? ends[2] : 0;

        int cH = compares.length > 0 ? compares[0] : 0;
        int cM = compares.length > 1 ? compares[1] : 0;
        int cS = compares.length > 2 ? compares[2] : 0;

        int b = bS + bM * 60 + bH * 3600;
        int e = eS + eM * 60 + eH * 3600;
        int c = cS + cM * 60 + cH * 3600;

        // 跨天的时间
        if (eH < bH) {
            c += 24 * 3600;
        }

        if (c >= b && c <= e) {
            return true;
        } else {
            return false;
        }
    }

    public static String getSystemCurrentTimeString() {
        return getDateFormat(new Date());
    }

    /**
     * 自定义格式化日期
     *
     * @param date
     * @param dateFormat yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getDateFormat(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String ctime = formatter.format(date);
        return ctime;
    }

    /**
     * 比较日期是否同一天
     *
     * @param date
     * @return
     */
    public static boolean dateCompare(Date date) {
        if (date == null) {
            return false;
        }
        Calendar now = getCalendar();
        Calendar other = getCalendar(date);
        return dateCompare(now, other) == 0 ? true : false;
    }

    /**
     * 比较两个时间是否相等
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean dataCompare(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar c1 = getCalendar(date1);
        Calendar c2 = getCalendar(date2);
        return dateCompare(c1, c2) == 0 ? true : false;
    }

    /**
     * 返回两个日期相差天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static int dateCompare(Calendar startDate, Calendar endDate) {
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);

        int day = (int) (endDate.getTimeInMillis() / 1000 / 60 / 60 / 24
                - startDate.getTimeInMillis() / 1000 / 60 / 60 / 24);
        return day;
    }

    /**
     * 返回两个日期相差天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static int dateCompare(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        Calendar c1 = getCalendar(startDate);
        Calendar c2 = getCalendar(endDate);
        return dateCompare(c1, c2);
    }

    /**
     * 比较日期是否是同一个月份
     *
     * @param date 被比较的日期
     * @return
     */
    public static boolean monthCompare(Date date) {// 一年之内是否是同一个月
        if (date == null) {
            return false;
        }
        Calendar now = getCalendar();
        Calendar other = getCalendar(date);
        int nowMonth = now.get(Calendar.MONTH) + 1;
        int otherMonth = other.get(Calendar.MONTH) + 1;
        return (otherMonth - nowMonth) == 0 ? true : false;
    }

    /**
     * 获取该月的天数
     *
     * @return
     */
    public static int monthDays() {// 返回当前月份的天数
        Calendar now = getCalendar();
        return now.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前是该月的第几天
     *
     * @return
     */
    public static int monthDay() {
        Calendar now = getCalendar();
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算两个时间的时间差
     *
     * @param startTime
     * @param endTime
     * @return 时间差毫秒
     */
    public static long calcDistanceMillis(Date startTime, Date endTime) {
        return endTime.getTime() - startTime.getTime();
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static Calendar getCalendar() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        return nowCalendar;
    }

    /**
     * 获取指定的时间
     *
     * @param date
     * @return
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = new GregorianCalendar();

        if (date == null) {
            date = new Date();
        }

        calendar.setTime(date);

        return calendar;
    }

    /**
     * 获取下一个星期天的日期
     *
     * @return
     */
    public static Date getNextSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        Date monday = currentDate.getTime();
        return monday;
    }

    /**
     * 获取下一个星期一的日期
     *
     * @return
     */
    public static Date getNextMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        Date monday = currentDate.getTime();
        return monday;
    }

    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期一是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    /**
     * 获取当日处于这周的哪一天 从星期一开始，1表示星期一，2表示星期二.....
     *
     * @return
     */
    public static int getDayOfWeekIndex() {
        Calendar calendar = Calendar.getInstance();
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (index == 0) {
            index = 7;
        }
        return index;
    }

    /**
     * 返回当日的星期中文说明，周一，周日
     *
     * @return
     */
    public static String getDayOfWeekName() {
        Calendar calendar = Calendar.getInstance();
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (index == 0) {
            index = 7;
        }

        switch (index) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "周一";
        }
    }

    /**
     * 与当前时间对比，判断是否超时
     *
     * @param expDate
     * @return
     */
    public static boolean isTimeOut(Date expDate) {
        Calendar curentDate = Calendar.getInstance();
        Calendar expirtDate = Calendar.getInstance();
        expirtDate.setTime(expDate);

        long intervalMillis = expirtDate.getTimeInMillis()
                - curentDate.getTimeInMillis();
        return intervalMillis <= 0;
    }

    /**
     * 获取星期六的日期
     *
     * @param nextWeek 表示哪一周，0表示当周，1表示下一周，后面类似
     * @return
     */
    public static Date getSaturday(int nextWeek) {
        int mondayPlus = getMondayPlus();
        if (nextWeek > 0) {
            mondayPlus = mondayPlus + (nextWeek * 7);
        }
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 5);
        currentDate.set(Calendar.HOUR_OF_DAY, 5);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        Date saturday = currentDate.getTime();
        return saturday;
    }

    /**
     * 判断今天是不是星期六
     *
     * @return
     */
    public static boolean isSaturday() {
        int dayIndex = getDayOfWeekIndex();
        if (6 == dayIndex) {
            return true;
        }
        return false;
    }

    /**
     * 解析时间
     *
     * @param dateStr 时间字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date parseDate(String dateStr) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = df.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回两个日期相隔的（小时，分钟，秒）
     *
     * @param startTime
     * @param endTime
     * @param type      [day,hour,min,sec]
     * @return
     */
    public static int timeSpan(Date startTime, Date endTime, String type) {
        if (startTime == null || endTime == null) {
            return 0;
        }

        long span = endTime.getTime() - startTime.getTime();
        if (type.equalsIgnoreCase("day")) {
            int a = (int) (span / (24 * 60 * 60 * 1000));
            return a;
        } else if (type.equalsIgnoreCase("hour")) {
            return (int) (span / (60 * 60 * 1000));
        } else if (type.equalsIgnoreCase("min")) {
            return (int) (span / (60 * 1000));
        } else if (type.equalsIgnoreCase("sec")) {
            return (int) (span / 1000);
        } else {
            return (int) span;
        }
    }

    public static int timeSpanDays(Date startTime, Date endTime) {
        if (startTime == null || endTime == null)
            return 0;
        if (isSameDay(startTime, endTime)) {
            return 0;
        }
        endTime = parseDate(getDateFormat(endTime).split(" ")[0] + " 00:00:00");
        long span = endTime.getTime() - startTime.getTime();
        return (int) (span / (24 * 60 * 60 * 1000) + 1);
    }

    /**
     * 返回两个日期相隔的（小时，分钟，秒）
     *
     * @param startTime
     * @param endTime
     * @param type      [day,hour,min,sec]
     * @return
     */
    public static int timeSpan(long startTime, long endTime, String type) {
        long span = endTime - startTime;
        if (type.equalsIgnoreCase("day")) {
            return (int) (span / (24 * 60 * 60 * 1000));
        } else if (type.equalsIgnoreCase("hour")) {
            return (int) (span / (60 * 60 * 1000));
        } else if (type.equalsIgnoreCase("min")) {
            return (int) (span / (60 * 1000));
        } else if (type.equalsIgnoreCase("sec")) {
            return (int) (span / 1000);
        } else {
            return (int) span;
        }
    }

    /**
     * 比较两个日期是否在同一周内<br>
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isInSameWeek(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);

        if (c1.get(Calendar.WEEK_OF_YEAR) != c2.get(Calendar.WEEK_OF_YEAR)) {
            // 如果周数不同, 则可确定不是同一周
            return false;
        }
        if (Math.abs(c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) > 1) {
            // 如果年之间大于1年, 则肯定不是同一周
            return false;
        }

        // 其他情况则判断 date2 是否在 date1 的周一到周日中
        int mondayOffset = c1.get(Calendar.DAY_OF_WEEK) - 1;
        if (mondayOffset == 0) {
            mondayOffset = 7;
        }

        // 计算date1所在周的星期一时间
        Calendar monday = Calendar.getInstance();
        monday.setTime(date1);
        monday.add(Calendar.DATE, -mondayOffset);
        monday.set(Calendar.HOUR_OF_DAY, 0);
        monday.set(Calendar.MINUTE, 0);
        monday.set(Calendar.SECOND, 0);

        // 计算date1所在周的星期日时间
        Calendar sunday = Calendar.getInstance();
        sunday.setTime(monday.getTime());
        sunday.add(Calendar.DATE, 7);
        sunday.set(Calendar.HOUR_OF_DAY, 23);
        sunday.set(Calendar.MINUTE, 59);
        sunday.set(Calendar.SECOND, 59);

        return date2.getTime() >= monday.getTimeInMillis()
                && date2.getTime() <= sunday.getTimeInMillis();
    }

    /**
     * 是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String d1 = format.format(date1);
        String d2 = format.format(date2);
        return d1.equals(d2);
    }

    /**
     * 比较日期是否同一天
     *
     * @param
     * @return
     */
    public static boolean isSameDay(long date1, long date2) {
        Calendar now = getCalendar(getMillisToDate(date1));
        Calendar other = getCalendar(getMillisToDate(date2));
        return dateCompare(now, other) == 0 ? true : false;
    }

    /**
     * 是否是同一月
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String d1 = format.format(date1);
        String d2 = format.format(date2);
        return d1.equals(d2);
    }

    /**
     * 判断今天是否是指定的星期几
     *
     * @param weekDay 1为星期1....不写了
     * @return
     */
    public static boolean isWeekDay(Date date, int weekDay) {
        if (weekDay > 7 || weekDay < 1) {
            return false;
        }
        if (weekDay == 7) {
            weekDay = 0;
        }

        Calendar compareDate = Calendar.getInstance();
        compareDate.setTime(date);
        return compareDate.get(Calendar.DAY_OF_WEEK) == (weekDay + 1);
    }

    /**
     * 是否是昨天
     *
     * @param date
     * @return
     */
    public static boolean isYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);

        return calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 日期的加减
     *
     * @param type  Calendar.DATE ,Calendar.HOUR ,Calendar.MINUTE, .....
     * @param value
     * @return
     */
    public static Date addOrRemoveDate(Date date, int type, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        switch (type) {
            case Calendar.DATE:// 增加天数
                cal.add(Calendar.DATE, value);
                break;
            case Calendar.HOUR:// 增加小时
                cal.add(Calendar.HOUR, value);
                break;
            case Calendar.MINUTE:// 增加分钟
                cal.add(Calendar.MINUTE, value);
                break;
            case Calendar.SECOND:// 增加秒
                cal.add(Calendar.SECOND, value);
                break;
            case Calendar.MILLISECOND:// 增加毫秒
                cal.add(Calendar.MILLISECOND, value);
                break;
            default:
                System.err.println("当前类型不存在！");
                break;
        }
        return new Date(cal.getTimeInMillis());
    }

    /**
     * 判断时间是否超过现在时间
     *
     * @param hour
     * @param min
     * @param sec
     * @return
     */
    public static boolean isTimeBefore(int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);// 得到24小时机制的
        int m = cal.get(Calendar.MINUTE);
        int s = cal.get(Calendar.SECOND);

        if (h < hour) {
            return false;
        }

        if (m < min) {
            return false;
        }

        if (s < sec) {
            return false;
        }
        return true;
    }

    /**
     * 格式化时间
     *
     * @param date
     * @param format HH:mm:ss:SSS
     * @return
     */
    public static String getTimeFormat(Date date, String format) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format);
        String ctime = formatter.format(date);
        return ctime;
    }

    public static void main(String[] args) {
        Date date = new Date();

        Date startDate = parseDate("2018-03-07 16:40:47");

        int sec = TimeUtil.timeSpan(getInitDate(), date, "sec");
        System.out.println(sec);
    }

    /**
     * 判断是否是今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }

        String d1 = getDateFormat(date, "yyyy-MM-dd");
        String d2 = getDateFormat(new Date(), "yyyy-MM-dd");
        return d1.equals(d2);
    }

    /**
     * @param date
     * @return
     */
    public static Date getDateByDay(Date date) {
        String value = TimeUtil.getDateFormat(date, "yyyy-MM-dd");
        date = TimeUtil.formartDate(value, "yyyy-MM-dd");
        return date;
    }

}
