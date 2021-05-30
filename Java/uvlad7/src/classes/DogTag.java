package classes;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;

public class DogTag extends SimpleTagSupport {
    private String firstDog;
    private String secondDog;
    StringWriter sw = new StringWriter();

   public void setFirstDog(String firstDog){
       this.firstDog = firstDog;
   }
   public void setSecondDog(String secondDog){
       this.secondDog = secondDog;
   }

    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        getJspBody().invoke(sw);
        out.println(sw.toString());
        out.println("<br><br><img src =" + firstDog + secondDog + ".jpg>");

    }
}
