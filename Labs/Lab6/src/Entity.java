import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class Entity implements Serializable {
    private final Date creationDate;
    private Type type;
    transient protected ResourceBundle resources;

    public Entity(Type type, ResourceBundle resources) {
        this.type = type;
        creationDate = new Date();
        this.resources = resources;
    }

    public String getCreationDate() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT, DateFormat.DEFAULT, resources.getLocale());
        return dateFormatter.format(creationDate);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
