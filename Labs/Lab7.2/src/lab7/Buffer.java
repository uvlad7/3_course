package lab7;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Buffer {
    static final String zipEntryName = "z";

    static byte[] toByteArray(Serializable obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(obj);
        }
        return out.toByteArray();

    }

    static byte[] toZipByteArray(Serializable obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            zos.putNextEntry(new ZipEntry(zipEntryName));
            zos.setLevel(ZipOutputStream.DEFLATED);
            try (ObjectOutputStream oos = new ObjectOutputStream(zos)) {
                oos.writeObject(obj);
                zos.closeEntry();
            }
        }
        return out.toByteArray();
    }

    static Object fromByteArray(byte[] arr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(arr);
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            return ois.readObject();
        }
    }

    static Object fromZipByteArray(byte[] arr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(arr);
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry zen = zis.getNextEntry();
            if (!zen.getName().equals(zipEntryName)) {
                throw new IOException("Invalid block format");
            }
            try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                return ois.readObject();
            }
        }
    }

    public static long writeObject(RandomAccessFile file, Serializable obj, Boolean zipped) throws IOException {
        long result = file.length();
        file.seek(result);
        byte[] what;
        if (zipped) {
            what = toZipByteArray(obj);
            file.writeByte(1);
        } else {
            what = toByteArray(obj);
            file.writeByte(0);
        }
        file.writeInt(what.length);
        file.write(what);
        file.setLength(file.getFilePointer());
        return result;
    }

    public static Object readObject(RandomAccessFile file, long position, boolean[] wasZipped) throws IOException, ClassNotFoundException {
        file.seek(position);
        byte zipped = file.readByte();
        int length = file.readInt();
        byte[] what = new byte[length];
        file.read(what);
        if (wasZipped != null) {
            wasZipped[0] = (zipped != 0);
        }
        if (zipped == 0) {
            return fromByteArray(what);
        } else if (zipped == 1) {
            return fromZipByteArray(what);
        } else {
            throw new IOException("Invalid block format");
        }
    }
}