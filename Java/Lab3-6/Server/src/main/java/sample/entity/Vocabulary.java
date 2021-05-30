package sample.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "vocabulary")
public class Vocabulary implements Serializable {

    private static final long serialVersionUID = 2L;

    private int id;
    private String name;
    private boolean isExplanatory;


    public Vocabulary() {
    }

    public Vocabulary(int id, String name, boolean isExplanatory) {
        this.id = id;
        this.name = name;
        this.isExplanatory = isExplanatory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExplanatory() {
        return isExplanatory;
    }

    public void setExplanatory(boolean explanatory) {
        isExplanatory = explanatory;
    }

    @Override
    public String toString() {
        return name;
    }
}
