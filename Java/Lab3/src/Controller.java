import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        stage.setTitle(resources.getString("unnamed") + " - JScript");
        pane = loader.<Pane>load();
        view = loader.getController();
        view.setController(this);
        view.setStage(stage);
        view.setWrapText(wrap());
        view.setFont(font());
        isModified = false;
    }

    public void create() {
        view.setText("");
        currentFile = null;
        isModified = false;
        stage.setTitle(resources.getString("unnamed") + " - JScript");
    }

    public void open(File file) {
        try {
            view.setText(new String(Files.readAllBytes(Paths.get(file.toURI()))));
            currentFile = file;
            isModified = false;
            stage.setTitle(file.getName() + " - JScript");
        } catch (IOException e) {
            view.warningDialog(resources.getString("fileNotFound").replace("filename", file.getName()));
        }
    }

    public boolean saveAs(File file) {
        try {
            Files.write(Paths.get(file.toURI()), view.getText().getBytes());
            currentFile = file;
            isModified = false;
            stage.setTitle(file.getName() + " - JScript");
            return true;
        } catch (IOException e) {
            view.warningDialog(resources.getString("cantOpen").replace("filename", file.getName()));
            return false;
        }
    }

    public boolean save() {
        if (currentFile == null) {
            return false;
        }
        return saveAs(currentFile);
    }

    public void run() {
        view.cls();
        Interpreter interpreter = new Interpreter(view.getOut(), view.getErr());

        interpreter.run(view.getText());
    }

    public void stop() {
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

    public void setWrap(boolean value) {
        ini.setProperty("wrap", Boolean.toString(value));
        try {
            ini.store(new OutputStreamWriter(new FileOutputStream(new File("settings.ini")), StandardCharsets.UTF_8), null);
        } catch (IOException ignored) {

        }
    }

    public void setFont() {
        Optional<Font> answer = view.showSelectFont(font());
        if (answer.isPresent()) {
            Font font = answer.get();
            view.setFont(font);
            ini.setProperty("fontName", font.getName());
            ini.setProperty("fontSize", Double.toString(font.getSize()));
            try {
                ini.store(new OutputStreamWriter(new FileOutputStream(new File("settings.ini")), StandardCharsets.UTF_8), null);
            } catch (IOException ignored) {

            }
        }
    }

    public void onChange() {
        if (!isModified) {
            stage.setTitle("*" + stage.getTitle());
            isModified = true;
        }
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

    private boolean wrap() {
        return Boolean.parseBoolean(ini.getProperty("wrap", "true"));
    }

    private Font font() {
        String fontName = ini.getProperty("fontName");
        String fontSize = ini.getProperty("fontSize");
        if (fontName != null) {
            return new Font(fontName, (fontSize == null ? Font.getDefault().getSize() : Double.parseDouble(fontSize)));
        }
        return Font.getDefault();
    }
}
