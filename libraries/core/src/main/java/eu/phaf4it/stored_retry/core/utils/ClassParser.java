package eu.phaf4it.stored_retry.core.utils;

public final class ClassParser {
    private ClassParser() {
        // utility class
    }

    /**
     * Parser for className which ensure that also primitives are correctly converted.
     * @param className to parse as a class
     * @return parsed class
     */
    public static Class<?> forName(final String className) {
        switch (className) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "char":
                return char.class;
            case "void":
                return void.class;
            default:
                String fqn = className.contains(".") ? className : "java.lang.".concat(className);
                try {
                    return Class.forName(fqn);
                } catch (ClassNotFoundException ex) {
                    throw new IllegalArgumentException("Class not found: " + fqn);
                }
        }
    }

}
