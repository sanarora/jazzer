package com.code_intelligence.jazzer.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.platform.commons.annotation.Testable;

@Retention(RetentionPolicy.RUNTIME)
@Testable
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface FuzzTest {}
