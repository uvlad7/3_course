package app.entity;

public class Vocabulary {
    private Integer id;
    private String name;
    private boolean isExplanatory;

    public Vocabulary() {
    }

    public Vocabulary(Integer id, String name, boolean isExplanatory) {
        this.id = id;
        this.name = name;
        this.isExplanatory = isExplanatory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExplanatory() {
        return isExplanatory;
    }

    public void setExplanatory(boolean explanatory) {
        isExplanatory = explanatory;
    }

    @Override
    public String toString() {
        return name;
    }
}
