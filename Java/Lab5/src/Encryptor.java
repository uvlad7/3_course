import java.math.BigInteger;

public class Encryptor {
    private static int CHAR_SIZE = 65537;
    private static int a = 100;
    private static int b = 233;
    public static String encrypt(String message) {
        char[] encryptedMessage = message.toCharArray();
        for (int i = 0; i < message.length(); i++) {
            encryptedMessage[i] = (char) ((a * encryptedMessage[i] + b) % CHAR_SIZE);
        }
        return new String(encryptedMessage);
    }

    public static String decrypt(String encryptedMessage) {
        char[] decryptedMessage = encryptedMessage.toCharArray();
        int a1 =  BigInteger.valueOf(a).modInverse(BigInteger.valueOf(CHAR_SIZE)).intValue();
        for (int i = 0; i < encryptedMessage.length(); i++) {
            decryptedMessage[i] = (char) ((a1 * (decryptedMessage[i] - b)) % CHAR_SIZE);
        }
        return new String(decryptedMessage);
    }
}
