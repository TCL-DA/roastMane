package org.jfree.chart.servlet;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DisplayChart extends HttpServlet {
   public void init() throws ServletException {
   }

   public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      HttpSession session = request.getSession();
      String filename = request.getParameter("filename");
      if (filename == null) {
         throw new ServletException("Parameter 'filename' must be supplied");
      } else {
         filename = ServletUtilities.searchReplace(filename, "..", "");
         File file = new File(System.getProperty("java.io.tmpdir"), filename);
         if (!file.exists()) {
            throw new ServletException("Unable to display the chart with the filename '" + filename + "'.");
         } else {
            boolean isChartInUserList = false;
            ChartDeleter chartDeleter = (ChartDeleter)session.getAttribute("JFreeChart_Deleter");
            if (chartDeleter != null) {
               isChartInUserList = chartDeleter.isChartAvailable(filename);
            }

            boolean isChartPublic = false;
            if (filename.length() >= 6 && filename.substring(0, 6).equals("public")) {
               isChartPublic = true;
            }

            boolean isOneTimeChart = false;
            if (filename.startsWith(ServletUtilities.getTempOneTimeFilePrefix())) {
               isOneTimeChart = true;
            }

            if (!isChartInUserList && !isChartPublic && !isOneTimeChart) {
               throw new ServletException("Chart image not found");
            } else {
               ServletUtilities.sendTempFile(file, response);
               if (isOneTimeChart) {
                  file.delete();
               }
            }
         }
      }
   }
}
