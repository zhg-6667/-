package com.hk.simba.license.service.utils;

import com.hk.base.util.DateUtils;

import java.util.Date;
import java.util.Random;

/**
 * @author cyh
 * @date 2020/7/13/10:22
 * 交易码生成器
 */
public class CodeUtil {

    private static final String NUMBER = "1234567890";
    private static final String STR = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateCode() {
        String timestamp = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
        String random = generateRandom(true, 4);
        return timestamp + random;
    }


    /**
     * 生成随机字符
     *
     * @param numberFlag 是否纯数字
     * @param length     字符位数
     * @return
     */
    public static String generateRandom(boolean numberFlag, int length) {
        String retStr = "";
        String chars = numberFlag ? NUMBER : STR;
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            retStr += chars.charAt(rd.nextInt(chars.length()));
        }
        return retStr;
    }


    public static void main(String[] args) {
        String s = CodeUtil.generateCode();
        String tradeOrderCode = String.format("QS%s-%d-%s", "214", 123, s);
        System.out.println(tradeOrderCode);
    }
}
