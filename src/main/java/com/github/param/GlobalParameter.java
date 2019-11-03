package com.github.param;

public interface GlobalParameter {

    String JSON_FIELD_NAME = "name";

    String SELECT_TABLE_INFO_SQL = "SELECT\n" +
            "\tCONCAT_WS(';',t.TABLE_NAME, t.COLUMN_NAME, t.ORDINAL_POSITION, t.COLUMN_TYPE ) AS columnSortKey,\n" +
            "\tCONCAT_WS(';',t.TABLE_NAME, t.COLUMN_NAME, t.COLUMN_TYPE ) AS columnKey,\n" +
            "\tt.TABLE_SCHEMA as tableSchema,\n" +
            "\tt.TABLE_NAME as tableName,\n" +
            "\tt.COLUMN_NAME as columnName,\n" +
            "\tt.ORDINAL_POSITION as ordinal_position,\n" +
            "\tt.COLUMN_TYPE as columnType,\n" +
            "\tt.COLUMN_COMMENT as columnComment\n" +
            "FROM\n" +
            "\tinformation_schema.`COLUMNS` t \n" +
            "WHERE\n" +
            "\tt.TABLE_SCHEMA = ? \n" +
            "ORDER BY\n" +
            "\tt.TABLE_NAME,\n" +
            "\tt.ORDINAL_POSITION;";

    String MYSQL_LINK_URL = "jdbc:mysql://%s?useUnicode=true&characterEncoding=utf8";

    String SOURCE_DATA_LINK_KEY = "sourceDataLink";
    String SOURCE_USER_NAME_KEY = "sourceUserName";
    String SOURCE_PASSWORD_KEY = "sourcePassword";

    String TARGET_DATA_LINK_KEY = "targetDataLink";
    String TARGET_USER_NAME_KEY = "targetUserName";
    String TARGET_PASSWORD_KEY = "targetPassword";
}
