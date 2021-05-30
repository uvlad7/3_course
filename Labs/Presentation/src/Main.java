import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            File dir = new File("output");
            if (dir.exists() || dir.mkdirs()) {
                Pattern pattern = Pattern.compile("data=\"(.+?)\"");
                List<String> strings = new ArrayList<>();
                String string = new String(Files.readAllBytes(Paths.get("input2.html")));
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    strings.add(matcher.group(1));
                }
                byte[] data;
                File file;
                for (int i = 0; i < strings.size(); i++) {
                    //convert base64 string to binary data
                    data = DatatypeConverter.parseBase64Binary(strings.get(i));
                    file = new File("output\\" + i + ".jpg");
                    if (file.exists() || (file.createNewFile())) {
                        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                            outputStream.write(data);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}