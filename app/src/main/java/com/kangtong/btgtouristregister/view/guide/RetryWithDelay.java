package com.kangtong.btgtouristregister.view.guide;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RetryWithDelay implements Function<Flowable<Throwable>, Publisher<?>> {

    // 重试参数
    private final int maxRetryCount = 8;
    private final int retryDelayMillis = 200;
    private int retryCount = 0;

    @Override
    public Publisher<?> apply(Flowable<Throwable> throwableFlow) {
        // 重试
        return throwableFlow.flatMap((Function<Throwable, Publisher<?>>) throwable -> {
            retryCount++;
            // 继续重试
            if (retryCount <= maxRetryCount) {
                return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
            }
            // 返回错误
            else {
                return Flowable.error(throwable);
            }

        });
    }
}