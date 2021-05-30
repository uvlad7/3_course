import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Connector {
    public Connector() {
    }

    public static void write(File dest, List<? extends Entity>... entities) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(dest));
        for (List<? extends Entity> list : entities) {
            for (Entity entity : list) {
                stream.writeObject(entity);
            }
        }
        stream.close();
    }

    public static List<Entity> read(File source) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(source));
        List<Entity> list = new ArrayList<>();
        try {
            while (true) {
                list.add((Entity) stream.readObject());
            }
        } catch (EOFException ignored) {
        }
        stream.close();
        return list;
    }
}
