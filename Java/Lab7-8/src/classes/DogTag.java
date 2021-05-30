package classes;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

public class DogTag extends SimpleTagSupport {
    StringWriter sw = new StringWriter();
    private String firstDog;
    private String secondDog;

    public void setFirstDog(String firstDog) {
        this.firstDog = firstDog;
    }

    public void setSecondDog(String secondDog) {
        this.secondDog = secondDog;
    }

    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        getJspBody().invoke(sw);
        out.println(sw.toString());
        out.println("<p><img src =" + firstDog + secondDog + ".jpg></p>");
    }
}
