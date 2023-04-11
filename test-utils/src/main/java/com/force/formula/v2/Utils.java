package com.force.formula.v2;

import com.force.formula.MockFormulaDataType;
import com.force.formula.commands.FormulaJsTestUtils;
import com.force.formula.util.FormulaTextUtil;
import com.force.formula.v2.data.FormulaFieldDefinition;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public class Utils {

    public static Date getDateObject(String dateString, Boolean isDateOnly) {
        Calendar myCal = Calendar.getInstance();
        myCal.clear();
        if (dateString == null || dateString.length() == 0) return myCal.getTime();

        StringTokenizer stDate = new StringTokenizer(dateString, ":");
        int year = stDate.hasMoreTokens() ? Integer.parseInt(stDate.nextToken()) : 2004;
        int month = stDate.hasMoreTokens() ? Integer.parseInt(stDate.nextToken()) - 1 : 0;
        int dayOfMonth = stDate.hasMoreTokens() ? Integer.parseInt(stDate.nextToken()) : 1;
        int hourOfDay = stDate.hasMoreTokens() ? Integer.parseInt(stDate.nextToken()) : 0;
        int minutes = stDate.hasMoreTokens() ? Integer.parseInt(stDate.nextToken()) : 0;
        int seconds = stDate.hasMoreTokens() ? Integer.parseInt(stDate.nextToken()) : 0;
        TimeZone timeZone = stDate.hasMoreTokens() ? TimeZone.getTimeZone(stDate.nextToken()) : null;
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        myCal.setTimeZone(timeZone);
        if (isDateOnly) {
            // remove the time part:
            myCal.set(year, month, dayOfMonth, 0, 0, 0);
        } else {
            myCal.set(year, month, dayOfMonth, hourOfDay, minutes, seconds);
        }
        return myCal.getTime();
    }

    public static List<FormulaFieldDefinition> flattenFieldList(List<FormulaFieldDefinition> nestedFields) {
        List<FormulaFieldDefinition> flattenedList = new LinkedList<>();
        if(nestedFields!=null && !nestedFields.isEmpty()){
            for (FormulaFieldDefinition field : nestedFields) {
                if (field.getReferenceFields()!=null && !field.getReferenceFields().isEmpty())
                    flattenedList.addAll(flattenFieldList(field.getReferenceFields()));
                flattenedList.add(field);
            }
        }
        return flattenedList;
    }

    public static MockFormulaDataType getDataType(String dataType){
        if (ImmutableSet.of("email", "url", "phone", "textarea").contains(dataType)) return MockFormulaDataType.TEXT;
        if ("number".equals(dataType)) return MockFormulaDataType.DOUBLE;
        if ("currency".equals(dataType)) return MockFormulaDataType.CURRENCY;
        if ("percent".equals(dataType)) return MockFormulaDataType.PERCENT;
        MockFormulaDataType formulaDataType = MockFormulaDataType.fromCamelCaseName(dataType);
        if (formulaDataType == null) {
                throw new IllegalArgumentException("Given dataType is not supported: " + dataType);
            }
        return formulaDataType;
    }

    public static Map<String,Object> createJSMapFromTestInput(Map<String, Object> testInput){
        Map<String,Object> record = testInput != null ? new HashMap<String,Object>(testInput) : new HashMap<String,Object>();
        Map<String,Object> jsMap = new HashMap<>();
        jsMap.put("record", FormulaJsTestUtils.get().makeJSMap(record));
        return jsMap;
    }

    public static String getSQLOutput(String rawSql, String sqlGuard, boolean nullAsNull){
        StringBuffer output = new StringBuffer();
        output.append("    <SqlOutput nullAsNull=\""+nullAsNull+"\">\n");
        output.append("       <Sql>");
        output.append(FormulaTextUtil.escapeToXml(rawSql));
        output.append("</Sql>\n");
        output.append("       <Guard>");
        output.append(FormulaTextUtil.escapeToXml(sqlGuard));
        output.append("</Guard>\n");
        output.append("    </SqlOutput>\n");
        return output.toString();
    }

    public static String getJavascriptOutput(String jsCode, boolean highPrec, boolean nullAsNull){
        StringBuffer output = new StringBuffer();
        output.append("    <JsOutput highPrec=\""+highPrec+"\" nullAsNull=\""+nullAsNull+"\">");
        output.append(FormulaTextUtil.escapeToXml(jsCode));
        output.append("</JsOutput>\n");
        return output.toString();
    }

}
