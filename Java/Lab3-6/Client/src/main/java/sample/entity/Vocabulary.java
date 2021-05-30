package sample.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

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

    public void setExplanatory(boolean explanatory) {
        isExplanatory = explanatory;
    }

    public Vocabulary(String name, boolean isExplanatory) {
        this.id=(int)(Math.random()*10000000);
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

    @Override
    public String toString() {
        return name;
    }
}
