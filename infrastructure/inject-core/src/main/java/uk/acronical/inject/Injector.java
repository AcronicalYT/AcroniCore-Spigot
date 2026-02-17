package uk.acronical.inject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A lightweight dependency injection container for managing and injecting services.
 * <p>
 * This injector supports both class-based and interface-based service registration,
 * utilising reflection to populate fields annotated with {@link Inject}.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class Injector {

    private final Map<Class<?>, Object> services = new HashMap<>();

    /**
     * Registers a service instance within the container.
     * <p>
     * The service is mapped to its concrete class and all implemented interfaces,
     * allowing for flexible injection based on abstractions.
     *
     * @param service The object instance to register as a service.
     */
    public void register(@NotNull Object service) {
        services.put(service.getClass(), service);

        for (Class<?> serviceInterface : service.getClass().getInterfaces()) {
            services.put(serviceInterface, service);
        }
    }

    /**
     * Injects registered services into the annotated fields of a target object.
     *
     * @param target The object instance to perform injection upon.
     * @throws IllegalArgumentException If an {@link Inject} field has no registered service.
     * @throws RuntimeException         If a field cannot be accessed or modified via reflection.
     */
    public void inject(@NotNull Object target) {
        Class<?> clazz = target.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> type = field.getType();
                Object service = services.get(type);

                if (service == null) {
                    throw new IllegalArgumentException("Cannot inject " + type.getSimpleName() + " into " + clazz.getSimpleName() + ": no registered service found");
                }

                try {
                    field.setAccessible(true);
                    field.set(target, service);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject " + type.getSimpleName() + " into " + clazz.getSimpleName(), e);
                }
            }
        }
    }

    /**
     * Creates a new instance of a class and automatically injects its dependencies.
     * <p>
     * Note: The target class must possess a public no-argument constructor.
     *
     * @param clazz The class to instantiate.
     * @param <T>   The type of the instance.
     * @return A fully initialised instance with injected dependencies.
     * @throws RuntimeException If instantiation fails or dependencies cannot be satisfied.
     */
    @NotNull
    public <T> T getInstance(@NotNull Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            inject(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getSimpleName(), e);
        }
    }
}
