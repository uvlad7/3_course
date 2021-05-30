public interface ControllerInterface {
    boolean open(String path);

    void showValuable(String org);

    void showLazy();

    void showNames();

    void search();

    void count(String org);
}