import java.applet.Applet;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CircleDiagram extends Applet {
    private List<Slice> slices;
    private String title;
    private double scale;
    private String maxLen; //самая длинная метка (чтобы поместилась)

    @Override
    public void init() {
        super.init();
        setBackground(new Color(0xA0A0A0));
        title = getParameter("param_0");
        maxLen = "";
        String input;
        String[] pair;
        int value;
        int i = 1;
        slices = new ArrayList<>();
        while ((input = getParameter(String.format("param_%d", i++))) != null) {
            pair = input.split(" ", 2);//лимит 2 чтобы по остальным пробелам не делилось
            value = Integer.parseInt(pair[0]);
            slices.add(new Slice(pair[1], value,
                    new Color((int) (Math.random() * 0x1000000))));
            scale += value;
            if (pair[0].length() > maxLen.length()) {
                maxLen = pair[0];
            }
        }
        scale = 360.0 / scale;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        Font titleFont = new Font("Verdana", Font.BOLD, 18);
        Font labelFont = new Font("Verdana", Font.BOLD, 12);
// Вычисляем длину заголовка.
        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D titleBounds = titleFont.getStringBounds(title, context);
        int titleWidth = (int) titleBounds.getWidth();
        int titleHeight = (int) titleBounds.getHeight();
// Рисуем заголовок.
        int y = (int) (10 - titleBounds.getY()); //отступ сверху
        int x = (panelWidth - titleWidth) / 2; //отступ левого края от центра
        g2.setFont(titleFont);
        g2.drawString(title, x, y);
// Вычисляем размеры меток диаграммы.
        LineMetrics labelMetrics = labelFont.getLineMetrics(maxLen, context);
        int labelHeight = (int) labelMetrics.getHeight();
        int labelWidth = labelHeight * maxLen.length();
        int radius = Math.min(panelHeight - titleHeight - labelHeight * 2 - 60, panelWidth - labelWidth * 2 - 20) / 2;
        x = panelWidth / 2;
        y = (panelHeight - titleHeight - labelHeight - 20) / 2 + titleHeight + labelHeight;
        g2.setFont(labelFont);
// Рисуем сектора.
        double angle = 0;
        for (Slice slice : slices) {
// Заполняем и рисуем сектора.
            g2.setPaint(slice.color);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.fillArc(x - radius, y - radius, 2 * radius, 2 * radius, (int) Math.round(angle), (int) Math.ceil(slice.value * scale));
            g2.setPaint(Color.BLACK);
            g2.drawArc(x - radius, y - radius, 2 * radius, 2 * radius, (int) Math.round(angle), (int) Math.ceil(slice.value * scale));
// Рисуем метку.
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Rectangle2D curLabelBounds = labelFont.getStringBounds(slice.name, context);
            float curLabelWidth = (float) curLabelBounds.getWidth();
            //сноска по центру сектора от 0.9 радуиса до 1.1
            g2.drawLine(x + (int) Math.round(radius * 0.9 * Math.cos((angle + slice.value * scale / 2) * Math.PI / 180)),
                    y - (int) Math.round(radius * 0.9 * Math.sin((angle + slice.value * scale / 2) * Math.PI / 180)),
                    x + (int) Math.round(radius * 1.1 * Math.cos((angle + slice.value * scale / 2) * Math.PI / 180)),
                    y - (int) Math.round(radius * 1.1 * Math.sin((angle + slice.value * scale / 2) * Math.PI / 180)));
            //направление надписи (прилегает к сноске левым или правым боком)
            if ((angle + slice.value * scale / 2 > 90) && (angle + slice.value * scale / 2 <= 270)) {
                g2.drawString(slice.name, x - curLabelWidth - 3 + (int) Math.round(radius * 1.1 * Math.cos((angle + slice.value * scale / 2) * Math.PI / 180)),
                        y - (int) Math.round(radius * 1.1 * Math.sin((angle + slice.value * scale / 2) * Math.PI / 180)));
            } else {
                g2.drawString(slice.name, x + 3 + (int) Math.round(radius * 1.1 * Math.cos((angle + slice.value * scale / 2) * Math.PI / 180)),
                        y - (int) Math.round(radius * 1.1 * Math.sin((angle + slice.value * scale / 2) * Math.PI / 180)));
            }
            curLabelBounds = labelFont.getStringBounds(String.valueOf(slice.value), context);
            curLabelWidth = (float) curLabelBounds.getWidth();
            g2.drawString(String.valueOf(slice.value), x - curLabelWidth / 2 + (int) Math.round(radius * 0.7 * Math.cos((angle + slice.value * scale / 2) * Math.PI / 180)),
                    y - (int) Math.round(radius * 0.7 * Math.sin((angle + slice.value * scale / 2) * Math.PI / 180)));
// Увеличиваем угол.
            angle += slice.value * scale;
        }
    }

    private static class Slice {
        String name;
        int value;
        Color color;

        public Slice(String name, int value, Color color) {
            this.name = name;
            this.value = value;
            this.color = color;
        }
    }
}
