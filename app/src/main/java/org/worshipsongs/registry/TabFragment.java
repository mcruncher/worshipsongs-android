package org.worshipsongs.registry;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Author : Madasamy
 * Version : 4.x.x
 */

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface TabFragment
{
    int sortOrder() default 999;

    @StringRes int title() default 0;

    boolean checked() default true;

}
