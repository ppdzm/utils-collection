package io.github.ppdzm.utils.universal;

import io.github.ppdzm.utils.universal.base.Mathematics;
import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.feature.Retry;
import org.junit.Test;

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
                .<Integer>retry()
                .times(5)
                .interval(1000)
                .ignoreInvalidResult(true)
                .retry(() -> {
                    int x = Mathematics.randomInt(1, 10);
                    System.out.println(x);
                    return x;
                })
                .until(e -> e == 10)
                .apply();
        System.out.println(StringUtils.repeat('=', 10));
        System.out.println(retry);
    }

}
