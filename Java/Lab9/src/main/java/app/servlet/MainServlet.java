/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package app.servlet;

import app.dao.DataConnection;
import app.dao.impl.MySQLDataConnection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class MainServlet extends HttpServlet {
    private DataConnection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = new MySQLDataConnection(new HashMap<String, String>() {{
                put("db.url", config.getInitParameter("db.url"));
                put("db.user", config.getInitParameter("db.user"));
                put("db.password", config.getInitParameter("db.password"));
                put("db.schema", config.getInitParameter("db.schema"));
            }});
        } catch (SQLException | ClassNotFoundException ex) {
            throw new ServletException(ex);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String wordId = request.getParameter("word_id");
        String recordId = request.getParameter("record_id");
        String isExp = request.getParameter("is_exp");
        String vocId = request.getParameter("voc_id");
        String vocExp = request.getParameter("voc_exp");
        try {
            switch (request.getParameter("action_name")) {
                case "delete_word": {
                    connection.deleteWord(Integer.parseInt(wordId));
                    break;
                }
                case "delete_record": {
                    connection.deleteRecord(Integer.parseInt(recordId), Boolean.parseBoolean(isExp));
                    break;
                }
                case "delete_vocabulary": {
                    connection.deleteVocabulary(Integer.parseInt(vocId));
                    break;
                }
                case "add_vocabulary": {
                    connection.addVocabulary(request.getParameter("voc_name"), Boolean.parseBoolean(vocExp));
                    break;
                }
                case "edit_vocabulary": {
                    connection.updateVocabulary(Integer.parseInt(vocId), request.getParameter("voc_name"), Boolean.parseBoolean(vocExp));
                    break;
                }
                case "add_word": {
                    connection.addWord(request.getParameter("word_text"));
                    break;
                }
                case "edit_word": {
                    connection.editWord(Integer.parseInt(wordId), request.getParameter("word_text"));
                    break;
                }
                case "add_record": {
                    connection.addRecord(Integer.parseInt(vocId), Boolean.parseBoolean(vocExp), request.getParameter("word_text"),  request.getParameter("word2_text"));
                    break;
                }
                case "edit_record": {
                    connection.editRecord(Integer.parseInt(recordId), Boolean.parseBoolean(isExp), request.getParameter("word_text"),  request.getParameter("word2_text"));
                    break;
                }
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("vocs", connection.getVocabularies());
            String vocId = request.getParameter("id");
            String vocType = request.getParameter("explanatory");
            String likeStr = request.getParameter("like_str");
            if (vocId != null) {
                request.setAttribute("recs", connection.selectVocabulary(Integer.parseInt(vocId), Boolean.parseBoolean(vocType)));
            } else if (likeStr != null) {
                request.setAttribute("recs", connection.findAllLikeStr(likeStr));
            }
            request.getRequestDispatcher("/").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

}
