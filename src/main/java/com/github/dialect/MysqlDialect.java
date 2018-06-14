package com.github.dialect;

public class MysqlDialect implements IDialect{

    @Override
    public String getPageSql(String sql, Integer startRow, Integer endRow) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        sqlBuilder.append(sql);
        sqlBuilder.append(" limit ");
        sqlBuilder.append(startRow);
        sqlBuilder.append(",");
        sqlBuilder.append(endRow);

        return sqlBuilder.toString();
    }
}
