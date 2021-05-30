import java.io.Serializable;

public class ClientRequest implements Serializable {
    public String message;
    public String request;


    public ClientRequest(String message, String request) {
        this.message = message;
        this.request = request;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return message +
                " requested: \"" + request + "\"\n";
    }
}
