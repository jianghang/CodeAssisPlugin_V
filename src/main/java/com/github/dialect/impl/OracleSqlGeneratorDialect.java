package com.github.dialect.impl;

import com.github.dialect.ISqlGeneratorDialect;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OracleSqlGeneratorDialect implements ISqlGeneratorDialect {

    @Override
    public String buildInsertSql() {
        return null;
    }

    public static void main(String[] args) {
        ST st = new ST("Hello,$value$\n",'$','$');
        st.add("value","world!");
        System.out.println(st.render());
        String s = "<resultMap id=\"BaseResultMap\" type=\"$type$\">$resultTemplate(result)$\n</resultMap>";

        String filePath = OracleSqlGeneratorDialect.class.getClass().getResource("/st").getPath();
        System.out.println(filePath);
        STGroup stGroup = new STGroupFile(filePath + "/test.stg",'$','$');
        ST decl = stGroup.getInstanceOf("decl");
        decl.add("name","yyy");
        decl.add("type","uuu");
        System.out.println(decl.render());

        stGroup = new STGroupFile(filePath + "/mybatis-template.stg",'$','$');
        ST resultMapSt = stGroup.getInstanceOf("resultMapTemplate");
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("column","RESOURCE_ID");
        resultMap.put("property","resourceId");
        resultMap.put("jdbcType","VARCHAR");
        Map<String,String> resultMap1 = new HashMap<>();
        resultMap1.put("column","RESOURCE_ID");
        resultMap1.put("property","resourceId");
        resultMap1.put("jdbcType","VARCHAR");
        List<Map<String,String>> resultList = new ArrayList<>();
        resultList.add(resultMap);
        resultList.add(resultMap1);

        resultMapSt.add("result",resultList);
        resultMapSt.add("type","com.sunsharing.xshare.api.check.config.entity.CheckConfigBaseEntity");

        String result = resultMapSt.render();
        System.out.println(result);


    }
}
