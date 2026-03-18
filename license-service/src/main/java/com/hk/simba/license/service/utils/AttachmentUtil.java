package com.hk.simba.license.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.hk.simba.license.api.vo.EventAttachment;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author cyh
 * @date 2020/5/14/14:28
 * 附件工具类
 */
public class AttachmentUtil {

    public static List<EventAttachment> getImageAnnex(String annex) {
        if (StringUtils.isBlank(annex)) {
            return null;
        }
        List<EventAttachment> arr = JSONArray.parseArray(annex, EventAttachment.class);
        for (Iterator<EventAttachment> it = arr.iterator(); it.hasNext(); ) {
            EventAttachment next = it.next();
            if (!isImage(next.getAttachment())) {
                it.remove();
            }
        }
        return arr;
    }

    public static List<EventAttachment> getOtherAnnex(String annex) {
        if (StringUtils.isBlank(annex)) {
            return null;
        }
        List<EventAttachment> arr = JSONArray.parseArray(annex, EventAttachment.class);
        for (Iterator<EventAttachment> it = arr.iterator(); it.hasNext(); ) {
            EventAttachment next = it.next();
            if (isImage(next.getAttachment())) {
                it.remove();
            }
        }
        return arr;
    }

    private static boolean isImage(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        name = name.toLowerCase();
        String[] extension = new String[]{".bmp", ".jpg", ".jpeg", ".png", ".tif", ".gif", ".pcx", ".tga", ".exif", ".fpx", ".svg", ".psd", ".cdr", ".pcd", ".dxf", ".ufo", ".eps", ".ai", ".raw", ".wmf", ".webp"};
        for (String s : extension) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

}
