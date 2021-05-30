package classes;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "/classes.DogServlet", urlPatterns = {"/classes.DogServlet"})
public class DogServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("throwException") != null) {
            throw new ArithmeticException("Dog Exception");
        }
        if (request.getParameter("radio") != null) {
            request.getSession().setAttribute("page" + ((Integer) request.getSession().getAttribute("pageNumber")).toString(), request.getParameter("radio"));
            if (request.getParameter("next") != null) {
                request.getSession().setAttribute("pageNumber", (Integer) request.getSession().getAttribute("pageNumber") + 1);
            }
        }
        if (request.getParameter("previous") != null) {
            request.getSession().setAttribute("pageNumber", (Integer) request.getSession().getAttribute("pageNumber") - 1);
        } else if (request.getParameter("start") != null) {
            request.getSession().invalidate();
            request.getSession().setAttribute("pageNumber", 1);
        }
        switch ((Integer) request.getSession().getAttribute("pageNumber")) {
            case 1: {
                request.getRequestDispatcher("index.jsp").forward(request, response);
                break;
            }
            case 2: {
                request.getRequestDispatcher("secondDog.jsp").forward(request, response);
                break;
            }
            case 3: {
                request.getRequestDispatcher("resultDog.jsp").forward(request, response);
                break;
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
