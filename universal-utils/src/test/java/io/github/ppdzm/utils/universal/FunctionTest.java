package io.github.ppdzm.utils.universal;

import io.github.ppdzm.utils.universal.base.Mathematics;
import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.feature.Compiler;
import io.github.ppdzm.utils.universal.feature.Retry;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class FunctionTest {

    @Test
    public void retryTest1() {
        int r = Retry.retry(5, 1000, () -> {
            int x = Mathematics.randomInt(1, 10);
            System.out.println(x);
            return x;
        }, e -> e == 10, 0, true);
        System.out.println(StringUtils.repeat('=', 10));
        System.out.println(r);
    }

    @Test
    public void retryTest2() {
        Integer retry = Retry
                .<Integer, Integer>retry()
                .times(5)
                .interval(1000)
                .defaultResult(5)
                .defaultValue(5)
                .ignoreInvalidResult(true)
                .retry(() -> {
                    int x = Mathematics.randomInt(1, 10);
                    System.out.println(x);
                    return x;
                })
                .until(e -> e == 10)
                .process(e -> e * e)
                .applyWithProcessor();
        System.out.println(StringUtils.repeat('=', 10));
        System.out.println(retry);
    }

}
