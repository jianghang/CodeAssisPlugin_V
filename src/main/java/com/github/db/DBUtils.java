package com.github.db;

import com.github.generator.CodeGenerator;
import com.github.model.DbColumnInfo;
import com.github.utils.CodeStringUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.lang.model.element.Modifier;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBUtils {

    public static Connection getConnection(String projectName, String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static ResultSetMetaData getResultSetMetaData(Connection conn, String sql) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet.getMetaData();
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        generateCode();
        compareData();

    }

    private static void compareData() throws SQLException {
        String targetUrl = "jdbc:mysql://192.168.249.132:3306/wesay?useUnicode=true&characterEncoding=utf8";
        String sourceUrl = "jdbc:mysql://192.168.249.132:3306/mybatis_test?useUnicode=true&characterEncoding=utf8";
        String sql = "SELECT\n" +
                "\tCONCAT_WS(';',t.TABLE_NAME, t.COLUMN_NAME, t.ORDINAL_POSITION, t.COLUMN_TYPE ) AS columnSortKey,\n" +
                "\tCONCAT_WS(';',t.TABLE_NAME, t.COLUMN_NAME, t.COLUMN_TYPE ) AS columnKey,\n" +
                "\tt.TABLE_SCHEMA as tableSchema,\n" +
                "\tt.TABLE_NAME as tableName,\n" +
                "\tt.COLUMN_NAME as columnName,\n" +
                "\tt.ORDINAL_POSITION as ordinal_position,\n" +
                "\tt.COLUMN_TYPE as columnType \n" +
                "FROM\n" +
                "\tinformation_schema.`COLUMNS` t \n" +
                "WHERE\n" +
                "\tt.TABLE_SCHEMA = ? \n" +
                "ORDER BY\n" +
                "\tt.TABLE_NAME,\n" +
                "\tt.ORDINAL_POSITION;";
        String str1 = "192.168.249.132:3306/wesay";
        List<String> strList = Splitter.on("/").splitToList(str1);
        System.out.println(strList.get(1));
        DbUtils.loadDriver(DatabaseTypeEnum.MYSQL.getDatabaseDriver());
        Connection targetConn = DriverManager.getConnection(targetUrl, "admin", "Abc5462.");
        QueryRunner runner = new QueryRunner();
        List<DbColumnInfo> dbColumnInfoList = runner.query(targetConn, sql, new BeanListHandler<>(DbColumnInfo.class), "wesay");
        Table<String, String, DbColumnInfo> targetTable = HashBasedTable.create();
        handleColumnInfoList(dbColumnInfoList, targetTable);
        targetConn.close();

        Connection sourceConn = DriverManager.getConnection(sourceUrl, "admin", "Abc5462.");
        dbColumnInfoList = runner.query(sourceConn, sql, new BeanListHandler<>(DbColumnInfo.class), "mybatis_test");
        Table<String, String, DbColumnInfo> sourceTable = HashBasedTable.create();
        handleColumnInfoList(dbColumnInfoList, sourceTable);
        sourceConn.close();

        Set<String> sourceTableNameSet = sourceTable.rowKeySet();
        sourceTableNameSet.forEach(tableName -> {
            if (targetTable.containsRow(tableName)) {
                compareColumnInfo(sourceTable, targetTable, tableName);
            } else {
                String str = "源数据库[%s]表[%s]存在，目标数据库[%s]不存在";
                str = String.format(str, "mybatis_test", "wesay", tableName);
                System.out.println(str);
            }
        });
    }

    public static List<String> compareColumnInfo(Table<String, String, DbColumnInfo> sourceTable,
                                                 Table<String, String, DbColumnInfo> targetTable,
                                                 String tableName) {
        Map<String, DbColumnInfo> sourceColumnMap = sourceTable.row(tableName);
        Map<String, DbColumnInfo> targetColumnMap = targetTable.row(tableName);
        DbColumnInfo sourceInfo = sourceColumnMap.values().iterator().next();
        DbColumnInfo targetInfo = targetColumnMap.values().iterator().next();
        Set<String> sourceColumnSet = sourceColumnMap.keySet();
        List<String> messageList = Lists.newArrayList();
        sourceColumnSet.forEach(columnKey -> {
            if (targetColumnMap.containsKey(columnKey)) {
                targetColumnMap.remove(columnKey);
            } else {
                DbColumnInfo columnInfo = sourceTable.get(tableName, columnKey);
                String str = "源数据库[%s]数据表[%s]字段[%s]存在，目标数据库[%s]不存在";
                str = String.format(str, sourceInfo.getTableSchema(), columnInfo.getTableName(),
                        columnInfo.getColumnName(), targetInfo.getTableSchema());
//                str = new String(str.getBytes(), Charset.forName("UTF-8"));
                messageList.add(str);
            }
        });

        if (targetColumnMap.size() > 0) {
            targetColumnMap.values().forEach(value -> {
                String str = "源数据库[%s]数据表[%s]字段[%s]不存在，目标数据库[%s]存在";
                messageList.add(String.format(str, sourceInfo.getTableSchema(), value.getTableName(),
                        value.getColumnName(), targetInfo.getTableSchema()));
            });
        }
        return messageList;
    }

    public static void handleColumnInfoList(List<DbColumnInfo> dbColumnInfoList,
                                            Table<String, String, DbColumnInfo> table) {
        dbColumnInfoList.forEach(e -> table.put(e.getTableName(), e.getColumnName(), e));
    }

    private static void generateCode() throws ClassNotFoundException, SQLException {
        String URL = "jdbc:mysql://localhost:3306/myblog?useUnicode=true&characterEncoding=utf8";
        String USER = "root";
        String PASSWORD = "admin";

        URL = "jdbc:oracle:thin:@192.168.0.58:1521:CXDATABASE";
        USER = "cobweb3";
        PASSWORD = "admin";
        //1.加载驱动程序
        Class.forName(DatabaseTypeEnum.ORACLE.getDatabaseDriver());
        //2.获得数据库链接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        //3.通过数据库的连接操作数据库，实现增删改查（使用Statement类）
        Statement st = conn.createStatement();
        String sql = "select t.id from sys_user t";
        sql = "select * from t_fwzy_ssl";
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        int count = resultSetMetaData.getColumnCount();
        TypeSpec.Builder builder = CodeGenerator.buildClass("SampleSql");
        FieldSpec fieldSpec;
        MethodSpec.Builder getMethodSpec;
        MethodSpec.Builder setMethodSpec;
        List<FieldSpec> fieldSpecList = new ArrayList<>();
        List<MethodSpec> methodSpecList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String typeName = resultSetMetaData.getColumnTypeName(i);
            String columnName = resultSetMetaData.getColumnName(i);
            System.out.println(resultSetMetaData.getColumnLabel(i));
            System.out.println(typeName + " : " + columnName);
            MySqlDataTypeEnum typeEnum = MySqlDataTypeEnum.valueOf(typeName);
            columnName = columnName.toLowerCase();
            if ((columnName.charAt(0) >= 'A' && columnName.charAt(0) <= 'Z') || columnName.contains("_")) {
                columnName = CodeStringUtils.underlineToCamelhump(columnName);
            }
            getMethodSpec = CodeGenerator.buildGetMethodSpec(columnName, typeEnum.getJavaClass());
            methodSpecList.add(getMethodSpec.build());
            setMethodSpec = CodeGenerator.buildSetMethodSpec(columnName, typeEnum.getJavaClass());
            methodSpecList.add(setMethodSpec.build());
            fieldSpec = FieldSpec.builder(typeEnum.getJavaClass(), columnName, Modifier.PRIVATE).build();
            fieldSpecList.add(fieldSpec);
            System.out.println(columnName + " : " + typeName);
        }

        fieldSpecList.forEach(builder::addField);
        methodSpecList.forEach(builder::addMethod);
        JavaFile javaFile = JavaFile.builder("com.github", builder.build()).build();
        System.out.println(javaFile.toString());

        rs.close();
        conn.close();
    }
}
