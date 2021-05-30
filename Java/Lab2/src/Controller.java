import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private View view;
    private Pane pane;
    private Properties ini;
    private Map<String, String[]> languages;
    private Stage stage;
    private File currentFile;
    private boolean isModified;
    private ResourceBundle resources;
    private List<Train> model;
    private DateTimeFormatter formatter;

    public Controller(Stage stage) throws Exception {
        this.stage = stage;
        Properties langProperties = new Properties();
        langProperties.load(new InputStreamReader(getClass().getResourceAsStream("languages.properties"), StandardCharsets.UTF_8));
        ini = new Properties();
        File file = new File("settings.ini");
        if (file.exists() || file.createNewFile()) {
            ini.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        }
        languages = Arrays.stream(langProperties.getProperty("languages").split(";")).map(s -> s.split(":")).collect(
                Collectors.toMap(strings -> strings[0], strings -> strings[1].split("_")));
        FXMLLoader loader = new FXMLLoader();
        resources = ResourceBundle.getBundle("bundles.LangBundle", locale());
        loader.setResources(resources);
        loader.setLocation(getClass().getResource("View.fxml"));
        stage.setTitle(resources.getString("title"));
        pane = loader.<Pane>load();
        view = loader.getController();
        view.setController(this);
        view.setStage(stage);
        formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(resources.getLocale());
        model = new ArrayList<>();
    }

    public void create() {
        model = new ArrayList<>();
        view.set(model);
        currentFile = null;
        isModified = false;
    }

    public void add() {
        Train item = new Train(123, resources.getString("from"), resources.getString("to"),
                LocalTime.of(4, 20), LocalTime.of(10, 10), 146.0, resources.getLocale());
        model.add(item);
        view.add(item);
        onChange();
    }

    public void delete(Train item) {
        model.remove(item);
        view.remove(item);
        onChange();
    }

    public void filter() {
        Map<String, String> answer = view.showFilter(dark());
        if (answer != null) {
            view.set(model.stream().filter(train ->
                    (train.getFrom().toLowerCase().contains(answer.get("from").toLowerCase()) &&
                            (train.getArrival().isAfter(LocalTime.parse(answer.get("arrival_from"), formatter)) ||
                                    train.getArrival().equals(LocalTime.parse(answer.get("arrival_from"), formatter))) &&
                            (train.getArrival().isBefore(LocalTime.parse(answer.get("arrival_to"), formatter)) ||
                                    train.getArrival().equals(LocalTime.parse(answer.get("arrival_to"), formatter))))).filter(train ->
                    (train.getTo().toLowerCase().contains(answer.get("to").toLowerCase()) &&
                            (train.getDeparture().isAfter(LocalTime.parse(answer.get("departure_from"), formatter)) ||
                                    train.getDeparture().equals(LocalTime.parse(answer.get("departure_from"), formatter))) &&
                            (train.getDeparture().isBefore(LocalTime.parse(answer.get("departure_to"), formatter)) ||
                                    train.getDeparture().equals(LocalTime.parse(answer.get("departure_to"), formatter))))
            ).collect(Collectors.toList()));
        }
    }

    public void open(File file) {
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            model = (List<Train>) stream.readObject();
            view.set(model);
            stream.close();
            currentFile = file;
            isModified = false;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            view.warningDialog(resources.getString("cantOpen").replace("filename", file.getName()));
        }
    }

    public boolean saveAs(File file) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
            stream.writeObject(model);
            stream.close();
            currentFile = file;
            isModified = false;
            return true;
        } catch (IOException e) {
            view.warningDialog(resources.getString("fileNotFound").replace("filename", file.getName()));
            return false;
        }
    }

    public boolean save() {
        if (currentFile == null) {
            return false;
        }
        return saveAs(currentFile);
    }

    public void settings() {
        Map<String, String> answer = view.showSettings(dark(), languages.keySet(), lang());
        if (answer != null) {
            if (dark() && !Boolean.parseBoolean(answer.get("dark"))) {
                stage.getScene().getStylesheets().remove(getClass().getResource("darcula.css").toExternalForm());
            }
            if (!dark() && Boolean.parseBoolean(answer.get("dark"))) {
                stage.getScene().getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
            }
            for (Map.Entry<String, String> entry : answer.entrySet()) {
                ini.setProperty(entry.getKey(), entry.getValue());
            }
            try {
                ini.store(new OutputStreamWriter(new FileOutputStream(new File("settings.ini")), StandardCharsets.UTF_8), null);
            } catch (IOException ignored) {

            }
        }
    }

    public void onChange() {
        isModified = true;
    }

    public boolean onClose() {
        if (isModified) {
            return view.saveOnClose((currentFile == null ? resources.getString("unnamed") : currentFile.getName()));
        }
        return true;
    }

    public Pane getPane() {
        return pane;
    }

    private String lang() {
        return resources.getString("lang");
    }

    private Locale locale() {
        String lang = ini.getProperty("lang");
        if (lang == null)
            return Locale.getDefault();
        String[] params = languages.get(lang);
        return new Locale(params[0], params[1]);
    }

    public boolean dark() {
        return Boolean.parseBoolean(ini.getProperty("dark", "true"));
    }
}
