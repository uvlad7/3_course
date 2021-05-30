package sample.entity;

public class Vocabulary {
    private int id;
    private String name;
    private boolean isExplanatory;

    public Vocabulary(int id, String name, boolean isExplanatory) {
        this.id = id;
        this.name = name;
        this.isExplanatory = isExplanatory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return name;
    }
}
