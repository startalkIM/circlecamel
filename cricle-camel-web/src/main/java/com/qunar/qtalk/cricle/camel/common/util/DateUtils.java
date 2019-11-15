package com.qunar.qtalk.cricle.camel.common.util;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by haoling.wang on 2019/1/4.
 */
public class DateUtils {

    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    public static final SimpleDateFormat NORMAL_SDF_NO_SING     = new SimpleDateFormat("yyyyMMdd");

    public static final String DEFAULT_YMD_FORMAT               = "yyyy-MM-dd";

    public static final String DEFAULT_YMD_FORMAT_NO_SING       = "yyyyMMdd";

    public static final String DEFAULT_DD_FORMAT = "dd";

    public static final String DEFAULT_YYMMDDHHMMSS_FORMAT = "yyMMddHHmm";

    public static final String DEFAULT_YMDHMS_FORMAT            = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_YMDHMSS_FORMAT_NO_SING   = "yyyyMMddHHmmssSS";

    public static final String DEFAULT_YMDHMS_FORMAT_NOSING     = "yyyyMMddHHmmss";

    public static final String DEAULT_HHMMSS_NOSIGN = "HHmmss";

    public static final long BASE_START_DAY                     = 1546272000000L;

    public static final long ONE_DAY_MILLIS                     = 86400000;

    public static String getCurDateTime() {
        return convertDateToStr(new Date(), DEFAULT_YMDHMS_FORMAT);
    }

    public static String getCurDateDD() {
        return convertDateToStr(new Date(), DEFAULT_DD_FORMAT);
    }

    public static String getCurDateYYMMDDHHMMSS(){
        return convertDateToStr(new Date(),DEFAULT_YYMMDDHHMMSS_FORMAT);
    }

    public static String getCurDate() {
        return convertDateToStr(new Date(), DEFAULT_YMD_FORMAT);
    }

    public static String gerCurHHMMss(){
        return convertDateToStr(new Date(), DEAULT_HHMMSS_NOSIGN);
    }

    public static String getCurDateNoSign() {
        return convertDateToStr(new Date(), DEFAULT_YMD_FORMAT_NO_SING);
    }

    public static String getCurDteTimeNoSign() {
        return convertDateToStr(new Date(), DEFAULT_YMDHMSS_FORMAT_NO_SING);
    }

    public static String getCurDateYYYYMMDDHHMMSS(){
        return convertDateToStr(new Date(), DEFAULT_YMDHMS_FORMAT_NOSING);
    }

    public static String convertDateToStr(Date date) {
        return convertDateToStr(date, DEFAULT_YMDHMS_FORMAT);
    }

    public static String convertDateToStr(Date date, String dateFormat) {
        return (new DateTime(date))
                .toString(DateTimeFormat.forPattern(dateFormat));
    }

    public static double diffDays(Date date1, Date date2) {
        long start = getFirstTimestamp(date1);
        long end = getFirstTimestamp(date2);
        return (double)(end - start) / (60 * 60 * 24 * 1000);
    }

    public static Date parseDate(String date, SimpleDateFormat sdf) {
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            log.error("parse date string occur exception", ex);
        }
        return null;
    }

    public static long getFirstTimestamp(Date date){
        return BASE_START_DAY + ((date.getTime() - BASE_START_DAY) / ONE_DAY_MILLIS) * ONE_DAY_MILLIS;
    }

    public static long getIntervalFromBaseDay(Date date) {
        return (date.getTime()) - BASE_START_DAY;
    }

    public static Timestamp getCurTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static long getSecondsWithDays(int day) {
        return (60 * 60 * 24) * day;
    }
    public static long getSecondsWithHours(int hour) {
        return (60 * 60 ) * hour;
    }

    public static Date parseDateWithDay(String dayStr, String pattern) {
        try {
            if (!Strings.isNullOrEmpty(dayStr)) {
                return DateTime.parse(dayStr, DateTimeFormat.forPattern(pattern)).toDate();
            }
        } catch (Exception e) {
            log.error("parseDateWithDay exception,dayStr:{},pattern:{}", dayStr, pattern, e);
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println(getIntervalFromBaseDay(new Date()));
        System.out.println(getFirstTimestamp(new Date()));
        System.out.println(getSecondsWithDays(2));
        System.out.println(TimeUnit.DAYS.toSeconds(2));
        System.out.println(parseDateWithDay("2019-06-23",DEFAULT_YMD_FORMAT));
    }
}
