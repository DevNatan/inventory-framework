package me.saiintbrisson.minecraft.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that can be called only before view initialization,
 * otherwise it will throw a {@link me.saiintbrisson.minecraft.exception.InitializationException}.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface InitOnly {}
