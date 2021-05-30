package sample.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

public class Word implements Serializable {

    private static final long serialVersionUID = 3L;

    private int id;
    private String word;

    public Word() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public Word(int id, String word) {
        this.id = id;
        this.word = word;
    }
    public Word(String word){
        this.id = (int)(Math.random()*10000000);
        this.word=word;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }
}
