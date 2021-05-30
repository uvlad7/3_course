import java.io.Serializable;


public class Action implements Serializable {
    private String message;
    private Type type;
    private String filename;

    public Action() {
    }

    public Action(String message, Type type, String filename) {
        this.message = message;
        this.type = type;
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
