package com.hexshell.support.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * @author wangzhiheng
 */
public class LogHelper {
    private static Logger logger = LoggerFactory.getLogger(LogHelper.class);

    private LogHelper() {
    }

    public static String getExceptionWithStack(Throwable t) {
        String msg = t.getMessage();

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            msg = (msg == null ? "<No Message>" : msg) + " Related Throwable: " + sw;
        } catch (Exception e) {
            msg = "get stacktrace from Exception[" + t.getMessage() + "] failed for : " + e;
        }

        return msg;
    }

    public static String escapeTag(String s) {
        return s == null ? null : s.replace("]]", "\\]\\]");
    }

    public static String toJsonString(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            logger.error(getExceptionWithStack(e));
            return Objects.toString(o);
        }
    }

    public static String toMaskedJsonString(Object o, String[] paths) {
        if (o == null || paths == null || paths.length == 0) {
            return toJsonString(o);
        }

        Object json = JSON.toJSON(o);

        if (json instanceof JSONObject || json instanceof JSONArray) {
            try {
                for (String p : paths) {
                    if (JSONPath.contains(json, p)) {
                        JSONPath.set(json, p, "***");
                    }
                }
            } catch (Exception e) {
                logger.error(getExceptionWithStack(e));
            }
        }

        return json.toString();
    }
}
