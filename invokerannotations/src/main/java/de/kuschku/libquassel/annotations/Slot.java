package de.kuschku.libquassel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("WeakerAccess")
@Retention(RetentionPolicy.SOURCE)
@Target(value = ElementType.METHOD)
public @interface Slot {
  String value() default "";
}
