package sqlsupport;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
/**
 *
 * @author xpy12
 */
public class OracleImpl {
    
    private Connection conn = null;
    
    public void close(){
        try{
            if(conn != null && !conn.isClosed())
                conn.close();
            conn = null;
        } catch(Exception e){
            
        }
    }
    
    public boolean open() throws Exception{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String username = "admin";
        String password = "123456";
        conn = DriverManager.getConnection(url, username, password);
        return conn != null;
    }
    
    public void finialize() throws Exception{
        if(conn != null && !conn.isClosed())
            conn.close();
    }
    
    public void execute(String sql) throws Exception{
        PreparedStatement ps = conn.prepareStatement(sql);
        if(ps.execute())
            System.out.println("[FAILED]" + sql);
        
    }
    
    public ResultSet excuteQuery(String sql){
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            return ps.executeQuery();
        } catch(SQLException e){
            System.err.println("SQL Statement");
            e.printStackTrace();
        }
        return null;
    }
    
    public void insertOrgan(int id, String addr1, String addr2) throws Exception{
        String sql = "INSERT INTO S_ORGAN VALUES(?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        statement.setString(2, addr1);
        statement.setString(3, addr2);
        if(statement.execute())
            System.out.println("[FAILED]" + sql);
        statement.close();
    }
    
    public void insertShip(int shipID, String shipType, int shipCount, float totalWeight) throws Exception{
        String sql = "INSERT INTO S_SHIPINFO VALUES(?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, shipID);
        statement.setString(2, shipType);
        statement.setInt(3, shipCount);
        statement.setFloat(4, totalWeight);
        if(statement.execute())
            System.out.println("[FAILED]" + sql);
        statement.close();
    }
    
    public void insertSchedule(int lsho, int organID, int shipID, String portName, 
            char direction, Date regDate, Date scheduleDate) throws Exception{
        String sql = "INSERT INTO S_SCHEDULE VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, lsho);
        statement.setInt(2, organID);
        statement.setInt(3, shipID);
        statement.setString(4, portName);
        statement.setString(5, direction + "");
        statement.setDate(6, regDate);
        statement.setDate(7, scheduleDate);
        if(statement.execute())
            System.out.println("[FAILED]" + sql);
        statement.close();
    }
    
    
}
