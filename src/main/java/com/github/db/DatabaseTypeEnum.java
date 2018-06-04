package com.github.db;

import com.github.enums.AnnotationEnum;
import com.github.generator.CodeGenerator;
import com.github.param.GlobalParameter;
import com.github.utils.CodeStringUtils;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public enum DatabaseTypeEnum {
    ORACLE("Oracle","oracle.jdbc.OracleDriver",0){

        @Override
        public String buildJavaCode(ResultSetMetaData metaData,String className,String packageName,String annotationType) throws SQLException {
            String typeName;
            String columnName;
            OralceDataTypeEunm dataTypeEnum;
            TypeSpec.Builder builder = CodeGenerator.buildClass(className);
            FieldSpec fieldSpec;
            MethodSpec.Builder getMethodSpec;
            MethodSpec.Builder setMethodSpec;
            AnnotationSpec annotationSpec;
            List<FieldSpec> fieldSpecList = new ArrayList<>();
            List<MethodSpec> methodSpecList = new ArrayList<>();
            for(int i = 1;i <= metaData.getColumnCount();i++){
                typeName = metaData.getColumnTypeName(i);
                columnName = metaData.getColumnName(i);
                columnName = columnName.toLowerCase();
                if((columnName.charAt(0) >= 'A' && columnName.charAt(0) <= 'Z') || columnName.contains("_")){
                    columnName = CodeStringUtils.underlineToCamelhump(columnName);
                }
                dataTypeEnum = OralceDataTypeEunm.valueOf(typeName);
                getMethodSpec = CodeGenerator.buildGetMethodSpec(columnName,dataTypeEnum.getJavaClass());
                setMethodSpec = CodeGenerator.buildSetMethodSpec(columnName,dataTypeEnum.getJavaClass());
                fieldSpec = FieldSpec.builder(dataTypeEnum.getJavaClass(),columnName,Modifier.PRIVATE).build();
                if(AnnotationEnum.JSON.getType().equals(annotationType)){
                    annotationSpec = AnnotationEnum.JSON.buildAnnotation(GlobalParameter.JSON_FIELD_NAME,columnName);
                    getMethodSpec.addAnnotation(annotationSpec);
                    setMethodSpec.addAnnotation(annotationSpec);
                }
                methodSpecList.add(getMethodSpec.build());
                methodSpecList.add(setMethodSpec.build());
                fieldSpecList.add(fieldSpec);
            }

            fieldSpecList.forEach(builder::addField);
            methodSpecList.forEach(builder::addMethod);
            JavaFile javaFile = JavaFile.builder(packageName,builder.build()).build();

            return javaFile.toString();
        }
    },
    MYSQL("MySQL","com.mysql.jdbc.Driver",1) {

        @Override
        public String buildJavaCode(ResultSetMetaData metaData,String className,String packageName,String annotationType) throws SQLException {
            String typeName;
            String columnName;
            MySqlDataTypeEnum dataTypeEnum;
            TypeSpec.Builder builder = CodeGenerator.buildClass(className);
            FieldSpec fieldSpec;
            MethodSpec.Builder getMethodSpec;
            MethodSpec.Builder setMethodSpec;
            AnnotationSpec annotationSpec;
            List<FieldSpec> fieldSpecList = new ArrayList<>();
            List<MethodSpec> methodSpecList = new ArrayList<>();
            for(int i = 1;i <= metaData.getColumnCount();i++){
                typeName = metaData.getColumnTypeName(i);
                columnName = metaData.getColumnName(i);
                columnName = columnName.toLowerCase();
                if((columnName.charAt(0) >= 'A' && columnName.charAt(0) <= 'Z') || columnName.contains("_")){
                    columnName = CodeStringUtils.underlineToCamelhump(columnName);
                }
                dataTypeEnum = MySqlDataTypeEnum.valueOf(typeName);
                getMethodSpec = CodeGenerator.buildGetMethodSpec(columnName,dataTypeEnum.getJavaClass());
                setMethodSpec = CodeGenerator.buildSetMethodSpec(columnName,dataTypeEnum.getJavaClass());
                fieldSpec = FieldSpec.builder(dataTypeEnum.getJavaClass(),columnName,Modifier.PRIVATE).build();
                if(AnnotationEnum.JSON.getType().equals(annotationType)){
                    annotationSpec = AnnotationEnum.JSON.buildAnnotation(GlobalParameter.JSON_FIELD_NAME,columnName);
                    getMethodSpec.addAnnotation(annotationSpec);
                    setMethodSpec.addAnnotation(annotationSpec);
                }
                methodSpecList.add(getMethodSpec.build());
                methodSpecList.add(setMethodSpec.build());
                fieldSpecList.add(fieldSpec);
            }

            fieldSpecList.forEach(builder::addField);
            methodSpecList.forEach(builder::addMethod);
            JavaFile javaFile = JavaFile.builder(packageName,builder.build()).build();

            return javaFile.toString();
        }
    };

    private String dataType;
    private String databaseDriver;
    private Integer index;

    public abstract String buildJavaCode(ResultSetMetaData metaData,String className,String packageName,String annotationType) throws SQLException;

    DatabaseTypeEnum(String dataType,String databaseDriver,Integer index){
        this.dataType = dataType;
        this.databaseDriver = databaseDriver;
        this.index = index;
    }

    public static String getDriverByType(String dataType){
        for(DatabaseTypeEnum typeEnum : DatabaseTypeEnum.values()){
            if(typeEnum.dataType.equals(dataType)){
                return typeEnum.databaseDriver;
            }
        }

        return null;
    }

    public static Integer getIndexByType(String dataType){
        for(DatabaseTypeEnum typeEnum : DatabaseTypeEnum.values()){
            if(typeEnum.dataType.equals(dataType)){
                return typeEnum.index;
            }
        }
        return null;
    }

    public static DatabaseTypeEnum getDatabaseTypeEnumByType(String dataType){
        for(DatabaseTypeEnum typeEnum : DatabaseTypeEnum.values()){
            if(typeEnum.dataType.equals(dataType)){
                return typeEnum;
            }
        }
        return null;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public Integer getIndex() {
        return index;
    }
}
