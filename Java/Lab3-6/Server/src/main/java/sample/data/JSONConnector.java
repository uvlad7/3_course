package sample.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import sample.entity.Record;
import sample.entity.Vocabulary;
import sample.entity.Word;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class JSONConnector {
    private Type recordList;
    private Type wordList;
    private Type vocabularyList;
    private File vocFile, recFile, wordsFile;
    private Gson gson;


    public JSONConnector(String vocPath, String recPath, String wordsPath) {
        vocFile = new File(vocPath);
        recFile = new File(recPath);
        wordsFile = new File(wordsPath);
        gson = new GsonBuilder().setPrettyPrinting().create();
        recordList = new TypeToken<List<Record>>() {
        }.getType();
        wordList = new TypeToken<List<Word>>() {
        }.getType();
        vocabularyList = new TypeToken<List<Vocabulary>>() {
        }.getType();
    }

    public List<Record> getRecords() {
        return gson.fromJson(getJson(recFile), recordList);
    }

    public List<Vocabulary> getVocabularies() {
        return gson.fromJson(getJson(vocFile), vocabularyList);
    }

    public List<Word> getWords() {
        return gson.fromJson(getJson(wordsFile), wordList);
    }

    public void saveRecords(List<Record> records) throws IOException {
        writeToFile(gson.toJson(records), recFile);
    }

    public void saveVocabularies(List<Vocabulary> vocabularies) throws IOException {
        writeToFile(gson.toJson(vocabularies), vocFile);
    }

    public void saveWords(List<Word> words) throws IOException {
        writeToFile(gson.toJson(words), wordsFile);
    }


    private String getJson(File file) {
        StringBuilder json = new StringBuilder("");
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json.toString();
    }

    private void writeToFile(String json, File file) throws IOException {
        try (FileWriter stream = new FileWriter(file)) {
            stream.write(json);
        }
    }
}
