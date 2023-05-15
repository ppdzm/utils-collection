package io.github.ppdzm.utils.universal.tuple;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Tuple1<T1> {
    public T1 f1;

    public static <T1> Tuple1<T1> of(T1 v1) {
        return new Tuple1<>(v1);
    }

}
