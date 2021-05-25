/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * A command option.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface Option
{
   /**
    * The name of this option
    */
   String value();

   /**
    * The label of this {@link InputComponent}.
    */
   String label() default "";

   /**
    * The programmatic name of this {@link InputComponent}. This is typically a shorthand or alternate name used for
    * keyboard shortcuts or command line flags.
    */
   char shortName() default InputComponents.DEFAULT_SHORT_NAME;

   /**
    * If <code>true</code>, the value of this object must not be <code>null</code>.
    */
   boolean required() default false;

   /**
    * The message to display during validation when this {@link InputComponent} value is set to <code>null</code>.
    */
   String requiredMessage() default "";

   /**
    * The description for this {@link InputComponent}.
    */
   String description() default "";

   /**
    * Return <code>true</code> if this {@link InputComponent} is enabled.
    */
   boolean enabled() default true;

   /**
    * The {@link InputType} for this {@link InputComponent}.
    */
   String type() default InputType.DEFAULT;

   /**
    * The default value for this {@link InputComponent}.
    */
   String defaultValue() default "";

   /**
    * The note for this {@link InputComponent}.
    */
   String note() default "";

   /**
    * Is this {@link InputComponent} deprecated?
    */
   boolean deprecated() default false;

   /**
    * The deprecated message for this {@link InputComponent}
    */
   String deprecatedMessage() default "";
}
