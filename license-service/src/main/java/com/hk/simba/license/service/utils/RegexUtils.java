package com.hk.simba.license.service.utils;

import java.util.regex.Pattern;

/**
 * @author cyh
 * @date 2020/3/31/17:25
 */
public class RegexUtils {

    /**
     * 权限字符输入验证
     */
    private static final Pattern pattern = Pattern.compile("^[A-Za-z:0-9]+$");

    private static final Pattern numPattern = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 权限字符输入验证
     *
     * @param str
     * @return
     */
    public static boolean isAllow(String str) {
        return pattern.matcher(str).matches();
    }

    /**
     * 数字验证
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        return numPattern.matcher(str).matches();
    }


}
