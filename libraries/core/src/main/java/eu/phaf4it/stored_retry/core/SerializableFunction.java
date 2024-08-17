package eu.phaf4it.stored_retry.core;

import java.io.Serializable;
import java.util.function.Function;

public interface SerializableFunction<T, R> extends Function<T, R>, Serializable, MethodReferenceReflection {

    static <A, R> SerializableFunction<A, R> of(
            SerializableFunction<A, R> function) {
        return function;
    }

    static <A, B, R> SerializableFunction2<A, B, R> of(
            SerializableFunction2<A, B, R> function) {
        return function;
    }

    static <A, B, C, R> SerializableFunction3<A, B, C, R> of(
            SerializableFunction3<A, B, C, R> function) {
        return function;
    }

    static <A, B, C, D, R> SerializableFunction4<A, B, C, D, R> of(
            SerializableFunction4<A, B, C, D, R> function) {
        return function;
    }

    static <A, B, C, D, E, R> SerializableFunction5<A, B, C, D, E, R> of(

            SerializableFunction5<A, B, C, D, E, R> function
    ) {
        return function;
    }

    static <A, B, C, D, E, F, R> SerializableFunction6<A, B, C, D, E, F, R> of(

            SerializableFunction6<A, B, C, D, E, F, R> function
    ) {
        return function;
    }

    static <A, B, C, D, E, F, G, R> SerializableFunction7<A, B, C, D, E, F, G, R> of(

            SerializableFunction7<A, B, C, D, E, F, G, R> function
    ) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, R> SerializableFunction8<A, B, C, D, E, F, G, H, R> of(
            SerializableFunction8<A, B, C, D, E, F, G, H, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, R> SerializableFunction9<A, B, C, D, E, F, G, H, I, R> of(
            SerializableFunction9<A, B, C, D, E, F, G, H, I, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, J, R> SerializableFunction10<A, B, C, D, E, F, G, H, I, J, R> of(
            SerializableFunction10<A, B, C, D, E, F, G, H, I, J, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, R> SerializableFunction11<A, B, C, D, E, F, G, H, I, J, K, R> of(
            SerializableFunction11<A, B, C, D, E, F, G, H, I, J, K, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, R> SerializableFunction12<A, B, C, D, E, F, G, H, I, J, K, L, R> of(
            SerializableFunction12<A, B, C, D, E, F, G, H, I, J, K, L, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, M, R> SerializableFunction13<A, B, C, D, E, F, G, H, I, J, K, L, M, R> of(
            SerializableFunction13<A, B, C, D, E, F, G, H, I, J, K, L, M, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> SerializableFunction14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> of(
            SerializableFunction14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> function) {
        return function;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> SerializableFunction15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> of(
            SerializableFunction15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> function) {
        return function;
    }

    @FunctionalInterface
    interface SerializableFunction2<A, B, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b);
    }

    @FunctionalInterface
    interface SerializableFunction3<A, B, C, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c);
    }

    @FunctionalInterface
    interface SerializableFunction4<A, B, C, D, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d);
    }

    @FunctionalInterface
    interface SerializableFunction5<A, B, C, D, E, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e);
    }

    @FunctionalInterface
    interface SerializableFunction6<A, B, C, D, E, F, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f);
    }

    @FunctionalInterface
    interface SerializableFunction7<A, B, C, D, E, F, G, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g);
    }

    @FunctionalInterface
    interface SerializableFunction8<A, B, C, D, E, F, G, H, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h);
    }

    @FunctionalInterface
    interface SerializableFunction9<A, B, C, D, E, F, G, H, I, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);
    }

    @FunctionalInterface
    interface SerializableFunction10<A, B, C, D, E, F, G, H, I, J, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);
    }

    @FunctionalInterface
    interface SerializableFunction11<A, B, C, D, E, F, G, H, I, J, K, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k);
    }

    @FunctionalInterface
    interface SerializableFunction12<A, B, C, D, E, F, G, H, I, J, K, L, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l);
    }

    @FunctionalInterface
    interface SerializableFunction13<A, B, C, D, E, F, G, H, I, J, K, L, M, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m);
    }

    @FunctionalInterface
    interface SerializableFunction14<A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n);
    }

    @FunctionalInterface
    interface SerializableFunction15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, R> extends Serializable, MethodReferenceReflection {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o);
    }
}
