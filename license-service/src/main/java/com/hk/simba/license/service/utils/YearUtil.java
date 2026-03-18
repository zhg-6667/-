package com.hk.simba.license.service.utils;

import com.hk.base.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

/**
 * @author cyh
 * @date 2020/4/30/16:54
 */
public class YearUtil {

    /**
     * 判断平年闰年
     */
    public static Boolean judgeYear() {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        boolean b = (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) ? true : false;
        return b;
    }

    /**
     * 获取当前月份
     */
    public static int getCurrentMonth() {
        Calendar date = Calendar.getInstance();
        int month = date.get(Calendar.MONTH) + 1;
        return month;
    }


    public static Date getCurrentDayZeroTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;
    }

    public static Date getNextDayZeroTime(Date date, Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.addDays(date, amount));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;
    }

    public static boolean isToday(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (fmt.format(date).toString().equals(fmt.format(new Date()).toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static Date getDateTime(Integer hour, Integer minute, Integer second) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();

    }

    public static boolean isBetween(Date date, Date startDate, Date endDate) {
        boolean isBetween = false;
        long a = date.getTime() - Optional.ofNullable(startDate).map(Date::getTime).orElse(0L);
        long b = date.getTime() - Optional.ofNullable(endDate).map(Date::getTime).orElse(Long.MAX_VALUE);
        if (a >= 0 && b <= 0) {
            isBetween = true;
        }
        return isBetween;
    }

    public static void main(String[] args) {
        Date zero = YearUtil.getNextDayZeroTime(new Date(), 3);
        System.out.println(zero);
    }

}
