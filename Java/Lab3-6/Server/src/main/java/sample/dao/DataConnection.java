package sample.dao;


import sample.entity.Record;
import sample.entity.Vocabulary;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

public interface DataConnection extends Remote {

    List<Vocabulary> getVocabularies() throws RemoteException, SQLException, JAXBException;

    List<Record> selectVocabulary(int id, boolean isExplanatory) throws RemoteException, SQLException, JAXBException;

    List<Record> findAllLikeStr(String subStr) throws RemoteException, SQLException, JAXBException;

    int addWord(String word) throws RemoteException, SQLException, JAXBException, IOException;

    int deleteWord(int id) throws RemoteException, SQLException, JAXBException, IOException;

    int editWord(int id, String word) throws RemoteException, SQLException, JAXBException, IOException;

    int addVocabulary(String name, boolean type) throws RemoteException, SQLException, JAXBException, IOException;

    int updateVocabulary(int id, String name, boolean type) throws RemoteException, SQLException, JAXBException, IOException;

    int deleteVocabulary(int id) throws RemoteException, SQLException, JAXBException, IOException;

    int addRecord(int vocabularyID, boolean type, String word, String definition) throws RemoteException, SQLException, JAXBException, IOException;

    int editRecord(int id, boolean type, String word, String definition) throws RemoteException, SQLException, JAXBException, IOException;

    int deleteRecord(int id, boolean type) throws RemoteException, SQLException, JAXBException, IOException;
}
