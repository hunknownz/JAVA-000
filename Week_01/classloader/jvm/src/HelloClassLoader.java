import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class HelloClassLoader extends ClassLoader {

    private byte[] bytes = {};

    public static void main(String[] args) {
        var helloClassLoader = new HelloClassLoader();

        try {
            helloClassLoader.readFile("Hello.xlass");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            var helloClass = helloClassLoader.findClass("Hello");
            var helloObj = helloClass.newInstance();
            try {
                var helloMethod = helloClass.getMethod("hello");
                helloMethod.invoke(helloObj);
            } catch (NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        var decodeBytes = decode();
        return defineClass(name, decodeBytes, 0, decodeBytes.length);
    }

    private void readFile(String path) throws IOException {
        try (InputStream input = new FileInputStream(path)) {
            bytes = input.readAllBytes();
        }
    }

    private byte[] decode() {
        var newBytes = new byte[bytes.length];
        for (int i=0; i<bytes.length; i++) {
            newBytes[i] = (byte)(255 - bytes[i]);
        }
        return newBytes;
    }
}
