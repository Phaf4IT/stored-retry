package eu.phaf4it.stored_retry.core;

import java.io.Serializable;
import java.util.function.Consumer;

public interface SerializableConsumer<T> extends Consumer<T>, Serializable, MethodReferenceReflection {

    static <A> SerializableConsumer<A> of(SerializableConsumer<A> consumer) {
        return consumer;
    }

    static <A, B> SerializableConsumer2<A, B> of(SerializableConsumer2<A, B> consumer) {
        return consumer;
    }

    static <A, B, C> SerializableConsumer3<A, B, C> of(SerializableConsumer3<A, B, C> consumer) {
        return consumer;
    }

    static <A, B, C, D> SerializableConsumer4<A, B, C, D> of(SerializableConsumer4<A, B, C, D> consumer) {
        return consumer;
    }

    static <A, B, C, D, E> SerializableConsumer5<A, B, C, D, E> of(SerializableConsumer5<A, B, C, D, E> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F> SerializableConsumer6<A, B, C, D, E, F> of(SerializableConsumer6<A, B, C, D, E, F> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G> SerializableConsumer7<A, B, C, D, E, F, G> of(SerializableConsumer7<A, B, C, D, E, F, G> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H> SerializableConsumer8<A, B, C, D, E, F, G, H> of(SerializableConsumer8<A, B, C, D, E, F, G, H> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I> SerializableConsumer9<A, B, C, D, E, F, G, H, I>
    of(SerializableConsumer9<A, B, C, D, E, F, G, H, I> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I, J> SerializableConsumer10<A, B, C, D, E, F, G, H, I, J>
    of(SerializableConsumer10<A, B, C, D, E, F, G, H, I, J> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I, J, K> SerializableConsumer11<A, B, C, D, E, F, G, H, I, J, K>
    of(SerializableConsumer11<A, B, C, D, E, F, G, H, I, J, K> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L> SerializableConsumer12<A, B, C, D, E, F, G, H, I, J, K, L>
    of(SerializableConsumer12<A, B, C, D, E, F, G, H, I, J, K, L> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, M> SerializableConsumer13<A, B, C, D, E, F, G, H, I, J, K, L, M>
    of(SerializableConsumer13<A, B, C, D, E, F, G, H, I, J, K, L, M> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N> SerializableConsumer14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>
    of(SerializableConsumer14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> consumer) {
        return consumer;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> SerializableConsumer15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
    of(SerializableConsumer15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> consumer) {
        return consumer;
    }

    @FunctionalInterface
    interface SerializableConsumer2<A, B> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b);
    }

    @FunctionalInterface
    interface SerializableConsumer3<A, B, C> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c);
    }

    @FunctionalInterface
    interface SerializableConsumer4<A, B, C, D> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d);
    }

    @FunctionalInterface
    interface SerializableConsumer5<A, B, C, D, E> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e);
    }

    @FunctionalInterface
    interface SerializableConsumer6<A, B, C, D, E, F> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f);
    }

    @FunctionalInterface
    interface SerializableConsumer7<A, B, C, D, E, F, G> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g);
    }

    @FunctionalInterface
    interface SerializableConsumer8<A, B, C, D, E, F, G, H> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h);
    }

    @FunctionalInterface
    interface SerializableConsumer9<A, B, C, D, E, F, G, H, I> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);
    }

    @FunctionalInterface
    interface SerializableConsumer10<A, B, C, D, E, F, G, H, I, J> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);
    }

    @FunctionalInterface
    interface SerializableConsumer11<A, B, C, D, E, F, G, H, I, J, K> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k);
    }

    @FunctionalInterface
    interface SerializableConsumer12<A, B, C, D, E, F, G, H, I, J, K, L> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l);
    }

    @FunctionalInterface
    interface SerializableConsumer13<A, B, C, D, E, F, G, H, I, J, K, L, M> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m);
    }

    @FunctionalInterface
    interface SerializableConsumer14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n);
    }

    @FunctionalInterface
    interface SerializableConsumer15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> extends Serializable, MethodReferenceReflection {
        void apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o);
    }


}
