package com.github.enums;

import com.github.generator.AnnotationGenerator;
import com.squareup.javapoet.AnnotationSpec;

public enum AnnotationEnum {
    JSON("Json"){

        @Override
        public AnnotationSpec buildAnnotation(String name, String parameter) {
            AnnotationSpec annotationSpec = AnnotationGenerator.buildJSONField(name,parameter);

            return annotationSpec;
        }
    },

    XML("Xml"){

        @Override
        public AnnotationSpec buildAnnotation(String name, String parameter) {
            return null;
        }
    };

    private String type;

    AnnotationEnum(String type){
        this.type = type;
    }

    public abstract AnnotationSpec buildAnnotation(String name,String parameter);

    public static AnnotationEnum getAnnotaionType(String type){
        for(AnnotationEnum generatorEnum : AnnotationEnum.values()){
            if(generatorEnum.type.equals(type)){
                return generatorEnum;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }
}
