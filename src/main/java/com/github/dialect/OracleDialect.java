package com.github.dialect;

public class OracleDialect implements IDialect{

    @Override
    public String getPageSql(String sql, Integer startRow, Integer endRow) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        if(startRow > 0){
            sqlBuilder.append("SELECT * FROM ( ");
        }
        if(endRow > 0){
            sqlBuilder.append(" SELECT TMP_PAGE.* FROM ( ");
        }
        sqlBuilder.append(sql);

        if(endRow > 0){
            sqlBuilder.append(" ) TMP_PAGE WHERE ROWNUM <= ");
            sqlBuilder.append(endRow);
        }

        return sqlBuilder.toString();
    }
}
