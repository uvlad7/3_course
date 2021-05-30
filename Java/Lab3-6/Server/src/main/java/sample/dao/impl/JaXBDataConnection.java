package sample.dao.impl;

import sample.dao.DataConnection;
import sample.data.XMLConnector;
import sample.entity.Record;
import sample.entity.Vocabulary;
import sample.entity.Word;

import javax.xml.bind.JAXBException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JaXBDataConnection extends UnicastRemoteObject implements DataConnection {
    XMLConnector connector;
    private AtomicInteger maxVocId;
    private AtomicInteger maxWordId;
    private AtomicInteger maxRecId;

    public JaXBDataConnection(Properties properties) throws RemoteException, JAXBException {
        connector = new XMLConnector(properties.getProperty("xml.voc"),
                properties.getProperty("xml.rec"), properties.getProperty("xml.words"));
        maxVocId = new AtomicInteger(connector.getXMLVoc().stream().map(Vocabulary::getId).max(Integer::compareTo).orElse(0) + 1);
        maxWordId = new AtomicInteger(connector.getXMLWords().stream().map(Word::getId).max(Integer::compareTo).orElse(0) + 1);
        maxRecId = new AtomicInteger(connector.getXMLRec().stream().map(Record::getId).max(Integer::compareTo).orElse(0) + 1);

    }

    @Override
    public List<Vocabulary> getVocabularies() throws JAXBException {
        return connector.getXMLVoc();
    }

    @Override
    public List<Record> selectVocabulary(int id, boolean isExplanatory) throws RemoteException, SQLException, JAXBException {
        return connector.getXMLRec()
                .stream()
                .filter(elem -> elem.getVocabulary().getId() == id).peek(el -> el.setVocabulary(null))
                .collect(Collectors.toList());
    }

    @Override
    public List<Record> findAllLikeStr(String subStr) throws JAXBException {
        return connector.getXMLRec()
                .stream()
                .filter(elem -> elem.getWord().getWord().contains(subStr))
                .collect(Collectors.toList());
    }

    @Override
    public int addWord(String word) throws JAXBException {
        List<Word> words = connector.getXMLWords();
        Optional<Word> vocabulary = words.stream().filter(w -> w.getWord().equals(word)).findAny();
        if (vocabulary.isPresent())
            return 0;
        words.add(new Word(maxWordId.getAndIncrement(), word));
        connector.makeWordsXML(words);
        return 1;
    }

    @Override
    public int deleteWord(int id) throws JAXBException {
        List<Word> words = connector.getXMLWords();
        if (words.removeIf(w -> w.getId() == id)) {
            connector.makeWordsXML(words);
            List<Record> records = connector.getXMLRec();
            records.removeIf(elem -> (elem.getWord().getId() == id ||
                    (!elem.isExplanatory() && elem.getWord2().getId() == id)));
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }

    @Override
    public int editWord(int id, String word) throws JAXBException {
        List<Word> words = connector.getXMLWords();
        Optional<Word> word1 = words.stream().filter(w -> w.getId() == id).findAny();
        if (word1.isPresent()) {
            word1.get().setWord(word);
            connector.makeWordsXML(words);
            List<Record> records = connector.getXMLRec();
            records.forEach(elem -> {
                if (elem.getWord().getId() == id) {
                    elem.getWord().setWord(word);
                }
                if (!elem.isExplanatory() && elem.getWord2().getId() == id) {
                    elem.getWord2().setWord(word);
                }
            });
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }

    @Override
    public int addVocabulary(String name, boolean type) throws JAXBException {
        List<Vocabulary> vocabularies = connector.getXMLVoc();
        Optional<Vocabulary> vocabulary = vocabularies.stream().filter(voc -> voc.getName().equals(name)).findAny();
        if (vocabulary.isPresent())
            return 0;
        vocabularies.add(new Vocabulary(maxVocId.getAndIncrement(), name, type));
        connector.makeVocXML(vocabularies);
        return 1;
    }

    @Override
    public int updateVocabulary(int id, String name, boolean type) throws JAXBException {
        List<Vocabulary> vocabularies = connector.getXMLVoc();
        Optional<Vocabulary> vocabulary = vocabularies.stream().filter(voc -> voc.getId() == id).findAny();
        if (vocabulary.isPresent()) {
            vocabulary.get().setName(name);
            vocabulary.get().setExplanatory(type);
            connector.makeVocXML(vocabularies);
            List<Record> records = connector.getXMLRec();
            records.forEach(elem -> {
                if (elem.getVocabulary().getId() == id) {
                    elem.setVocabulary(vocabulary.get());
                    elem.setExplanatory(type);
                }
            });
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteVocabulary(int id) throws JAXBException {
        List<Vocabulary> vocabularies = connector.getXMLVoc();
        if (vocabularies.removeIf(voc -> voc.getId() == id)) {
            connector.makeVocXML(vocabularies);
            List<Record> records = connector.getXMLRec();
            records.removeIf(elem -> elem.getVocabulary().getId() == id);
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }

    @Override
    public int addRecord(int vocabularyID, boolean type, String word, String definition) throws JAXBException {
        Optional<Vocabulary> vocabulary = connector.getXMLVoc().stream().filter(voc -> voc.getId() == vocabularyID).findAny();
        if (vocabulary.isPresent()) {
            List<Word> words = connector.getXMLWords();
            List<Record> records = connector.getXMLRec();
            Word word1 = getWord(words, false, word);
            Word word2 = getWord(words, type, definition);
            records.add(new Record(type, maxRecId.getAndIncrement(), word1, word2, vocabulary.get()));
            connector.makeWordsXML(words);
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }

    private Word getWord(List<Word> words, boolean type, String word) {
        return type ? new Word(maxWordId.getAndIncrement(), word) : words.stream().filter(elem -> elem.getWord().equals(word)).findAny().orElseGet(() -> {
            Word w = new Word(maxWordId.getAndIncrement(), word);
            words.add(w);
            return w;
        });
    }

    @Override
    public int editRecord(int id, boolean type, String word, String definition) throws JAXBException {
        List<Record> records = connector.getXMLRec();
        Optional<Record> record = records.stream().filter(r -> r.getId() == id).findAny();
        if (record.isPresent()) {
            List<Word> words = connector.getXMLWords();
            Word word1 = getWord(words, false, word);
            Word word2 = getWord(words, type, definition);
            record.get().setWord(word1);
            record.get().setWord2(word2);
            connector.makeWordsXML(words);
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteRecord(int id, boolean type) throws JAXBException {
        List<Record> records = connector.getXMLRec();
        if (records.removeIf(record -> record.getId() == id)) {
            connector.makeRecXML(records);
            return 1;
        }
        return 0;
    }
}
