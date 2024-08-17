package eu.phaf4it.stored_retry.core;

import java.io.Serializable;
import java.util.function.Supplier;

public interface SerializableSupplier<T> extends Supplier<T>, Serializable, MethodReferenceReflection {
}
