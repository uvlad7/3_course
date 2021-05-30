import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MyFrame extends JFrame implements ActionListener {
    private MyController controller;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenu data;
    private JMenuItem append;
    private JMenuItem print;
    private JMenuItem find;
    private JMenuItem delete;
    private JMenuItem open;
    private JMenuItem exit;
    private JList<Record> list;
    private JLabel status;

    public MyFrame(MyController controller) throws HeadlessException {
        super("Lab 11");
        this.controller = controller;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Do you want to close application?", "Close",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
                    setVisible(false);
                    dispose();
                }
            }
        });
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException ignored) {
        }
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        menuBar = new JMenuBar();

        file = new JMenu("File");
        open = new JMenuItem("Open");
        exit = new JMenuItem("Exit");
        file.add(open);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        file.addSeparator();
        file.add(exit);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));

        data = new JMenu("Data");
        append = new JMenuItem("Append");
        print = new JMenuItem("Print");
        delete = new JMenuItem("Delete");
        find = new JMenuItem("Find");
        data.setEnabled(false);

        data.add(append);
        data.add(print);
        data.add(find);
        data.add(delete);

        menuBar.add(file);
        menuBar.add(data);
        setJMenuBar(menuBar);

        open.addActionListener(this);
        exit.addActionListener(this);
        append.addActionListener(this);
        print.addActionListener(this);
        delete.addActionListener(this);
        find.addActionListener(this);

        list = new JList<>();
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane);

        status = new JLabel("No file opened");
        Border border = BorderFactory.createEtchedBorder(Color.white, new Color(178, 178, 178));
        status.setBorder(border);
        //Делаем фон строки непрозрачным
        status.setOpaque(true);
        add(status);

        pack();
        setSize(900, 450);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == open) {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Data file", "dat"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String file = fileChooser.getDescription(fileChooser.getSelectedFile()).replaceFirst("[.][^.]+$", "");
                controller.open(file);
                data.setEnabled(true);
            }
        } else if (event.getSource() == exit) {
            if (JOptionPane.showConfirmDialog(null, "Do you want to close application?", "Close",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
                setVisible(false);
                dispose();
            }
        } else if (event.getSource() == delete) {
            JPanel inputPanel = new JPanel();
            JComboBox<String> index = new JComboBox<>(new String[] {"By department", "By name", "By employment date"});
            index.setEnabled(true);
            index.setSelectedIndex(0);
            inputPanel.add(new JLabel("Index"));
            inputPanel.add(index);
            JTextField key = new JTextField("11", 15);
            inputPanel.add(new JLabel("Key"));
            inputPanel.add(key);
            String[] hints = {"11", "Ivanov", "2000-01-01"};
            index.addItemListener(e -> key.setText(hints[index.getSelectedIndex()]));
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                    "Enter position", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    controller.delete(index.getSelectedIndex(), key.getText());
                } catch (ClassNotFoundException | IOException  | IllegalArgumentException e) {
                    showError(e);
                }
            }
        } else if (event.getSource() == append) {
            JTextField personnelNumber = new JTextField("1234", 10);
            JTextField departmentNumber = new JTextField("11", 10);
            JTextField name = new JTextField("Ivanov", 15);
            JTextField salary = new JTextField("500", 7);
            JTextField date = new JTextField("2000-01-01", 10);
            JTextField allowances = new JTextField("13", 5);
            JTextField income = new JTextField("42.4", 7);

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(8, 2));
            inputPanel.add(new JLabel("Personnel number"));
            inputPanel.add(personnelNumber);
            inputPanel.add(new JLabel("Department number"));
            inputPanel.add(departmentNumber);
            inputPanel.add(new JLabel("Full name"));
            inputPanel.add(name);
            inputPanel.add(new JLabel("Salary"));
            inputPanel.add(salary);
            inputPanel.add(new JLabel("Date of employment"));
            inputPanel.add(date);
            inputPanel.add(new JLabel("Allowances"));
            inputPanel.add(allowances);
            inputPanel.add(new JLabel("Income tax"));
            inputPanel.add(income);

            JCheckBox zipped = new JCheckBox("Zipped", false);
            inputPanel.add(zipped);
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                    "Append employee", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    controller.appendFile(zipped.isSelected(), personnelNumber.getText(), departmentNumber.getText(),
                            name.getText(), salary.getText(), date.getText(), allowances.getText(), income.getText());

                } catch (IOException | ClassNotFoundException | DateTimeParseException | IllegalArgumentException e) {
                    showError(e);
                }
            }
        } else if (event.getSource() == print) {
            JRadioButton notSorted = new JRadioButton("Not sorted");
            JRadioButton sorted = new JRadioButton("Sorted");
            JRadioButton revSorted = new JRadioButton("Reverse sorted");
            ButtonGroup group = new ButtonGroup();
            group.add(notSorted);
            group.add(sorted);
            group.add(revSorted);
            notSorted.setSelected(true);

            JPanel inputPanel = new JPanel();
            inputPanel.add(notSorted);
            inputPanel.add(sorted);
            inputPanel.add(revSorted);

            JComboBox<String> index = new JComboBox<>(new String[] {"By department", "By name", "By employment date"});
            index.setEnabled(false);
            index.setSelectedIndex(0);
            notSorted.addActionListener(e -> index.setEnabled(false));
            sorted.addActionListener(e -> index.setEnabled(true));
            revSorted.addActionListener(e -> index.setEnabled(true));
            inputPanel.add(new JLabel("Index"));
            inputPanel.add(index);
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                    "Print employees", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    if (notSorted.isSelected()) {
                        controller.printFile();
                    } else {
                        controller.printSorted(revSorted.isSelected(), index.getSelectedIndex());
                    }
                } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
                    showError(e);
                }
            }
        } else if (event.getSource() == find) {
            JRadioButton byKey = new JRadioButton("By key");
            JRadioButton larger = new JRadioButton("Large");
            JRadioButton less = new JRadioButton("Less");
            JPanel inputPanel = new JPanel();
            inputPanel.add(byKey);
            inputPanel.add(larger);
            inputPanel.add(less);
            ButtonGroup group = new ButtonGroup();
            group.add(byKey);
            group.add(larger);
            group.add(less);
            byKey.setSelected(true);
            JComboBox<String> index = new JComboBox<>(new String[] {"By department", "By name", "By employment date"});
            index.setEnabled(true);
            index.setSelectedIndex(0);
            inputPanel.add(new JLabel("Index"));
            inputPanel.add(index);
            JTextField key = new JTextField("11", 15);
            inputPanel.add(new JLabel("Key"));
            inputPanel.add(key);
            String[] hints = {"11", "Ivanov", "2000-01-01"};
            index.addItemListener(e -> key.setText(hints[index.getSelectedIndex()]));
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                    "Find employees", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    if (byKey.isSelected()) {
                        controller.printByKey(index.getSelectedIndex(), key.getText());
                    } else if (larger.isSelected()) {
                        controller.printByKey(new KeyCompReverse(), index.getSelectedIndex(), key.getText());
                    } else {
                        controller.printByKey(new KeyComp(), index.getSelectedIndex(), key.getText());
                    }
                } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
                    showError(e);
                }
            }
        }
    }

    void showData(List<Record> data) {
        list.setListData(data.toArray(new Record[0]));
    }

    void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    void update(String string) {
        status.setText(string);
    }
}
