package com.acorn.test.unitydemo4.utils;

import android.util.Log;


import java.util.Locale;

/**
 * 打印日志工具类
 *
 * @author ChenChong 2018/9/21 15:47
 */
public final class LogUtil {
    public static boolean mIsDebugMode = true;

    /**
     * 精简日志信息
     */
    private static boolean mSimpleLogMode = true;

    private static final String CLASS_METHOD_LINE_FORMAT = "%s.%s()  Line:%d  (%s)";
    private static final String CLASS_METHOD_LINE_FORMAT_SIMPLE = "Line:%d  (%s)";

    private static String TAG = "Log：";

    public static void outE(String str) {
        if (mIsDebugMode) {
            String logText = "";
            StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
            if (mSimpleLogMode) {
                logText = String.format(Locale.getDefault(), CLASS_METHOD_LINE_FORMAT_SIMPLE, traceElement.getLineNumber(),
                        traceElement.getFileName());
            } else {
                logText = String.format(Locale.getDefault(), CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(),
                        traceElement.getMethodName(), traceElement.getLineNumber(), traceElement.getFileName());
            }

            logText += "  " + str;
            Log.e(TAG, logText);
        }
    }

    public static void outI(String str) {
        if (mIsDebugMode) {
            String logText = "";
            StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
            if (mSimpleLogMode) {
                logText = String.format(Locale.getDefault(), CLASS_METHOD_LINE_FORMAT_SIMPLE, traceElement.getLineNumber(),
                        traceElement.getFileName());
            } else {
                logText = String.format(Locale.getDefault(), CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(),
                        traceElement.getMethodName(), traceElement.getLineNumber(), traceElement.getFileName());
            }

            logText += "  " + str;
            Log.i(TAG, logText);
        }
    }

    public static void outD(String str) {
        if (mIsDebugMode) {
            String logText = "";
            StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
            if (mSimpleLogMode) {
                logText = String.format(Locale.getDefault(), CLASS_METHOD_LINE_FORMAT_SIMPLE, traceElement.getLineNumber(),
                        traceElement.getFileName());
            } else {
                logText = String.format(Locale.getDefault(), CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(),
                        traceElement.getMethodName(), traceElement.getLineNumber(), traceElement.getFileName());
            }

            logText += "  " + str;
            Log.d(TAG, logText);
        }
    }

    /**
     * 日志超长截断打印
     * 截断输出日志
     *
     * @param msg
     */
    public static void longE(String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            Log.e(tag, msg);
        } else {
            while (msg.length() > segmentSize) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, msg);// 打印剩余日志
        }
    }

    public static void oute(String str) {
        outE(str);
    }

    public static void outi(String str) {
        outI(str);
    }

    public static void outd(String str) {
        outD(str);
    }
}
