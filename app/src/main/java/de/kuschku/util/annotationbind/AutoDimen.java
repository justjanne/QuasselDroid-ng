package de.kuschku.util.annotationbind;

import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoDimen {
    @NonNull @AnyRes int[] value() default {};
}
