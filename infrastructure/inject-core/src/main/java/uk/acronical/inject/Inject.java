package uk.acronical.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be automatically populated by the dependency injection container.
 * <p>
 * When a class is processed by the injector, fields annotated with {@code @Inject}
 * will be assigned their corresponding registered instances, eliminating the
 * need for manual assignment or complex constructor chaining.
 *
 * @author Acronical
 * @since 1.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {}
