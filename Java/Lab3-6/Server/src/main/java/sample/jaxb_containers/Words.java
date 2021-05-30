package sample.jaxb_containers;

import sample.entity.Word;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "words")
@XmlAccessorType(XmlAccessType.FIELD)
public class Words {
    private
    @XmlElement(name = "word", type = Word.class)
    List<Word> words;

    public Words() {
    }

    public Words(List<Word> words) {
        this.words = words;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
