package io.github.ppdzm.utils.universal.tuple;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Tuple3<T1, T2, T3> {
    public T1 f1;
    public T2 f2;
    public T3 f3;

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 v1, T2 v2, T3 v3) {
        return new Tuple3<>(v1, v2, v3);
    }

}
