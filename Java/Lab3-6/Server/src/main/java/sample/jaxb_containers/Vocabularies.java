package sample.jaxb_containers;

import sample.entity.Vocabulary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "vocabularies")
@XmlAccessorType(XmlAccessType.FIELD)
public class Vocabularies {
    @XmlElement(name = "vocabulary", type = Vocabulary.class)
    private List<Vocabulary> vocabularies;

    public Vocabularies() {
    }

    public Vocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }
}
