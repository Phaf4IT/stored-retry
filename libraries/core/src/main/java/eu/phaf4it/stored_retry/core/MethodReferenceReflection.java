package eu.phaf4it.stored_retry.core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface MethodReferenceReflection {
    default SerializedLambda serialized() {
        return GetSerializedLambda.get(this);
    }

    default Class<?> getContainingClass() {
        try {
            String className = serialized().getImplClass().replaceAll("/", ".");
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default Method method() {
        SerializedLambda lambda = serialized();
        Class<?> containingClass = getContainingClass();
        return Arrays.stream(containingClass.getDeclaredMethods())
                .filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
                .filter(method -> MethodArgumentTypeExtractor.getMethodArgumentTypes(method).equals(MethodSignatureParser.parse(lambda.getImplMethodSignature())))
                .findFirst()
                .orElseThrow(UnableToGuessMethodException::new);
    }

    class MethodSignatureParser {

        public static List<String> parse(String methodSignature) {
            return parseType(methodSignature.substring(methodSignature.indexOf("(") + 1, methodSignature.indexOf(")")), new ArrayList<>());
        }

        public static List<String> parseType(String methodSignature, List<String> methodArguments) {
            char currentChar = methodSignature.charAt(0);
            if (currentChar == 'L') {
                int endOfObject = methodSignature.indexOf(';') + 1;
                methodArguments.add(parseObjectType(methodSignature.substring(0, endOfObject)));
                if (methodSignature.substring(endOfObject).isEmpty()) {
                    return methodArguments;
                } else {
                    return parseType(methodSignature.substring(endOfObject), methodArguments);
                }
            } else if (currentChar == '[') {
                int arrayDimensions = getTotalArrayDimension(methodSignature);
                int nextCharIndex = arrayDimensions - 1;
                if (methodSignature.charAt(nextCharIndex) == 'L') {
                    int semicolonIndex = methodSignature.indexOf(';') + 1;
                    methodArguments.add(parseArrayType(arrayDimensions, methodSignature.substring(nextCharIndex, semicolonIndex)));
                    if (methodSignature.substring(semicolonIndex).isEmpty()) {
                        return methodArguments;
                    } else {
                        return parseType(methodSignature.substring(semicolonIndex), methodArguments);
                    }
                } else {
                    methodArguments.add(parseArrayType(arrayDimensions, methodSignature.substring(nextCharIndex)));
                    if (methodSignature.substring(nextCharIndex + 1).isEmpty()) {
                        return methodArguments;
                    } else {
                        return parseType(methodSignature.substring(nextCharIndex + 1), methodArguments);
                    }
                }
            } else {
                methodArguments.add(parsePrimitiveType(currentChar));
                if (methodSignature.substring(1).isEmpty()) {
                    return methodArguments;
                } else {
                    return parseType(methodSignature.substring(1), methodArguments);
                }
            }
        }

        public static int getTotalArrayDimension(String methodSignature) {
            int index = 0;
            while (methodSignature.charAt(index) == '[') {
                index++;
            }
            return index;
        }

        private static String parseObjectType(String objectSignature) {
            return objectSignature.substring(1, objectSignature.length() - 1).replace('/', '.');
        }

        private static String parseArrayType(int totalArrayDimensions, String arraySignature) {
            String baseType = parseObjectType(arraySignature);
            return baseType + "[]".repeat(Math.max(0, totalArrayDimensions));
        }

        private static String parsePrimitiveType(char primitiveChar) {
            return switch (primitiveChar) {
                case 'Z' -> "boolean";
                case 'B' -> "byte";
                case 'C' -> "char";
                case 'S' -> "short";
                case 'I' -> "int";
                case 'J' -> "long";
                case 'F' -> "float";
                case 'D' -> "double";
                default -> throw new IllegalArgumentException("Unknown primitive type: " + primitiveChar);
            };
        }
    }

    public class MethodArgumentTypeExtractor {

        public static List<String> getMethodArgumentTypes(Method method) {
            List<String> argumentTypes = new ArrayList<>();

            for (Class<?> parameterType : method.getParameterTypes()) {
                argumentTypes.add(getFullTypeName(parameterType));
            }

            return argumentTypes;
        }

        private static String getFullTypeName(Class<?> clazz) {
            if (clazz.isArray()) {
                return clazz.getComponentType().getName() + "[]";
            } else {
                return clazz.getName();
            }
        }
    }

    class UnableToGuessMethodException extends RuntimeException {
    }

    class GetSerializedLambda extends ObjectOutputStream {

        private SerializedLambda info;

        GetSerializedLambda() throws IOException {
            super(OutputStream.nullOutputStream());
            super.enableReplaceObject(true);
        }

        @Override
        protected Object replaceObject(Object obj) {
            if (obj instanceof SerializedLambda) {
                info = (SerializedLambda) obj;
                obj = null;
            }
            return obj;
        }

        public static SerializedLambda get(Object obj) {
            try {
                GetSerializedLambda getter = new GetSerializedLambda();
                getter.writeObject(obj);
                return getter.info;
            } catch (IOException ex) {
                throw new IllegalArgumentException("not a serializable lambda", ex);
            }
        }
    }

}
