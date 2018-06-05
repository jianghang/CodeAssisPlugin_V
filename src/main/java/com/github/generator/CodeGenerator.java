package com.github.generator;

import com.alibaba.fastjson.annotation.JSONField;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class CodeGenerator {

    public static TypeSpec.Builder buildClass(String className){
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC);
        return typeSpec;
    }

    public static MethodSpec.Builder buildGetMethodSpec(String columnName,Class cls) {
        String methodName = createSetGetMethodName("get",columnName);
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(cls)
                .addStatement("return " + columnName);

        return methodSpec;
    }

    public static MethodSpec.Builder buildSetMethodSpec(String columnName,Class cls){
        String methodName = createSetGetMethodName("set",columnName);
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(cls,columnName)
                .addStatement("this." + columnName + " = " + columnName)
                .returns(void.class);
        return methodSpec;
    }

    private static String createSetGetMethodName(String methodPre, String columnName) {
        String methodName = methodPre + Character.toUpperCase(columnName.charAt(0)) + columnName.substring(1,columnName.length());

        return methodName;
    }


    public static void main(String[] args) {
        TypeSpec.Builder builder = buildClass("Sample");
        builder.addField(Integer.class,"sample",Modifier.PRIVATE);

        AnnotationSpec annotationSpec = AnnotationSpec.builder(JSONField.class).addMember("name","$S","yu").build();
        FieldSpec fieldSpec = FieldSpec.builder(Integer.class,"yyy",Modifier.PRIVATE).addAnnotation(annotationSpec).build();
        builder.addField(fieldSpec);

        AnnotationSpec annotationSpec1 = AnnotationSpec.builder(XmlRootElement.class)
                .addMember("name","$S","sample")
                .build();
//        ClassName className = ClassName.get("javax.xml.bind.annotation.XmlAccessType","XmlAccessType");
        ClassName className = ClassName.get(XmlAccessType.class);
        AnnotationSpec annotationSpec2 = AnnotationSpec.builder(XmlAccessorType.class)
//                .addMember("value","$L",XmlAccessType.FIELD)
                .addMember("value","$T.$L",className,"FIELD")
                .build();
        builder.addAnnotation(annotationSpec1);
        builder.addAnnotation(annotationSpec2);

        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class,"args")
                .addStatement("$T.out.println($S)",System.class,"Hello, JavaPoet!")
                .build();
        TypeSpec typeSpec = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder("com.github",typeSpec).build();
        System.out.println(javaFile.toString());

        javaFile = JavaFile.builder("com.github",builder.build()).skipJavaLangImports(true).build();
        System.out.println(javaFile.toString());
    }
}
