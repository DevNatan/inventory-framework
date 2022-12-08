package me.saiintbrisson.minecraft.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that can be called only before view initialization, otherwise it will throw a
 * InitializationException.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE_USE})
public @interface InitOnly {}
