package sample.dao;

import sample.entity.Record;
import sample.entity.Vocabulary;

import java.sql.SQLException;
import java.util.List;

public interface DataConnection {

    List<Vocabulary> getVocabularies() throws SQLException;

    List<Record> selectVocabulary(int id, boolean isExplanatory) throws SQLException;

    List<Record> findAllLikeStr(String subStr) throws SQLException;

    int addWord(String word) throws SQLException;

    int deleteWord(int id) throws SQLException;

    int editWord(int id, String word) throws SQLException;

    int addVocabulary(String name, boolean type) throws SQLException;

    int updateVocabulary(int id, String name, boolean type) throws SQLException;

    int deleteVocabulary(int id) throws SQLException;

    int addRecord(int vocabularyID, boolean type, String word, String definition) throws SQLException;

    int editRecord(int id, boolean type, String word, String definition) throws SQLException;

    int deleteRecord(int id, boolean type) throws SQLException;
}
