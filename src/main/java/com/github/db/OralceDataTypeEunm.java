package com.github.db;

import java.util.Date;

public enum OralceDataTypeEunm {
    CHAR(String.class),
    VARCHAR2(String.class),
    LONG(Long.class),
    NUMBER(Integer.class),
    DATE(Date.class),
    TIMESTAMP(Date.class),
    BLOB(byte[].class),
    CLOB(String.class)
    ;

    private OralceDataTypeEunm(Class javaClass){
        this.javaClass = javaClass;
    }

    private Class javaClass;

    public Class getJavaClass(){
        return this.javaClass;
    }

    public static void main(String[] args) {
        System.out.println(BLOB.javaClass);
    }
}
