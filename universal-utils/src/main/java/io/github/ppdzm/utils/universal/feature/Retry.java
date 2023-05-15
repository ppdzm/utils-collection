package io.github.ppdzm.utils.universal.feature;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Created by Stuart Alex on 2023/5/13.
 */
public class Retry<R> {
    private int retryTimes;
    private int interval;
    private Supplier<R> supplier;
    private Function<R, Boolean> validator;
    private R defaultValue;
    private boolean ignoreInvalidResult;

    /**
     * 构造函数
     */
    public Retry() {
        this.retryTimes = 3;
        this.interval = 1000;
        this.supplier = null;
        this.validator = null;
        this.defaultValue = null;
        this.ignoreInvalidResult = false;
    }

    /**
     * 静态构造函数
     *
     * @param <R> 泛型
     * @return Retry<R>
     */
    public static <R> Retry<R> retry() {
        return new Retry<>();
    }

    /**
     * 设置重试次数
     *
     * @param retryTimes 重试次数
     * @return Retry<R>
     */
    public Retry<R> times(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    /**
     * 设置重试间隔
     *
     * @param interval 重试间隔
     * @return Retry<R>
     */
    public Retry<R> interval(int interval) {
        this.interval = interval;
        return this;
    }

    /**
     * 设置默认返回值
     *
     * @param defaultValue 默认返回值
     * @return Retry<R>
     */
    public Retry<R> defaultValue(R defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Retry<R> ignoreInvalidResult(boolean ignoreInvalidResult) {
        this.ignoreInvalidResult = ignoreInvalidResult;
        return this;
    }

    /**
     * 设置要重试执行的代码
     *
     * @param supplier 要重试执行的代码
     * @return Retry<R>
     */
    public Retry<R> retry(Supplier<R> supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * 设置结果校验器
     *
     * @param validator 结果校验器
     * @return Retry<R>
     */
    public Retry<R> until(Function<R, Boolean> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * 执行
     *
     * @return R
     */
    public R apply() {
        return Retry.retry(this.retryTimes, this.interval, this.supplier, this.validator, this.defaultValue, this.ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param supplier   要执行的代码
     * @param <R>        返回值类型
     * @return R
     */
    public static <R> R retry(int retryTimes, Supplier<R> supplier) {
        return retry(retryTimes, 0, supplier, null);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param interval   重试间隔
     * @param supplier   要执行的代码
     * @param <R>        返回值类型
     * @return R
     */
    public static <R> R retry(int retryTimes, int interval, Supplier<R> supplier) {
        return retry(retryTimes, interval, supplier, null);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes   重试次数
     * @param interval     重试间隔
     * @param supplier     要执行的代码
     * @param defaultValue 默认值
     * @param <R>          返回值类型
     * @return R
     */
    public static <R> R retry(int retryTimes, int interval, Supplier<R> supplier, R defaultValue) {
        int tryTimes = 0;
        while (tryTimes < retryTimes) {
            try {
                return supplier.get();
            } catch (Exception e) {
                tryTimes++;
                if (interval > 0) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return defaultValue;
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param supplier   要执行的代码
     * @param validator  验证每次执行结果是否满足期望
     * @param <R>        返回值类型
     * @return R
     */
    public static <R> R retry(int retryTimes, Supplier<R> supplier, Function<R, Boolean> validator, boolean ignoreInvalidResult) {
        return retry(retryTimes, 0, supplier, validator, null, ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param interval   重试间隔
     * @param supplier   要执行的代码
     * @param validator  验证每次执行结果是否满足期望
     * @param <R>        返回值类型
     * @return R
     */
    public static <R> R retry(int retryTimes, int interval, Supplier<R> supplier, Function<R, Boolean> validator, boolean ignoreInvalidResult) {
        return retry(retryTimes, interval, supplier, validator, null, ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes   重试次数
     * @param interval     重试间隔
     * @param supplier     要执行的代码
     * @param validator    验证每次执行结果是否满足期望
     * @param defaultValue 默认返回值
     * @param <R>          返回值类型
     * @return R
     */
    public static <R> R retry(int retryTimes, int interval, Supplier<R> supplier, Function<R, Boolean> validator, R defaultValue, boolean ignoreInvalidResult) {
        int tryTimes = 0;
        R result = null;
        while (tryTimes < retryTimes) {
            try {
                result = supplier.get();
                if (validator == null) {
                    return result;
                }
                if (validator.apply(result)) {
                    return result;
                } else {
                    tryTimes++;
                    if (interval > 0) {
                        try {
                            Thread.sleep(interval);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } catch (Exception e) {
                tryTimes++;
                if (interval > 0) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        if (result != null && !ignoreInvalidResult)
            return result;
        return defaultValue;
    }

}
