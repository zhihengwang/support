package com.hexshell.support.log.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author wangzhiheng
 */
public class LogInterceptor {
    private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    private void setPossibleCommonFields(Object resp, boolean isSuccess, String message) {
        if (resp != null) {
            Field successField = this.getFieldIfExist(resp.getClass(), "success", Boolean.class);
            if (successField != null) {
                Object fieldValue = this.getField(successField, resp);
                if (fieldValue == null) {
                    this.setField(successField, resp, isSuccess);
                }
            }

            if (!StringUtils.isEmpty(message)) {
                Field messageField = this.getFieldIfExist(resp.getClass(), "message", String.class);
                if (messageField != null) {
                    Object fieldValue = this.getField(messageField, resp);
                    if (fieldValue == null) {
                        this.setField(messageField, resp, message);
                    }
                }
            }
        }

        this.preReturn(resp, isSuccess);
    }

    protected void preReturn(Object resp, boolean isSuccess) {
    }

    private Field getFieldIfExist(Class<?> clazz, String fieldName, Class<?> fieldClass) {
        try {
            Field field = clazz.getField(fieldName);
            return field.getType().equals(fieldClass) ? field : null;
        } catch (NoSuchFieldException e) {
            return null;
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private Object getField(Field field, Object obj) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Field field, Object obj, Object fieldValue) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            field.set(obj, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
