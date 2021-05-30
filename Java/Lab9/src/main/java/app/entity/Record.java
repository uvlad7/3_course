package app.entity;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.Serializable;

public class Record implements Serializable {
    private Integer id;
    private Word word;
    private Word word2;
    private Vocabulary vocabulary;
    private boolean isExplanatory;

    public Record() {
    }

    public Record(boolean isExplanatory, Integer id, @NotNull Word word, @NotNull Word word2, @Nullable Vocabulary vocabulary) {
        this.word = word;
        this.word2 = word2;
        this.id = id;
        this.vocabulary = vocabulary;
        this.isExplanatory = isExplanatory;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
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

    public void setExplanatory(boolean explanatory) {
        isExplanatory = explanatory;
    }

    @Override
    public String toString() {
        return word + ": " + word2 + (vocabulary == null ? "" : " (" + vocabulary + ")");
    }
}
