package app.dao.impl;

import app.dao.DataConnection;
import app.data.SqlConnector;
import app.entity.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MySQLDataConnection implements DataConnection {
    //language=MySQL
    private static String CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS ";
    //language=MySQL
    private static String USE_SCHEMA = "USE ";
    //language=MySQL
    private static String CREATE_WORD_TABLE = "CREATE TABLE IF NOT EXISTS word (" +
            "    id int NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "    text varchar(20) NOT NULL UNIQUE" +
            ");";
    //language=MySQL
    private static String CREATE_DEFINITION_TABLE = "CREATE TABLE IF NOT EXISTS definition (" +
            "    id int NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "    text varchar(100) NOT NULL," +
            "    word_id int NOT NULL," +
            "    vocabulary_id int NOT NULL," +
            "    FOREIGN KEY (word_id) REFERENCES word (id) ON DELETE CASCADE ON UPDATE CASCADE," +
            "    FOREIGN KEY (vocabulary_id) REFERENCES vocabulary (id) ON DELETE CASCADE ON UPDATE CASCADE" +
            ");";
    //language=MySQL
    private static String CREATE_VOCABULARY_TABLE = "CREATE TABLE IF NOT EXISTS vocabulary (" +
            "    id int NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "    name varchar(50) NOT NULL UNIQUE," +
            "    type bool NOT NULL" +
            ");";
    //language=MySQL
    private static String CREATE_PAIR_TABLE = "CREATE TABLE IF NOT EXISTS pair (" +
            "    id int NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "    word_id int NOT NULL," +
            "    word_2_id int NOT NULL," +
            "    vocabulary_id int NOT NULL," +
            "    UNIQUE (word_id, word_2_id, vocabulary_id)," +
            "    FOREIGN KEY (word_id) REFERENCES word (id) ON DELETE CASCADE ON UPDATE CASCADE," +
            "    FOREIGN KEY (word_2_id) REFERENCES word (id) ON DELETE CASCADE ON UPDATE CASCADE," +
            "    FOREIGN KEY (vocabulary_id) REFERENCES vocabulary (id) ON DELETE CASCADE ON UPDATE CASCADE" +
            ");";
    //language=MySQL
    private static String SELECT_VOCABULARIES = "select * from vocabulary";
    //language=MySQL
    private static String SELECT_VOCABULARY = "select false as type, p.id, word.id, word.text, w_2.id, w_2.text " +
            "from word join " +
            "(select pair.word_id, pair.word_2_id, pair.id from pair where vocabulary_id = ?) as p " +
            "on word.id = p.word_id join " +
            "word w_2 on w_2.id = p.word_2_id order by word.text;";
    //language=MySQL
    private static String SELECT_EXP_VOCABULARY = "select true as type, d.id, word.id, word.text, d.id, d.text " +
            "from word join " +
            "definition d on word.id = d.word_id " +
            "where d.vocabulary_id = ? order by word.text;";
    //language=MySQL
    private static String FIND_WORDS_BY_SUBSTR = "select false as type, p.id, w.id, w.text, w_2.id, w_2.text, v.id, v.name " +
            "from (select * from word where word.text like ?) w join " +
            "     (select * from pair) as p " +
            "     on w.id = p.word_id join vocabulary v  on p.vocabulary_id = v.id join " +
            "     word w_2 on w_2.id = p.word_2_id order by w.text;";
    //language=MySQL
    private static String FIND_WORDS_EXP_BY_SUBSTR = "select true as type, d.id, w.id, w.text, d.id, d.text, v.id, v.name " +
            "from (select * from word where word.text like ?) w join " +
            "    definition d on w.id = d.word_id join vocabulary v on d.vocabulary_id = v.id " +
            "order by w.text;";
    //language=MySQL
    private static String INSERT_WORD = "insert ignore into word (text) values (?)";
    //language=MySQL
    private static String INSERT_VOCABULARY = "insert ignore into vocabulary (name, type) values (?,?)";
    //language=MySQL
    private static String UPDATE_VOCABULARY = "update vocabulary set name = ?, type = ? where id = ? limit 1;";
    //language=MySQL
    private static String DELETE_VOCABULARY = "delete from vocabulary where id = ? limit 1;";
    //language=MySQL
    private static String INSERT_PAIR = "insert ignore into pair (word_id, word_2_id, vocabulary_id) " +
            "values ( (select id from word where text = ? limit 1), (select id from word where text = ? limit 1), ?);";
    //language=MySQL
    private static String INSERT_DEFINITION = "insert into definition (text, word_id, vocabulary_id) " +
            "values (?, (select id from word where text = ? limit 1), ?);";
    //language=MySQL
    private static String DELETE_PAIR = "delete from pair where id = ? limit 1;";
    //language=MySQL
    private static String DELETE_DEFINITION = "delete from definition where id = ? limit 1;";
    //language=MySQL
    private static String DELETE_WORD = "delete from word where id = ? limit 1;";
    //language=MySQL
    private static String UPDATE_WORD = "update word set text = ? where id = ? limit 1;";
    //language=MySQL
    private static String UPDATE_PAIR = "update pair set word_id = (select id from word where text = ? limit 1), " +
            "word_2_id = (select id from word where text = ? limit 1) where id = ? limit 1;";
    //language=MySQL
    private static String UPDATE_DEFINITION = "update definition set text = ?, " +
            "word_id = (select id from word where text = ? limit 1) where id = ? limit 1;";


    private SqlConnector connector;

    public MySQLDataConnection(Map<String, String> properties) throws SQLException {
        connector = SqlConnector.getInstance(properties);
        initTables(properties.get("db.schema"));
    }

    private void initTables(String schema) throws SQLException {
        try (Statement statement = connector.getConnection().createStatement()) {
            statement.executeUpdate(CREATE_SCHEMA + schema);
            statement.executeUpdate(USE_SCHEMA + schema);
            statement.executeUpdate(CREATE_WORD_TABLE);
            statement.executeUpdate(CREATE_VOCABULARY_TABLE);
            statement.executeUpdate(CREATE_DEFINITION_TABLE);
            statement.executeUpdate(CREATE_PAIR_TABLE);
        }
    }

    @Override
    public VocabularyList getVocabularies() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_VOCABULARIES);
        List<Vocabulary> vocabularies = new ArrayList<>();
        while (resultSet.next()) {
            vocabularies.add(new Vocabulary(resultSet.getInt("id"),
                    resultSet.getString("name"), resultSet.getBoolean("type")));
        }
        return new VocabularyList(vocabularies);
    }

    @Override
    public RecordList selectVocabulary(int id, boolean isExplanatory) throws SQLException {
        PreparedStatement statement = connector.getConnection().prepareStatement(isExplanatory ? SELECT_EXP_VOCABULARY : SELECT_VOCABULARY);
        statement.setInt(1, id);
        return initVocabulary(statement.executeQuery(), false, isExplanatory ? "E" : "T");
    }

    @Override
    public RecordList findAllLikeStr(String substr) throws SQLException {
        PreparedStatement statement1 = connector.getConnection().prepareStatement(FIND_WORDS_EXP_BY_SUBSTR);
        PreparedStatement statement2 = connector.getConnection().prepareStatement(FIND_WORDS_BY_SUBSTR);
        statement1.setString(1, "%" + substr + "%");
        statement2.setString(1, "%" + substr + "%");
        RecordList res = initVocabulary(statement1.executeQuery(), true, "M");
        res.getRecords().addAll(initVocabulary(statement2.executeQuery(), true, "M").getRecords());
        return res;
    }

    @Override
    public int addWord(String word) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(INSERT_WORD);
        preparedStatement.setString(1, word);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int deleteWord(int id) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(DELETE_WORD);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int editWord(int id, String word) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(UPDATE_WORD);
        preparedStatement.setString(1, word);
        preparedStatement.setInt(2, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int addVocabulary(String name, boolean type) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(INSERT_VOCABULARY);
        preparedStatement.setString(1, name);
        preparedStatement.setBoolean(2, type);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int updateVocabulary(int id, String name, boolean type) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(UPDATE_VOCABULARY);
        preparedStatement.setString(1, name);
        preparedStatement.setBoolean(2, type);
        preparedStatement.setInt(3, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int deleteVocabulary(int id) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(DELETE_VOCABULARY);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }


    @Override
    public int addRecord(int vocabularyID, boolean type, String word, String definition) throws SQLException {
        return setRecord(vocabularyID, type, word, definition, INSERT_DEFINITION, INSERT_PAIR);
    }

    @Override
    public int deleteRecord(int id, boolean type) throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(type ? DELETE_DEFINITION : DELETE_VOCABULARY);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int editRecord(int id, boolean type, String word, String definition) throws SQLException {
        return setRecord(id, type, word, definition, UPDATE_DEFINITION, UPDATE_PAIR);
    }

    private int setRecord(int id, boolean type, String word, String definition, String updateDefinition, String updatePair) throws SQLException {
        addWord(word);
        if (type) {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement(updateDefinition);
            preparedStatement.setString(1, definition);
            preparedStatement.setString(2, word);
            preparedStatement.setInt(3, id);
            return preparedStatement.executeUpdate();
        } else {
            addWord(definition);
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement(updatePair);
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, definition);
            preparedStatement.setInt(3, id);
            return preparedStatement.executeUpdate();
        }
    }

    private RecordList initVocabulary(ResultSet resultSet, boolean hasVoc, String type) throws SQLException {

        List<Record> lines = new ArrayList<>();
        while (resultSet.next()) {
            lines.add(new Record(resultSet.getBoolean(1), resultSet.getInt(2),
                    new Word(resultSet.getInt(3), resultSet.getString(4)),
                    new Word(resultSet.getInt(5), resultSet.getString(6)),
                    (hasVoc ? new Vocabulary(resultSet.getInt(7), resultSet.getString(8),
                            resultSet.getBoolean(1)) : null)));
        }
        return new RecordList(lines, type);
    }
}
