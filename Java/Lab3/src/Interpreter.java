import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    private static Pattern staticFunction = Pattern.compile("^((?:[A-Za-z]+\\.)*[A-Za-z]+)\\.([A-Za-z]+)[(](.*)[)];$");
    private static Pattern paramsPattern = Pattern.compile("^((\\S+)\\s*,\\s*)*(\\S+)?$");
    private static Pattern importPattern = Pattern.compile("^import\\s+((?:[A-Za-z]+\\.)*[A-Za-z]+)(\\.\\*)?;$");
    private static Class[] classes = {String.class, byte.class, Byte.class, short.class, Short.class,
            int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class};
    private List<String> searchPackages;
    private List<String> searchClasses;
    private PrintStream out;
    private PrintStream err;

    public Interpreter(PrintStream out, PrintStream err) {
        searchPackages = new ArrayList<>();
        searchPackages.add("java.lang");
        searchClasses = new ArrayList<>();
        this.out = out;
        this.err = err;
    }

    private static int pos(Class<?> objectClass) {
        for (int i = 0; i < classes.length; i++) {
            if (classes[i].equals(objectClass)) {
                return i;
            }
        }
        return -1;
    }

    private static Object invoke(Class<?> objectClass, String name, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method[] methods = Arrays.stream(objectClass.getDeclaredMethods()).filter(method -> method.getName().equals(name) && method.getParameterTypes().length == params.length).toArray(Method[]::new);
        Arrays.sort(methods, (o1, o2) -> {
            Class<?>[] l1 = o1.getParameterTypes(), l2 = o2.getParameterTypes();
            return Arrays.compare(l1, l2, Comparator.comparingInt(Interpreter::pos));
        });
        for (int i = 0; i < methods.length; i++) {
            if (matches(methods[i], params)) {
                return methods[i].invoke(null, params);
            }
        }
        throw new NoSuchMethodException(methodToString(objectClass, name, params));
    }

    private static String methodToString(Class<?> objectClass, String name, Object... params) {
        StringJoiner sj = new StringJoiner(", ", objectClass.getName() + "." + name + "(", ")");
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Class<?> c = params[i].getClass();
                sj.add((c == null) ? "null" : c.getName());
            }
        }
        return sj.toString();
    }

    private static boolean matches(Method method, Object... params) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != params.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(params[i].getClass())) {
                continue;
            }
            if (parameterTypes[i].equals(classes[0]) || params[i].getClass().equals(classes[0])) {
                return false;
            }
            if (pos(params[i].getClass()) - pos(parameterTypes[i]) > 1)
                return false;
        }
        return true;
    }

    private static Object parseParam(String param) {
        if (param.charAt(0) == '"' && param.charAt(param.length() - 1) == '"') {
            return param.substring(1, param.length() - 1);
        }
        try {
            return Byte.valueOf(param);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Short.valueOf(param);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Integer.valueOf(param);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Long.valueOf(param);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Float.valueOf(param);
        } catch (NumberFormatException ignored) {
        }
        return Double.valueOf(param);
    }

    private Class<?> findClassByName(String name) throws ClassNotFoundException {
        if (name.contains(".")) {
            return Class.forName(name);
        }
        for (String searchClass : searchClasses) {
            if (searchClass.endsWith("." + name)) {
                return Class.forName(searchClass);
            }
        }
        for (String searchPackage : searchPackages) {
            try {
                return Class.forName(searchPackage + "." + name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return Class.forName(name);
    }

    public void run(String program) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(program, "\r\n");
            while (tokenizer.hasMoreTokens()) {
                runLine(tokenizer.nextToken());
            }
        } catch (InvocationTargetException e) {
            err.println(e.getCause());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | RuntimeException e) {
            err.println(e);
        }
    }

    private void runLine(String line) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        Matcher matcher = staticFunction.matcher(line);
        if (matcher.matches()) {
            Class<?> objectClass = findClassByName(matcher.group(1));
            Matcher paramsMatcher = paramsPattern.matcher(matcher.group(3));
            if (paramsMatcher.matches()) {
                StringTokenizer tokenizer = new StringTokenizer(matcher.group(3), ", \t");
                List<Object> params = new ArrayList<>();
                while (tokenizer.hasMoreTokens()) {
                    try {
                        params.add(parseParam(tokenizer.nextToken()));
                    } catch (NumberFormatException e) {
                        throw new InvocationTargetException(new Exception("Invalid syntax: " + line));
                    }
                }
                out.println(invoke(objectClass, matcher.group(2), params.toArray()));
            } else {
                throw new InvocationTargetException(new Exception("Invalid syntax: " + line));
            }
        } else {
            Matcher importMatcher = importPattern.matcher(line);
            if (importMatcher.matches()) {
                if (importMatcher.group(2) == null) {
                    searchClasses.add(importMatcher.group(1));
                } else {
                    searchPackages.add(importMatcher.group(1));
                }
            } else {
                throw new InvocationTargetException(new Exception("Invalid syntax: " + line));
            }
        }
    }
}
