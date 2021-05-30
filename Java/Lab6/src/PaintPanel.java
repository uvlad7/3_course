import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class PaintPanel extends JPanel implements MouseListener, MouseMotionListener {
    private BufferedImage bufferedImage;
    private BufferedImage tempImage;
    private List<LineListener> listeners;
    private int xOld;
    private int yOld;

    public PaintPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        listeners = new LinkedList<>();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addLineListener(LineListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(LineEvent event) {
        for (LineListener listener : listeners) {
            listener.draw(event);
        }
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(dimension);
        bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, dimension.width, dimension.height);
        tempImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        graphics = (Graphics2D) tempImage.getGraphics();
        graphics.setBackground(new Color(255, 255, 255, 0));
        graphics.clearRect(0, 0, dimension.width, dimension.height);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.drawImage(tempImage, 0, 0, null);
    }

    public BufferedImage getBuffer() {
        return bufferedImage;
    }

    public BufferedImage getTempImage() {
        return tempImage;
    }

    public void clearTemp() {
        Graphics2D graphics = (Graphics2D) tempImage.getGraphics();
        graphics.setBackground(new Color(255, 255, 255, 0));
        graphics.clearRect(0, 0, getWidth(), getHeight());
    }

    public void loadImage(BufferedImage buf) {
        bufferedImage.createGraphics().setColor(Color.WHITE);
        bufferedImage.createGraphics().fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
        bufferedImage.createGraphics().drawImage(buf, 0, 0, null);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == this) {
            xOld = e.getX();
            yOld = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == this) {
            notifyListeners(new LineEvent(this, xOld, yOld, e.getX(), e.getY(), false));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == this) {
            notifyListeners(new LineEvent(this, xOld, yOld, e.getX(), e.getY(), true));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
