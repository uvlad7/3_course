package app.entity;

import java.util.ArrayList;
import java.util.List;

public class VocabularyList {
    private List<Vocabulary> vocabularies;

    public VocabularyList(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public VocabularyList() {
        vocabularies = new ArrayList<>();
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }
}
