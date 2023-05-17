package io.github.ppdzm.utils.universal.feature;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Created by Stuart Alex on 2023/5/13.
 */
public class Retry<T, R> {
    private int retryTimes;
    private int interval;
    private Supplier<T> supplier;
    private Function<T, Boolean> validator;
    private Function<T, R> processor;
    private T defaultValue;
    private R defaultResult;
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
        this.defaultResult = null;
        this.ignoreInvalidResult = false;
    }

    /**
     * 静态构造函数
     *
     * @param <R> 泛型
     * @return Retry
     */
    public static <T, R> Retry<T, R> retry() {
        return new Retry<>();
    }

    /**
     * 设置重试次数
     *
     * @param retryTimes 重试次数
     * @return Retry
     */
    public Retry<T, R> times(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    /**
     * 设置重试间隔
     *
     * @param interval 重试间隔
     * @return Retry
     */
    public Retry<T, R> interval(int interval) {
        this.interval = interval;
        return this;
    }

    /**
     * 设置默认返回值
     *
     * @param defaultValue 默认返回值
     * @return Retry
     */
    public Retry<T, R> defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * 设置默认返回值
     *
     * @param defaultResult 默认返回值
     * @return Retry
     */
    public Retry<T, R> defaultResult(R defaultResult) {
        this.defaultResult = defaultResult;
        return this;
    }

    /**
     * 是否忽略无效结果
     *
     * @param ignoreInvalidResult 是否忽略无效结果
     * @return Retry
     */
    public Retry<T, R> ignoreInvalidResult(boolean ignoreInvalidResult) {
        this.ignoreInvalidResult = ignoreInvalidResult;
        return this;
    }

    /**
     * 设置要重试执行的代码
     *
     * @param supplier 要重试执行的代码
     * @return Retry
     */
    public Retry<T, R> retry(Supplier<T> supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * 设置结果校验器
     *
     * @param validator 结果校验器
     * @return Retry
     */
    public Retry<T, R> until(Function<T, Boolean> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * 设置结果校验器
     *
     * @param processor 结果处理器
     * @return Retry
     */
    public Retry<T, R> process(Function<T, R> processor) {
        this.processor = processor;
        return this;
    }

    /**
     * 执行
     *
     * @return R
     */
    public T apply() {
        return Retry.retry(this.retryTimes, this.interval, this.supplier, this.validator, this.defaultValue, this.ignoreInvalidResult);
    }

    /**
     * 执行
     *
     * @return R
     */
    public R applyWithProcessor() {
        return Retry.retry(this.retryTimes, this.interval, this.supplier, this.validator, this.processor, this.defaultResult, this.ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param supplier   要执行的代码
     * @param <T>        返回值类型
     * @return T
     */
    public static <T> T retry(int retryTimes, Supplier<T> supplier) {
        return retry(retryTimes, 0, supplier, (T) null);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param interval   重试间隔
     * @param supplier   要执行的代码
     * @param <T>        返回值类型
     * @return T
     */
    public static <T> T retry(int retryTimes, int interval, Supplier<T> supplier) {
        return retry(retryTimes, interval, supplier, (T) null);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes   重试次数
     * @param interval     重试间隔
     * @param supplier     要执行的代码
     * @param defaultValue 默认值
     * @param <T>          返回值类型
     * @return T
     */
    public static <T> T retry(int retryTimes, int interval, Supplier<T> supplier, T defaultValue) {
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
     * @param <T>        返回值类型
     * @return T
     */
    public static <T> T retry(int retryTimes, Supplier<T> supplier, Function<T, Boolean> validator, boolean ignoreInvalidResult) {
        return retry(retryTimes, 0, supplier, validator, (T) null, ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param interval   重试间隔
     * @param supplier   要执行的代码
     * @param validator  验证每次执行结果是否满足期望
     * @param <T>        返回值类型
     * @return T
     */
    public static <T> T retry(int retryTimes, int interval, Supplier<T> supplier, Function<T, Boolean> validator, boolean ignoreInvalidResult) {
        return retry(retryTimes, interval, supplier, validator, (T) null, ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes   重试次数
     * @param interval     重试间隔
     * @param supplier     要执行的代码
     * @param validator    验证每次执行结果是否满足期望
     * @param defaultValue 默认返回值
     * @param <T>          返回值类型
     * @return T
     */
    public static <T> T retry(int retryTimes, int interval, Supplier<T> supplier, Function<T, Boolean> validator, T defaultValue, boolean ignoreInvalidResult) {
        int tryTimes = 0;
        T result = null;
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
        if (result != null && !ignoreInvalidResult) {
            return result;
        }
        return defaultValue;
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes 重试次数
     * @param supplier   要执行的代码
     * @param <R>        返回值类型
     * @return R
     */
    public static <T, R> R retry(int retryTimes, Supplier<T> supplier, Function<T, R> processor) {
        return retry(retryTimes, 0, supplier, processor, null);
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
    public static <T, R> R retry(int retryTimes, int interval, Supplier<T> supplier, Function<T, R> processor) {
        return retry(retryTimes, interval, supplier, processor, null);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes    重试次数
     * @param interval      重试间隔
     * @param supplier      要执行的代码
     * @param defaultResult 默认值
     * @param <R>           返回值类型
     * @return R
     */
    public static <T, R> R retry(int retryTimes, int interval, Supplier<T> supplier, Function<T, R> processor, R defaultResult) {
        int tryTimes = 0;
        while (tryTimes < retryTimes) {
            try {
                return processor.apply(supplier.get());
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
        return defaultResult;
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
    public static <T, R> R retry(int retryTimes, Supplier<T> supplier, Function<T, Boolean> validator, Function<T, R> processor, boolean ignoreInvalidResult) {
        return retry(retryTimes, 0, supplier, validator, processor, ignoreInvalidResult);
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
    public static <T, R> R retry(int retryTimes, int interval, Supplier<T> supplier, Function<T, Boolean> validator, Function<T, R> processor, boolean ignoreInvalidResult) {
        return retry(retryTimes, interval, supplier, validator, processor, null, ignoreInvalidResult);
    }

    /**
     * 重试执行代码
     *
     * @param retryTimes    重试次数
     * @param interval      重试间隔
     * @param supplier      要执行的代码
     * @param validator     验证每次执行结果是否满足期望
     * @param defaultResult 默认返回值
     * @param <R>           返回值类型
     * @return R
     */
    public static <T, R> R retry(int retryTimes, int interval, Supplier<T> supplier, Function<T, Boolean> validator, Function<T, R> processor, R defaultResult, boolean ignoreInvalidResult) {
        int tryTimes = 0;
        T result = null;
        while (tryTimes < retryTimes) {
            try {
                result = supplier.get();
                if (validator == null) {
                    return processor.apply(result);
                }
                if (validator.apply(result)) {
                    return processor.apply(result);
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
        if (result != null && !ignoreInvalidResult) {
            return processor.apply(result);
        }
        return defaultResult;
    }

}
