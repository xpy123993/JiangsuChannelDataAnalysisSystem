/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlsupport;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import net.sf.json.*;

/**
 *
 * @author xpy12
 */
public class DataUtils {

    
    
    public static String resultSetToJson(ResultSet rs) {
        try {
            JSONArray array = new JSONArray(); 
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObj.put(columnName, value);
                }
                array.add(jsonObj);
            }

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}
