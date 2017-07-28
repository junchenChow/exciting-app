package me.vociegif.android.mvp.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ContextLevel {
    String APPLICATION = "Application";
    String ACTIVITY = "Activity";
    String FRAGMENT = "Fragment";
    String SERVICE = "Service";

    String value() default APPLICATION;
}