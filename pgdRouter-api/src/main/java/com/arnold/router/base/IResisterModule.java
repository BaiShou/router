package com.arnold.router.base;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

/**
 * 模块化的注册管理
 * <p>
 * Created by waylenw on 2020/5/25.
 */
@Keep
public interface IResisterModule {

    void onCreate(@NonNull BaseBizRouter appBizRouter, @NonNull BaseSpiServiceLoader appSpiServiceLoader);

    void onStop(@NonNull BaseBizRouter appBizRouter, @NonNull BaseSpiServiceLoader appSpiServiceLoader);
}