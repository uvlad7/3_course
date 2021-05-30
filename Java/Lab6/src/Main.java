import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame implements ActionListener, LineListener, ColorChooserButton.ColorChangedListener {
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem close;
    private PaintPanel paintPanel;
    private Color color;
    private int width;

    public Main(String title) {
        super(title);
        setIconImage(new ImageIcon("src\\Paint.png").getImage());
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        open = new JMenuItem("Open", new ImageIcon("src\\Open.png"));
        save = new JMenuItem("Save", new ImageIcon("src\\Save.png"));
        close = new JMenuItem("Exit", new ImageIcon("src\\Close.png"));
        file.add(open);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        file.add(save);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        file.addSeparator();
        file.add(close);
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        menuBar.add(file);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel controlsPanel = new JPanel(new BorderLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        paintPanel = new PaintPanel(1500, 900);
        paintPanel.setLayout(null);
        JScrollPane pane = new JScrollPane(paintPanel);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        add(pane, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.insets = new Insets(0, 3, 3, 3);
        constraints.gridy = 0;
        constraints.ipady = 20;

        color = new Color(0, 0, 0);
        width = 1;
        ColorChooserButton colorChooserButton = new ColorChooserButton(color);
        colorChooserButton.addColorChangedListener(this);
        controlsPanel.add(colorChooserButton, BorderLayout.WEST);
        JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, width);
        widthSlider.addChangeListener(e -> width = widthSlider.getValue());
        widthSlider.setMinorTickSpacing(1);
        // draw the tick marks
        widthSlider.setPaintTicks(true);
        widthSlider.setSnapToTicks(true);
        controlsPanel.add(widthSlider, BorderLayout.CENTER);
        paintPanel.addLineListener(this);
        add(controlsPanel, constraints);

        open.addActionListener(this);
        save.addActionListener(this);
        close.addActionListener(this);

        pack();
    }

    public static void main(String[] args) {
        Main frame = new Main("PaintLite");
        frame.setSize(1300, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int i = fileName.lastIndexOf(".");
        if (i != -1 && i != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    @Override
    public void draw(LineEvent e) {
        if (e.getSource() == paintPanel) {
            paintPanel.clearTemp();
            Graphics2D g2 = (Graphics2D) (e.isTemp() ? paintPanel.getTempImage().getGraphics() : paintPanel.getBuffer().getGraphics());
            g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
            g2.drawLine(e.getX1(), e.getY1(), e.getX2(), e.getY2());
            paintPanel.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == open) {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image file", ImageIO.getReaderFormatNames()));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage bufImage = ImageIO.read(fileChooser.getSelectedFile());
                    paintPanel.loadImage(bufImage);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex, ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == save) {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image file", ImageIO.getWriterFormatNames()));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(paintPanel.getBuffer(), Main.getFileExtension(file), file);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex, ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == close) {
            setVisible(false);
            dispose();
        }
    }

    @Override
    public void colorChanged(Color newColor) {
        color = newColor;
    }
}