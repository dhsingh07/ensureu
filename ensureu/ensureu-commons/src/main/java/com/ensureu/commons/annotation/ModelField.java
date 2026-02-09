package com.ensureu.commons.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ModelField {

	/**
	 * @return
	 */
	public String name();
	/**
	 * @return
	 */
	public String type();
	/**
	 * @return
	 */
	public boolean isRequired();
}
