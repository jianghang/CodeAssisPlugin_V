package com.github.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.generator.CodeGenerator;
import com.github.utils.StringUtils;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    private static DruidDataSource dataSource = new DruidDataSource();

    public static Connection getConnection(String url,String username,String password) throws ClassNotFoundException, SQLException {
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        Connection connection = dataSource.getConnection();

        return connection;
    }

    public static ResultSetMetaData getResultSetMetaData(Connection conn,String sql) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet.getMetaData();
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String URL = "jdbc:mysql://localhost:3306/myblog?useUnicode=true&characterEncoding=utf8";
        String USER = "root";
        String PASSWORD = "admin";

//        URL = "jdbc:oracle:thin:@192.168.0.58:1521:CXDATABASE";
//        USER = "cobweb3";
//        PASSWORD = "admin";
        //1.加载驱动程序
        Class.forName(DatabaseTypeEnum.MYSQL.getDatabaseDriver());
        //2.获得数据库链接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        //3.通过数据库的连接操作数据库，实现增删改查（使用Statement类）
        Statement st = conn.createStatement();
        String sql = "select * from sys_user";
//        sql = "select * from t_fwzy_ssl";
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        int count = resultSetMetaData.getColumnCount();
        TypeSpec.Builder builder = CodeGenerator.buildClass("SampleSql");
        FieldSpec fieldSpec;
        MethodSpec getMethodSpec;
        MethodSpec setMethodSpec;
        List<FieldSpec> fieldSpecList = new ArrayList<>();
        List<MethodSpec> methodSpecList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String typeName = resultSetMetaData.getColumnTypeName(i);
            String columnName = resultSetMetaData.getColumnName(i);
            System.out.println(typeName + " : " + columnName);
            MySqlDataTypeEnum typeEnum = MySqlDataTypeEnum.valueOf(typeName);
            columnName = columnName.toLowerCase();
            if((columnName.charAt(0) >= 'A' && columnName.charAt(0) <= 'Z') || columnName.contains("_")){
                columnName = StringUtils.underlineToCamelhump(columnName);
            }
            getMethodSpec = CodeGenerator.buildGetMethodSpec(columnName,typeEnum.getJavaClass());
            methodSpecList.add(getMethodSpec);
            setMethodSpec = CodeGenerator.buildSetMethodSpec(columnName,typeEnum.getJavaClass());
            methodSpecList.add(setMethodSpec);
            fieldSpec = FieldSpec.builder(typeEnum.getJavaClass(),columnName,Modifier.PRIVATE).build();
            fieldSpecList.add(fieldSpec);
            System.out.println(columnName + " : " + typeName);
        }

        fieldSpecList.forEach(builder::addField);
        methodSpecList.forEach(builder::addMethod);
        JavaFile javaFile = JavaFile.builder("com.github",builder.build()).build();
        System.out.println(javaFile.toString());

        rs.close();
        conn.close();
    }
}
