/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlsupport;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author xpy12
 */
@WebServlet(name = "SelectDataSource", urlPatterns = {"/select_source"})
public class SelectDataSource extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private OracleImpl sqlConn = new OracleImpl();

    public SelectDataSource() {
        try {
            sqlConn.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String addr1 = request.getParameter("addr1");
            String addr2 = request.getParameter("addr2");
            String portname = request.getParameter("portname");

            if (addr1 == null) {
                out.println(DataUtils.resultSetToJson(
                        sqlConn.excuteQuery("SELECT DISTINCT ADDR1 AS LISTITEM FROM S_FACT "
                                + " ORDER BY ADDR1")));
            } else if (addr2 == null) {
                out.println(DataUtils.resultSetToJson(
                        sqlConn.excuteQuery("SELECT DISTINCT ADDR2 AS LISTITEM FROM S_FACT "
                                + "WHERE ADDR1 = N'" + addr1 + "' ORDER BY ADDR2")));
            } else if (portname == null) {
                out.println(DataUtils.resultSetToJson(
                        sqlConn.excuteQuery("SELECT DISTINCT PORTNAME AS LISTITEM FROM S_FACT "
                                + "WHERE ADDR1 = N'" + addr1 + "' AND "
                                + "ADDR2 = N'" + addr2 + "' ORDER BY PORTNAME")));
            } else if (request.getParameter("request_type") != null) {
                int request_type = Integer.parseInt(request.getParameter("request_type"));
                switch (request_type) {
                    case 0:
                        out.println(DataUtils.resultSetToJson(
                                sqlConn.excuteQuery("SELECT DISTINCT (TO_CHAR(SCHEDULEDATE, 'YYYY-MM')) AS LISTITEM FROM S_FACT  "
                                        + "WHERE ADDR1 = N'" + addr1 + "' AND "
                                        + "ADDR2 = N'" + addr2 + "' AND "
                                        + "PORTNAME = N'" + portname + "' ORDER BY LISTITEM")));
                        break;
                    case 1:
                        out.println(DataUtils.resultSetToJson(
                                sqlConn.excuteQuery("SELECT DISTINCT (TO_CHAR(SCHEDULEDATE, 'YYYY-Q')) AS LISTITEM FROM S_FACT  "
                                        + "WHERE ADDR1 = N'" + addr1 + "' AND "
                                        + "ADDR2 = N'" + addr2 + "' AND "
                                        + "PORTNAME = N'" + portname + "' ORDER BY LISTITEM")));
                        break;
                    case 2:
                        out.println(DataUtils.resultSetToJson(
                                sqlConn.excuteQuery("SELECT DISTINCT (TO_CHAR(SCHEDULEDATE, 'YYYY')) AS LISTITEM FROM S_FACT  "
                                        + "WHERE ADDR1 = N'" + addr1 + "' AND "
                                        + "ADDR2 = N'" + addr2 + "' AND "
                                        + "PORTNAME = N'" + portname + "' ORDER BY LISTITEM")));
                        break;
                    case 3:
                        out.println(DataUtils.resultSetToJson(
                                sqlConn.excuteQuery("SELECT DISTINCT (TO_CHAR(SCHEDULEDATE, 'YYYY')) AS LISTITEM FROM S_FACT  "
                                        + "WHERE ADDR1 = N'" + addr1 + "' AND "
                                        + "ADDR2 = N'" + addr2 + "' AND "
                                        + "PORTNAME = N'" + portname + "' ORDER BY LISTITEM")));
                    case 4:
                        out.println(DataUtils.resultSetToJson(
                                sqlConn.excuteQuery("SELECT DISTINCT SHIPTYPE AS LISTITEM FROM S_FACT  "
                                        + "WHERE ADDR1 = N'" + addr1 + "' AND "
                                        + "ADDR2 = N'" + addr2 + "' AND "
                                        + "PORTNAME = N'" + portname + "' ORDER BY LISTITEM")));
                }

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
