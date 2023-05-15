package io.github.ppdzm.utils.universal.feature;

import java.util.function.Function;

/**
 * @author Created by Stuart Alex on 2023/5/13.
 */
public class LoanPattern {

    public static <T extends AutoCloseable, R> R using(T resource, Function<T, R> block) throws Exception {
        try {
            return block.apply(resource);
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

}
