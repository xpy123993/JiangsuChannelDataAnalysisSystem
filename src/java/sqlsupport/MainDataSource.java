/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author xpy12
 */
@WebServlet(name = "queryServlet", urlPatterns = {"/query"})
public class MainDataSource extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private final OracleImpl conn = new OracleImpl();

    public MainDataSource() {
        try {
            conn.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResultSet execute_query(String sql) {
        try {
            ResultSet ret = conn.excuteQuery(sql);
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    private String getTableTimeColumn(String tableName) throws SQLException {
        String sql_date = "SELECT distinct CSCHEDULEDATE FROM " + tableName + " ORDER BY CSCHEDULEDATE";
        ResultSet rs = execute_query(sql_date);
        String tag = "";
        while (rs.next()) {
            if (tag.length() > 0) {
                tag += ", ";
            }
            tag += "'" + rs.getString(1) + "'";
        }
        return tag;
    }

    /*
    private String getTable1Info() throws SQLException {
        String tag = getTableTimeColumn("S_ZCRQ");
        String sql_query = "SELECT * FROM (SELECT * FROM S_ZCRQ)\n"
                + "PIVOT"
                + "(\n"
                + "SUM(OPENINGTIMES) AS OPENINGTIMES, SUM(OPENINGDAYS) AS OPENINGDAYS\n"
                + "FOR CSCHEDULEDATE IN (" + tag + ")\n"
                + ") order by addr1, addr2, portname";
        return DataUtils.resultSetToJson(execute_query(sql_query));
    }

    private String getTable2Info() throws SQLException {
        String tag = getTableTimeColumn("S_TFFX");
        String sql_query = "SELECT * FROM (SELECT * FROM S_TFFX)\n"
                + "PIVOT"
                + "(\n"
                + "SUM(SHIPCOUNT) AS SHIPCOUNT, SUM(SHIPWEIGHT) AS SHIPWEIGHT\n"
                + "FOR CSCHEDULEDATE IN (" + tag + ")\n"
                + ") order by addr1, addr2, portname";
        return DataUtils.resultSetToJson(execute_query(sql_query));
    }

    private String getTable3Info() throws SQLException {
        String tag = getTableTimeColumn("S_PJGZSJ");
        String sql_query = "SELECT * FROM (SELECT * FROM S_PJGZSJ)\n"
                + "PIVOT"
                + "(\n"
                + "SUM(AVGSCHEDULETIME) AS AVGSCHEDULETIME\n"
                + "FOR CSCHEDULEDATE IN (" + tag + ")\n"
                + ") order by addr1, addr2, portname, direction";
        return DataUtils.resultSetToJson(execute_query(sql_query));
    }
     */
    private String getTableDetails(String addr1, String addr2, String portName, int request_type, String cscheduledate, String direction) {
        int time_label = request_type / 3;
        String build_block = "";
        switch (time_label) {
            case 0:
                // monthly
                build_block = "TO_CHAR(SCHEDULEDATE, 'YYYY-MM') AS CSCHEDULEDATE\n";
                break;
            case 1:
                build_block = "(TO_CHAR(SCHEDULEDATE, 'YYYY') || '-0' ||TO_CHAR(SCHEDULEDATE, 'Q')) AS CSCHEDULEDATE\n";
                break;
            case 2:
                build_block = "(TO_CHAR(SCHEDULEDATE, 'YYYY') || '-' || (CASE \n"
                        + "WHEN TO_CHAR(SCHEDULEDATE, 'MM') IN ('01', '02', '03', '04', '05', '06') THEN '上半年'\n"
                        + "WHEN TO_CHAR(SCHEDULEDATE, 'MM') IN ('07', '08', '09', '10', '11', '12') THEN '下半年'\n"
                        + "END )) as CSCHEDULEDATE\n";
                break;
            case 3:
                build_block = "(TO_CHAR(SCHEDULEDATE, 'YYYY') || '全年') as CSCHEDULEDATE\n";
                break;
        }
        String sql_statement = "with MYTABLE AS (select \n"
                + build_block
                + ", ADDR1, ADDR2, PORTNAME, SHIPID, SHIPTYPE, SHIPCOUNT, TOTALWEIGHT, DIRECTION, REGDATE, SCHEDULEDATE, SCHEDULETIME\n"
                + "from s_fact)\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, SHIPID, SHIPTYPE, SHIPCOUNT, "
                + "TOTALWEIGHT, DIRECTION, to_char(REGDATE, 'YYYY-MM-DD') as REGDATE, to_char(SCHEDULEDATE, 'YYYY-MM-DD') AS SCHEDULEDATE, to_char((SCHEDULETIME / 3600000.0), '99999.99') as PASSTIME FROM MYTABLE "
                + "WHERE CSCHEDULEDATE like N'" + cscheduledate + "'\n"
                + "AND ADDR1 like N'" + addr1 + "'\n"
                + "AND ADDR2 like N'" + addr2 + "'\n"
                + "AND PORTNAME like N'" + portName + "'";
        if(direction != null)
            sql_statement += "\nAND DIRECTION like N'%" + (direction.startsWith("上行")?"U":"D" ) + "%'";
        String text = DataUtils.resultSetToJson(execute_query(sql_statement));
        return text;
    }

    private String getTableInfo_Year(int requestType, String start_cschedule, String end_cschedule) {
        String build_hfy_block = "(YEAR || '全年') as CSCHEDULEDATE\n";
        String condition_block = "";
        
        if(start_cschedule != null && start_cschedule.length() > 0){
            condition_block = " WHERE YEAR BETWEEN " + start_cschedule + " AND " + end_cschedule + " ";
        }
        
        String sql_statement_0 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, OPENINGTIMES, OPENINGDAYS, \n"
                + build_hfy_block
                + "FROM S_ZCRQ\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "SUM(OPENINGTIMES) AS OPENINGTIMES,\n"
                + "SUM(OPENINGDAYS) AS OPENINGDAYS FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String sql_statement_1 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, SHIPCOUNT, SHIPWEIGHT, \n"
                + build_hfy_block
                + "FROM S_TFFX\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "SUM(SHIPCOUNT) AS SHIPCOUNT,\n"
                + "to_char(SUM(SHIPWEIGHT), '99999.99') AS SHIPWEIGHT FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String sql_statement_2 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, (CASE WHEN DIRECTION = 'U' THEN '上行' ELSE '下行' END) as DIRECTION, (AVGSCHEDULETIME / 3600000.0) as AVGS, \n"
                + build_hfy_block
                + "FROM S_PJGZSJ\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "DIRECTION,\n"
                + "to_char(AVG(AVGS), '99999.99') AS AVGS FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION";

        switch (requestType) {
            case 9:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 10:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 11:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return "[]";
        }
    }

    private String getTableInfo_HalfYear(int requestType, String start_cschedule, String end_cschedule) {
        String build_hfy_block = "(YEAR || '-' || (CASE \n"
                + "WHEN MONTH IN ('01', '02', '03', '04', '05', '06') THEN '上半年'\n"
                + "WHEN MONTH IN ('07', '08', '09', '10', '11', '12') THEN '下半年'\n"
                + "END )) as CSCHEDULEDATE\n";
        String condition_block = "";
        
        if(start_cschedule != null && start_cschedule.length() > 0){
            condition_block = " WHERE YEAR BETWEEN " + start_cschedule + " AND " + end_cschedule + " ";
        }
        
        String sql_statement_0 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, OPENINGTIMES, OPENINGDAYS, \n"
                + build_hfy_block
                + "FROM S_ZCRQ\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "SUM(OPENINGTIMES) AS OPENINGTIMES,\n"
                + "SUM(OPENINGDAYS) AS OPENINGDAYS FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String sql_statement_1 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, SHIPCOUNT, SHIPWEIGHT, \n"
                + build_hfy_block
                + "FROM S_TFFX\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "SUM(SHIPCOUNT) AS SHIPCOUNT,\n"
                + "to_char(SUM(SHIPWEIGHT), '99999.99') AS SHIPWEIGHT FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String sql_statement_2 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, (CASE WHEN DIRECTION = 'U' THEN '上行' ELSE '下行' END) as DIRECTION, (AVGSCHEDULETIME / 3600000.0) as AVGS, \n"
                + build_hfy_block
                + "FROM S_PJGZSJ\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "DIRECTION,\n"
                + "to_char(AVG(AVGS), '99999.99') AS AVGS FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION";

        switch (requestType) {
            case 6:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 7:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 8:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return getTableInfo_Year(requestType, start_cschedule, end_cschedule);
        }
    }

    private String getTableInfo_Season(int requestType, String start_cschedule, String end_cschedule) {

        String build_season_block = "(YEAR || '-' || (CASE \n"
                + "WHEN MONTH IN ('01', '02', '03') THEN '01'\n"
                + "WHEN MONTH IN ('04', '05', '06') THEN '02'\n"
                + "WHEN MONTH IN ('07', '08', '09') THEN '03'\n"
                + "WHEN MONTH IN ('10', '11', '12') THEN '04'\n"
                + "END )) as CSCHEDULEDATE\n";
        
        String condition_block = "";
        
        if(start_cschedule != null && start_cschedule.length() > 0){
            condition_block = " WHERE YEAR BETWEEN " + start_cschedule + " AND " + end_cschedule + " ";
        }

        String sql_statement_0 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, OPENINGTIMES, OPENINGDAYS, \n"
                + build_season_block
                + "FROM S_ZCRQ\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "SUM(OPENINGTIMES) AS OPENINGTIMES,\n"
                + "SUM(OPENINGDAYS) AS OPENINGDAYS FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String sql_statement_1 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, SHIPCOUNT, SHIPWEIGHT, \n"
                + build_season_block
                + "FROM S_TFFX\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "SUM(SHIPCOUNT) AS SHIPCOUNT,\n"
                + "to_char(SUM(SHIPWEIGHT), '99999.99') AS SHIPWEIGHT FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String sql_statement_2 = "WITH MYTABLE AS \n"
                + "(\n"
                + "SELECT \n"
                + "ADDR1, ADDR2, PORTNAME, YEAR, (CASE WHEN DIRECTION = 'U' THEN '上行' ELSE '下行' END) as DIRECTION, (AVGSCHEDULETIME / 3600000.0) as AVGS, \n"
                + build_season_block
                + "FROM S_PJGZSJ\n" + condition_block
                + ")\n"
                + "SELECT ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, \n"
                + "DIRECTION,\n"
                + "to_char(AVG(AVGS), '99999.99') AS AVGS FROM MYTABLE\n"
                + "GROUP BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION\n"
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION";

        switch (requestType) {
            case 3:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 4:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 5:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return getTableInfo_HalfYear(requestType, start_cschedule, end_cschedule);
        }

    }

    private String getTableInfo(int requestType, String start_cschedule, String end_cschedule) {

        String condition_block = "";
        
        if(start_cschedule != null && start_cschedule.length() > 0){
            condition_block = " WHERE YEAR BETWEEN " + start_cschedule + " AND " + end_cschedule + " ";
        }
        
        System.err.println(start_cschedule);
        
        String query_table_type_0 = "SELECT ADDR1, ADDR2, portname，"
                + "CSCHEDULEDATE, OPENINGTIMES，"
                + "OPENINGDAYS FROM S_ZCRQ " + condition_block
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String query_table_type_1 = "SELECT ADDR1, ADDR2, portname,"
                + "CSCHEDULEDATE, SHIPCOUNT,"
                + "to_char(SHIPWEIGHT, '99999.99') as SHIPWEIGHT FROM S_TFFX " + condition_block
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE";

        String query_table_type_2 = "SELECT ADDR1, ADDR2, PORTNAME,"
                + "CSCHEDULEDATE, (CASE WHEN DIRECTION = 'U' THEN '上行' ELSE '下行' END) as DIRECTION,"
                + "to_char((AVGSCHEDULETIME / 3600000.0), '99999.99') as AVGS FROM S_PJGZSJ " + condition_block
                + "ORDER BY ADDR1, ADDR2, PORTNAME, CSCHEDULEDATE, DIRECTION";

        switch (requestType) {
            case 0:
                return DataUtils.resultSetToJson(execute_query(query_table_type_0));
            case 1:
                return DataUtils.resultSetToJson(execute_query(query_table_type_1));
            case 2:
                return DataUtils.resultSetToJson(execute_query(query_table_type_2));
            default:
                return getTableInfo_Season(requestType, start_cschedule, end_cschedule);
        }
    }

    private String getLineInfo_Year(int requestType, String addr1, String addr2, String portName) {
        String time_block = "(YEAR || '全年' )";

        String sql_statement_0 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "OPENINGTIMES as Y1, OPENINGDAYS as Y2 FROM S_ZCRQ "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' "
                + "ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 24 GROUP BY LABEL ORDER BY LABEL";

        String sql_statement_1 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "SHIPCOUNT as Y1, SHIPWEIGHT as Y2 FROM S_TFFX "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' "
                + "ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 24 GROUP BY LABEL ORDER BY LABEL";

        String sql_statement_2 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "DIRECTION, (AVGSCHEDULETIME / 3600000.0) as Y_DATA FROM S_PJGZSJ "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, DIRECTION, AVG(Y_DATA) AS Y_DATA FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 48 GROUP BY LABEL, DIRECTION ORDER BY LABEL, DIRECTION";

        switch (requestType) {
            case 9:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 10:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 11:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return "[]";
        }
    }

    private String getLineInfo_HalfYear(int requestType, String addr1, String addr2, String portName) {
        String time_block = "(YEAR || '-' || (CASE \n"
                + "WHEN MONTH IN ('01', '02', '03', '04', '05', '06') THEN '上半年'\n"
                + "WHEN MONTH IN ('07', '08', '09', '10', '11', '12') THEN '下半年'\n"
                + "END ))";

        String sql_statement_0 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "OPENINGTIMES as Y1, OPENINGDAYS as Y2 FROM S_ZCRQ "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' "
                + "ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 24 GROUP BY LABEL ORDER BY LABEL";

        String sql_statement_1 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "SHIPCOUNT as Y1, SHIPWEIGHT as Y2 FROM S_TFFX "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' "
                + "ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 24 GROUP BY LABEL ORDER BY LABEL";

        String sql_statement_2 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "DIRECTION, (AVGSCHEDULETIME / 3600000.0) as Y_DATA FROM S_PJGZSJ "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, DIRECTION, AVG(Y_DATA) AS Y_DATA FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 48 GROUP BY LABEL, DIRECTION ORDER BY LABEL, DIRECTION";

        switch (requestType) {
            case 6:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 7:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 8:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return getLineInfo_Year(requestType, addr1, addr2, portName);
        }
    }

    private String getLineInfo_Season(int requestType, String addr1, String addr2, String portName) {

        String time_block = "(YEAR || '-' || (CASE \n"
                + "WHEN MONTH IN ('01', '02', '03') THEN '01'\n"
                + "WHEN MONTH IN ('04', '05', '06') THEN '02'\n"
                + "WHEN MONTH IN ('07', '08', '09') THEN '03'\n"
                + "WHEN MONTH IN ('10', '11', '12') THEN '04'\n"
                + "END ))";

        String sql_statement_0 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "OPENINGTIMES as Y1, OPENINGDAYS as Y2 FROM S_ZCRQ "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' "
                + "ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 24 GROUP BY LABEL ORDER BY LABEL";

        String sql_statement_1 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "SHIPCOUNT as Y1, SHIPWEIGHT as Y2 FROM S_TFFX "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' "
                + "ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 24 GROUP BY LABEL ORDER BY LABEL";

        String sql_statement_2 = "WITH CHARTSOURCE as (SELECT " + time_block + " as LABEL, "
                + "DIRECTION, (AVGSCHEDULETIME / 3600000.0) as Y_DATA FROM S_PJGZSJ "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' "
                + "AND PORTNAME like N'" + portName + "' ORDER BY CSCHEDULEDATE DESC) \n"
                + "SELECT LABEL, DIRECTION, AVG(Y_DATA) AS Y_DATA FROM CHARTSOURCE "
                + "WHERE ROWNUM <= 48 GROUP BY LABEL, DIRECTION ORDER BY LABEL, DIRECTION";

        switch (requestType) {
            case 3:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 4:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 5:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return getLineInfo_HalfYear(requestType, addr1, addr2, portName);
        }
    }

    private String getLineInfo_2(int requestType, String addr1, String addr2, String portName, String shipType) {

        String labelStr = "";

        switch (requestType) {
            case 0:
                labelStr = "TO_CHAR(SCHEDULEDATE, 'YYYY-MM') AS LABEL, ";
                break;
            case 1:
                labelStr = "TO_CHAR(SCHEDULEDATE, 'YYYY-Q') AS LABEL, ";
                break;
            case 2:
                labelStr = "(TO_CHAR(SCHEDULEDATE, 'YYYY') || '-' || "
                        + "(case when to_number(TO_CHAR(SCHEDULEDATE, 'MM'), '99') < 7 then '上半年' else '下半年' end)) AS LABEL, ";
                break;
            case 3:
                labelStr = "(TO_CHAR(SCHEDULEDATE, 'YYYY') || '全年') AS LABEL, ";
                break;
        }

        String sql = "WITH SOURCE AS (SELECT "
                + labelStr
                + "(SHIPCOUNT) AS Y1,"
                + "(TOTALWEIGHT) AS Y2 "
                + "FROM S_FACT "
                + "WHERE ADDR1 = N'" + addr1 + "' AND ADDR2 = N'" + addr2 + "' AND "
                + "PORTNAME = N'" + portName + "' "
                + (shipType.equals("所有类型") ? "" : "AND SHIPTYPE = N'" + shipType + "'")
                + ") SELECT LABEL, SUM(Y1) AS Y1, SUM(Y2) AS Y2 FROM SOURCE GROUP BY LABEL ORDER BY LABEL";

        return DataUtils.resultSetToJson(execute_query(sql));
    }

    private String getPieInfo(int requestType, String addr1, String addr2, String portName, String cscheduledate) {
        String request_sql_0 = "SELECT SUM(TOTALWEIGHT) as TYPEVALUE, SHIPTYPE as TYPENAME FROM S_FACT "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' AND "
                + "PORTNAME like N'" + portName + "' AND to_char(SCHEDULEDATE, 'yyyy-MM') like '" + cscheduledate + "' "
                + "GROUP BY SHIPTYPE ORDER BY SHIPTYPE";
        String request_sql_1 = "SELECT SUM(TOTALWEIGHT) as TYPEVALUE, SHIPTYPE as TYPENAME FROM S_FACT "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' AND "
                + "PORTNAME like N'" + portName + "' AND to_char(SCHEDULEDATE, 'yyyy-Q') like '" + cscheduledate + "' "
                + "GROUP BY SHIPTYPE ORDER BY SHIPTYPE";
        String request_sql_2 = "SELECT SUM(TOTALWEIGHT) as TYPEVALUE, SHIPTYPE as TYPENAME FROM S_FACT "
                + "WHERE ADDR1 like N'" + addr1 + "' AND ADDR2 like N'" + addr2 + "' AND "
                + "PORTNAME like N'" + portName + "' AND to_char(SCHEDULEDATE, 'yyyy') like '" + cscheduledate + "' "
                + "GROUP BY SHIPTYPE ORDER BY SHIPTYPE";

        switch (requestType) {
            case 0:
                return DataUtils.resultSetToJson(execute_query(request_sql_0));
            case 1:
                return DataUtils.resultSetToJson(execute_query(request_sql_1));
            case 2:
                return DataUtils.resultSetToJson(execute_query(request_sql_2));
            default:
                return "[]";
        }

    }

    private String getLineInfo(int requestType, String addr1, String addr2, String portName) {
        
        String sql_statement_0 = "WITH CHARTSOURCE as ("
                + "SELECT CSCHEDULEDATE as LABEL, OPENINGTIMES as Y1, OPENINGDAYS as Y2 "
                + "FROM S_ZCRQ WHERE ADDR1 like N'" + addr1 + "' AND "
                + "ADDR2 like N'" + addr2 + "' AND PORTNAME like N'" + portName + "' ORDER BY CSCHEDULEDATE DESC"
                + ") SELECT * FROM CHARTSOURCE WHERE ROWNUM <= 24 ORDER BY LABEL";

        String sql_statement_1 = "WITH CHARTSOURCE as ("
                + "SELECT CSCHEDULEDATE as LABEL, SHIPCOUNT as Y1, to_char(SHIPWEIGHT, '99999.99') as Y2 "
                + "FROM S_TFFX WHERE ADDR1 like N'" + addr1 + "' AND "
                + "ADDR2 like N'" + addr2 + "' AND PORTNAME like N'" + portName + "' ORDER BY CSCHEDULEDATE DESC"
                + ") SELECT * FROM CHARTSOURCE WHERE ROWNUM <= 24 ORDER BY LABEL";

        String sql_statement_2 = "WITH CHARTSOURCE as ("
                + "SELECT CSCHEDULEDATE as LABEL, DIRECTION,"
                + "to_char((AVGSCHEDULETIME / 3600000.0), '99999.99') as Y_DATA "
                + "FROM S_PJGZSJ WHERE ADDR1 like N'" + addr1 + "' AND "
                + "ADDR2 like N'" + addr2 + "' AND PORTNAME like N'" + portName + "' ORDER BY CSCHEDULEDATE DESC"
                + ") SELECT * FROM CHARTSOURCE WHERE ROWNUM <= 48 ORDER BY LABEL";

        switch (requestType) {

            case 0:
                return DataUtils.resultSetToJson(execute_query(sql_statement_0));
            case 1:
                return DataUtils.resultSetToJson(execute_query(sql_statement_1));
            case 2:
                return DataUtils.resultSetToJson(execute_query(sql_statement_2));
            default:
                return getLineInfo_Season(requestType, addr1, addr2, portName);
        }
    }
    
    private String getTimeLabel(int request_type){
        int time_block = request_type / 3;
        
        String sql_month = "SELECT DISTINCT TO_CHAR(SCHEDULEDATE, 'YYYY-MM') AS TIMELABEL "
                + "FROM S_FACT ORDER BY TIMELABEL";
        String sql_season = "SELECT DISTINCT (TO_CHAR(SCHEDULEDATE, 'YYYY') || '-0' || TO_CHAR(SCHEDULEDATE, 'Q')) AS TIMELABEL "
                + "FROM S_FACT ORDER BY TIMELABEL";
        String sql_halfyear = "SELECT DISTINCT (TO_CHAR(SCHEDULEDATE, 'YYYY') || '-' || "
                        + "(case when to_number(TO_CHAR(SCHEDULEDATE, 'MM'), '99') < 7 then '上半年' else '下半年' end)) AS TIMELABEL "
                + "FROM S_FACT ORDER BY TIMELABEL";
        String sql_year = "SELECT (TO_CHAR(SCHEDULEDATE, 'YYYY') || '全年') AS TIMELABEL "
                + "FROM S_FACT ORDER BY TIMELABEL";
        String sql_unique_year = "SELECT DISTINCT TO_CHAR(SCHEDULEDATE, 'YYYY') AS TIMELABEL "
                + "FROM S_FACT ORDER BY TIMELABEL";
        
        switch(time_block){
            case 0:  
                return DataUtils.resultSetToJson(execute_query(sql_month));
            case 1:
                return DataUtils.resultSetToJson(execute_query(sql_season));
            case 2:
                return DataUtils.resultSetToJson(execute_query(sql_halfyear));
            case 3:
                return DataUtils.resultSetToJson(execute_query(sql_year));
            case -1:
                return DataUtils.resultSetToJson(execute_query(sql_unique_year));
        }

        return "[]";
    }
    

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            try {
                String requestChart = (request.getParameter("request_chart"));
                int requestType = Integer.parseInt(request.getParameter("request_type"));
                //out.println(getLineInfo(2, "市处 1", "船闸 1", "闸号1"));
                if (requestChart == null || requestChart.equals("table")) {
                    out.println(getTableInfo(requestType, request.getParameter("start_cschedule"), request.getParameter("end_cschedule")));
                } else if (requestChart.equals("line")) {
                    if (requestType % 3 != 1) {
                        out.println(getLineInfo(requestType, request.getParameter("addr1"),
                                request.getParameter("addr2"), request.getParameter("portname")));
                    } else {
                        out.println(getLineInfo_2(requestType / 3, request.getParameter("addr1"),
                                request.getParameter("addr2"), request.getParameter("portname"), request.getParameter("shiptype")));
                    }

                } else if (requestChart.equals("pie")) {
                    out.println(getPieInfo(requestType, request.getParameter("addr1"), request.getParameter("addr2"), request.getParameter("portname"), request.getParameter("cscheduledate")));
                } else if (requestChart.equals("details")){
                    out.println(getTableDetails(request.getParameter("addr1"), request.getParameter("addr2"), request.getParameter("portname"), requestType, request.getParameter("cscheduledate"), request.getParameter("direction")));
                } else if (requestChart.equals("timelabel")){
                    out.println(getTimeLabel(-3));
                }
                //out.println(getTableTimeColumn("S_ZCRQ"));
            } catch (Exception e) {
                out.println(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
