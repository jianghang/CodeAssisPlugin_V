package com.github.generator;

import com.alibaba.fastjson.annotation.JSONField;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class AnnotationGenerator {

    public static AnnotationSpec buildJSONField(String name, String parameter){
        AnnotationSpec annotationSpec = AnnotationSpec.builder(JSONField.class)
                .addMember(name,"$S",parameter).build();

        return annotationSpec;
    }

    public static AnnotationSpec buildXmlRootElement(String name,String parameter){
        AnnotationSpec annotationSpec = AnnotationSpec.builder(XmlRootElement.class)
                .addMember(name,"$S",parameter)
                .build();
        return annotationSpec;
    }

    public static AnnotationSpec buildXmlAccessorType(String value){
        ClassName className = ClassName.get(XmlAccessType.class);
        AnnotationSpec annotationSpec = AnnotationSpec.builder(XmlAccessorType.class)
                .addMember("value","$T.$L",className,value)
                .build();

        return annotationSpec;
    }

    public static AnnotationSpec buildXmlElement(String name,String parameter){
        AnnotationSpec annotationSpec = AnnotationSpec.builder(XmlElement.class)
                .addMember(name,"$S",parameter)
                .build();
        return annotationSpec;
    }

    public static void main(String[] args) {

    }
}
