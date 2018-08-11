package com.github.db;

import com.github.generator.CodeGenerator;
import com.github.utils.CodeStringUtils;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    public static Connection getConnection(String projectName, String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static ResultSetMetaData getResultSetMetaData(Connection conn, String sql) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet.getMetaData();
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
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
