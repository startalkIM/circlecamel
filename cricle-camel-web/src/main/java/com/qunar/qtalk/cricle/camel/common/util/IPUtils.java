package com.qunar.qtalk.cricle.camel.common.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Created by haoling.wang on 2019/1/4.
 */
public class IPUtils {

    private static Pattern ipPattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && !"unknown".equalsIgnoreCase(ip)) {
            String[] ips = ip.split("[,]");
            for (String tmp: ips) {
                if (tmp != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    ip = tmp;
                }
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static long getRemoteIpLongValue(HttpServletRequest request) {
        return convertIpToLong(getRemoteIP(request));
    }

    public static long convertIpToLong(String ip){
        String[] ipPart = ip.split("[.]");
        return (Long.parseLong(ipPart[0]) << 24) + (Long.parseLong(ipPart[1]) << 16) +
                (Long.parseLong(ipPart[2]) << 8) + Long.parseLong(ipPart[3]);
    }

    public static String convertLongToIp(Long ipL){
        return (new StringBuffer()).append(String.valueOf((ipL >>> 24))).append(".")
                .append(String.valueOf((ipL & 0x00FFFFFF) >>> 16)).append(".")
                .append(String.valueOf((ipL & 0x0000FFFF) >>> 8)).append(".")
                .append(String.valueOf((ipL & 0x000000FF))).toString();
    }

    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var1) {
            return null;
        }
    }

    public static boolean isIP(String address) {
        return ipPattern.matcher(address).matches();
    }
}
