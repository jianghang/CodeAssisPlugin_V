package com.github.generator;

import com.alibaba.fastjson.annotation.JSONField;
import com.squareup.javapoet.AnnotationSpec;

public class JsonAnnotationGenerator {

    public static AnnotationSpec buildJsonAnnotation(String name,String parameter){
        AnnotationSpec annotationSpec = AnnotationSpec.builder(JSONField.class)
                .addMember(name,"$S",parameter).build();

        return annotationSpec;
    }

    public static void main(String[] args) {

    }
}
