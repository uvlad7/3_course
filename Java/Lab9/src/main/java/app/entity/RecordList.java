package app.entity;

import java.io.Serializable;
import java.util.List;

public class RecordList implements Serializable {
    private List<Record> records;
    private String type;

    public RecordList() {
        type = "M";
    }

    public RecordList(List<Record> records, String type) {
        this.records = records;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }
}
