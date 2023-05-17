package io.github.ppdzm.utils.universal.tuple;

import lombok.AllArgsConstructor;

/**
 * @param <T1>
 * @param <T2>
 * @author Created by Stuart Alex on 2023/5/13.
 */
@AllArgsConstructor
public class Tuple2<T1, T2> {
    public T1 f1;
    public T2 f2;

    public static <T1, T2> Tuple2<T1, T2> of(T1 v1, T2 v2) {
        return new Tuple2<>(v1, v2);
    }

}
