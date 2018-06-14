package com.github.dialect;

public interface IDialect {

    String getPageSql(String sql,Integer startRow,Integer endRow);
}
