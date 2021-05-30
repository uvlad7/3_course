package sample.data;

import sample.entity.Record;
import sample.entity.Vocabulary;
import sample.entity.Word;
import sample.jaxb_containers.Records;
import sample.jaxb_containers.Vocabularies;
import sample.jaxb_containers.Words;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLConnector {
    private JAXBContext vocContext;
    private JAXBContext recContext;
    private JAXBContext wordsContext;
    private File vocFile;
    private File recFile;
    private File wordsFile;

    public XMLConnector(String vocPath, String recPath, String wordsPath) throws JAXBException {
        vocFile = new File(vocPath);
        recFile = new File(recPath);
        wordsFile = new File(wordsPath);
        vocContext = JAXBContext.newInstance(Vocabularies.class);
        recContext = JAXBContext.newInstance(Records.class);
        wordsContext = JAXBContext.newInstance(Words.class);
    }

    public List<Vocabulary> getXMLVoc() throws JAXBException {
        Vocabularies vocs = (Vocabularies) vocContext.createUnmarshaller().unmarshal(vocFile);
        return (vocs.getVocabularies() == null ? new ArrayList<>() : vocs.getVocabularies());
    }

    public List<Record> getXMLRec() throws JAXBException {
        Records recs = (Records) recContext.createUnmarshaller().unmarshal(recFile);
        return (recs.getRecords() == null ? new ArrayList<>() : recs.getRecords());
    }

    public List<Word> getXMLWords() throws JAXBException {
        Words words = (Words) wordsContext.createUnmarshaller().unmarshal(wordsFile);
        return (words.getWords() == null ? new ArrayList<>() : words.getWords());
    }

    public void makeVocXML(List<Vocabulary> vocData) throws JAXBException {
        Vocabularies vocabularies = new Vocabularies(vocData);
        Marshaller marshaller = vocContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(vocabularies, vocFile);
    }

    public void makeRecXML(List<Record> phoneData) throws JAXBException {
        Records book = new Records(phoneData);
        Marshaller marshaller = recContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(book, recFile);
    }

    public void makeWordsXML(List<Word> phoneData) throws JAXBException {
        Words book = new Words(phoneData);
        Marshaller marshaller = wordsContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(book, wordsFile);
    }

}
