package com.github.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class CodeGenerator {

    public static TypeSpec.Builder buildClass(String className){
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC);
        return typeSpec;
    }

    public static MethodSpec buildGetMethodSpec(String columnName,Class cls) {
        String methodName = createSetGetMethodName("get",columnName);
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(cls)
                .addStatement("return " + columnName)
                .build();

        return methodSpec;
    }

    public static MethodSpec buildSetMethodSpec(String columnName,Class cls){
        String methodName = createSetGetMethodName("set",columnName);
        MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(cls,columnName)
                .addStatement("this." + columnName + " = " + columnName)
                .returns(void.class)
                .build();
        return methodSpec;
    }

    private static String createSetGetMethodName(String methodPre, String columnName) {
        String methodName = methodPre + Character.toUpperCase(columnName.charAt(0)) + columnName.substring(1,columnName.length());

        return methodName;
    }

    public static void main(String[] args) {
        TypeSpec.Builder builder = buildClass("Sample");
        builder.addField(Integer.class,"sample",Modifier.PRIVATE);

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

        javaFile = JavaFile.builder("com.github",builder.build()).build();
        System.out.println(javaFile.toString());
    }
}
