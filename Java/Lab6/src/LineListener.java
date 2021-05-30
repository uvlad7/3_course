import java.util.EventListener;

public interface LineListener extends EventListener {
    void draw(LineEvent lineEvent);
}