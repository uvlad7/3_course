package sample.entity;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "record")
@XmlAccessorType(XmlAccessType.FIELD)
public class Record implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    @XmlElement(name = "word", type = Word.class)
    private Word word;
    @XmlElement(name = "word2", type = Word.class)
    private Word word2;
    @XmlElement(name = "vocabulary", type = Vocabulary.class)
    private Vocabulary vocabulary;
    private boolean isExplanatory;

    public Record() {
    }

    public Record(boolean isExplanatory, int id, @NotNull Word word, @NotNull Word word2, @Nullable Vocabulary vocabulary) {
        this.word = word;
        this.word2 = word2;
        this.id = id;
        this.vocabulary = vocabulary;
        this.isExplanatory = isExplanatory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Vocabulary getVocabulary() {
        return this.vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Word getWord2() {
        return word2;
    }

    public void setWord2(Word word2) {
        this.word2 = word2;
    }

    public boolean isExplanatory() {
        return isExplanatory;
    }

    public void setExplanatory(boolean type) {
        this.isExplanatory = type;
    }

    @Override
    public String toString() {
        return word + ": " + word2 + (vocabulary == null ? "" : " (" + vocabulary + ")");
    }
}
