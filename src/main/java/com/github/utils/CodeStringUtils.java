package com.github.utils;

public class CodeStringUtils {

    public static String underlineToCamelhump(String columnName) {
        StringBuilder builder = new StringBuilder();

        boolean nextUpperCase = false;
        for(int i = 0;i < columnName.length();i++){
            char c = columnName.charAt(i);
            if(c == '_'){
                if(builder.length() > 0){
                    nextUpperCase = true;
                }
            }else {
                if(nextUpperCase){
                    builder.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                }else {
                    builder.append(Character.toLowerCase(c));
                }
            }
        }

        return builder.toString();
    }

    public static boolean checkStringsEmpty(String... str){
        for(String s : str){
            if(org.apache.commons.lang.StringUtils.isEmpty(s)){
                return true;
            }
        }

        return false;
    }
}
