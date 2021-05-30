import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class View extends JFrame implements ViewInterface {
    private JList<Employee> valuableList;
    private JList<Employee> sortedList;
    private JList<String> namesList;

    public View(Controller controller) {
        super("IKR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel unsorted = new JPanel(new BorderLayout());
        JPanel sorted = new JPanel(new BorderLayout());
        JPanel names = new JPanel(new BorderLayout());

        valuableList = new JList<>();
        JScrollPane scrollPane1 = new JScrollPane(valuableList);
        unsorted.add(scrollPane1);

        sortedList = new JList<>();
        JScrollPane scrollPane2 = new JScrollPane(sortedList);
        sorted.add(scrollPane2);

        namesList = new JList<>();
        JScrollPane scrollPane4 = new JScrollPane(namesList);
        names.add(scrollPane4);

        tabbedPane.addTab("Valuable", unsorted);
        tabbedPane.addTab("Lazy", sorted);
        tabbedPane.addTab("Organizations", names);

        add(tabbedPane);

        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(open);
        file.add(exit);

        JMenu data = new JMenu("Data");
        data.setEnabled(false);
        JMenuItem showValuableItem = new JMenuItem("Show valuable");
        JMenuItem showSortedByItem = new JMenuItem("Show lazy");
        JMenuItem showNamesItem = new JMenuItem("Show organizations");
        JMenuItem searchItem = new JMenuItem("Search");
        JMenuItem countItem = new JMenuItem("Count min-max salary");
        data.add(showValuableItem);
        data.add(showSortedByItem);
        data.add(showNamesItem);
        data.add(searchItem);
        data.add(countItem);

        menuBar.add(file);
        menuBar.add(data);
        setJMenuBar(menuBar);

        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON file", "json"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getDescription(fileChooser.getSelectedFile());
                if (controller.open(filename)) {
                    data.setEnabled(true);
                }
            }
        });
        exit.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        showValuableItem.addActionListener(e -> {
            JTextField org = new JTextField(20);
            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("Organization:"));
            inputPanel.add(org);
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                    "Enter organization", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                controller.showValuable(org.getText());
            }
        });
        showSortedByItem.addActionListener(e -> controller.showLazy());
        showNamesItem.addActionListener(e -> controller.showNames());
        searchItem.addActionListener(e -> controller.search());
        countItem.addActionListener(e -> {
            JTextField org = new JTextField(20);
            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("Organization:"));
            inputPanel.add(org);
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                    "Enter organization", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                controller.count(org.getText());
            }
        });

        pack();
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void showValuable(List<Employee> unsorted) {
        valuableList.setListData(unsorted.toArray(new Employee[0]));
    }

    @Override
    public void showLazy(List<Employee> sorted) {
        sortedList.setListData(sorted.toArray(new Employee[0]));
    }


    @Override
    public void showNames(List<String> names) {
        namesList.setListData(names.toArray(new String[0]));
    }

    @Override
    public void showInfo(String title, Object message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
}