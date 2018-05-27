package com.github.db;

import java.util.Date;

public enum MySqlDataTypeEnum{
    TINYINT(Integer.class),
    SAMLLINT(Integer.class),
    INT(Integer.class),
    BIGINT(Long.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    DECIAML(Long.class),
    DATE(Date.class),
    TIME(Date.class),
    YEAR(Date.class),
    DATETIME(Date.class),
    TIMESTAMP(Date.class),
    CHAR(String.class),
    VARCHAR(String.class),
    TINYBLOB(String.class),
    TINYTEXT(String.class),
    BLOB(byte[].class),
    TEXT(String.class),
    MEDIUMBLOB(String.class),
    MEDIUMTEXT(String.class),
    LONGBLOB(String.class),
    LONGTEXT(String.class)
    ;

    private Class javaClass;

    MySqlDataTypeEnum(Class javaClass){
        this.javaClass = javaClass;
    }

    public static void main(String[] args) {
        Class cls = TINYINT.javaClass;
        if(cls.equals(Integer.class)){
            System.out.println("yes");
            System.out.println(TINYINT.name());
        }
    }

    public Class getJavaClass() {
        return this.javaClass;
    }
}
