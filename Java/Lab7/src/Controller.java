import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private View view;
    private Pane pane;
    private Properties ini;
    private Map<String, String> languages;
    private Stage stage;
    private ResourceBundle resources;
    private List<Train> model;
    private DateTimeFormatter formatter;
    private Connection connection;
    private Map<String, String> connectionParams;

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
                Collectors.toMap(strings -> strings[0], strings -> strings[1]));
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
        connectionParams = new HashMap<>();
        connectionParams.put("url", "jdbc:mysql://localhost:3306/");
        connectionParams.put("schema", "data");
        connectionParams.put("user", "root");
        connectionParams.put("password", "uvlad7");
    }

    public void create() {
        view.showConnect(dark(), connectionParams).ifPresent(map -> {
            try {
                connectionParams = map;
                Connection temp = DriverManager.getConnection(map.get("url") + "?serverTimezone=UTC", map.get("user"), map.get("password"));
                try (Statement statement = temp.createStatement()) {
                    statement.executeUpdate("CREATE SCHEMA " + map.get("schema"));
                }
                connection = DriverManager.getConnection(map.get("url") + map.get("schema") + "?serverTimezone=UTC", map.get("user"), map.get("password"));
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE train (\n" +
                            "   number_col int NOT NULL,\n" +
                            "   from_col varchar(30) NOT NULL,\n" +
                            "   to_col varchar(30) NOT NULL,\n" +
                            "   departure_hour int NOT NULL,\n" +
                            "   departure_minute int NOT NULL,\n" +
                            "   arrival_hour int NOT NULL,\n" +
                            "   arrival_minute int NOT NULL,\n" +
                            "   value_col double(10,4) NOT NULL,\n" +
                            "   locale_col char(5) NOT NULL,\n" +
                            "   CONSTRAINT train_pk PRIMARY KEY (number_col)\n" +
                            ");");
                    model.clear();
                }
            } catch (ClassCastException | SQLException e) {
                view.warningDialog(resources.getString("cantCreate"));
            }
        });
    }

    public void add() {
        try {
            if (connection == null || connection.isClosed()) {
                view.warningDialog(resources.getString("cantOpen"));
            } else {
                Map<String, String> map = view.createTrain(dark());
                if (map != null) {
                    Train item = new Train(Integer.valueOf(map.get("number")), map.get("from"), map.get("to"),
                            LocalTime.parse(map.get("departure"), formatter), LocalTime.parse(map.get("arrival"), formatter),
                            Double.valueOf(map.get("cost")), resources.getLocale());
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("INSERT INTO train\n" +
                                "VALUES(" + item + ");");
                    }
                    model.add(item);
                    view.add(item);
                }
            }
        } catch (SQLException e) {
            view.warningDialog(resources.getString("cantOpen"));
        }
    }

    public void delete(Train item) {
        try {
            if (connection == null || connection.isClosed()) {
                view.warningDialog(resources.getString("cantOpen"));
            } else {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("DELETE FROM train\n" +
                            "WHERE number_col=" + item.getNumber() + ";");
                }
                model.remove(item);
                view.remove(item);
            }
        } catch (SQLException e) {
            view.warningDialog(resources.getString("cantOpen"));
        }
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

    public void open() {
        view.showConnect(dark(), connectionParams).ifPresent(map -> {
            try {
                connectionParams = map;
                connection = DriverManager.getConnection(map.get("url") + map.get("schema") + "?serverTimezone=UTC", map.get("user"), map.get("password"));
                try (Statement statement = connection.createStatement();
                     ResultSet rs = statement.executeQuery("select * from train")) {
                    model.clear();
                    while (rs.next()) {
                        model.add(new Train(rs.getInt(1), rs.getString(2), rs.getString(3),
                                LocalTime.of(rs.getInt(4), rs.getInt(5)), LocalTime.of(rs.getInt(6), rs.getInt(7)),
                                rs.getDouble(8), Locale.forLanguageTag(rs.getString(9))));
                        view.set(model);
                    }
                }
            } catch (ClassCastException | SQLException e) {
                view.warningDialog(resources.getString("cantOpen"));
            }
        });
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

    public boolean onClose() {
        if (view.onClose()) {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException ignored) {

            }
            return true;
        }
        return false;
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
        return Locale.forLanguageTag(languages.get(lang));
    }

    public boolean dark() {
        return Boolean.parseBoolean(ini.getProperty("dark", "true"));
    }
}
