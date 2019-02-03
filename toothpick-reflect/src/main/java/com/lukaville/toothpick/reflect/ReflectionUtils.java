package com.lukaville.toothpick.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Scope;

import toothpick.Lazy;
import toothpick.ProvidesSingletonInScope;

final class ReflectionUtils {

    static @Nullable String findName(Annotation[] annotations) {
        final Annotation qualifier = findQualifier(annotations);
        if (qualifier != null) {
            if (qualifier.annotationType().equals(Named.class)) {
                Named named = (Named) qualifier;
                return named.value();
            }
            return qualifier.annotationType().getName();
        }
        return null;
    }

    static @NotNull ParamInjectionTarget getParamInjectionTarget(Class<?> clazz) {
        if (clazz.isAssignableFrom(Provider.class)) {
            return ParamInjectionTarget.PROVIDER;
        } else if (clazz.isAssignableFrom(Lazy.class)) {
            return ParamInjectionTarget.LAZY;
        }
        return ParamInjectionTarget.INSTANCE;
    }

    static @Nullable Annotation findScopeAnnotation(Annotation[] annotations) {
        Annotation scope = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getAnnotation(Scope.class) != null) {
                if (scope != null) {
                    throw new IllegalArgumentException(
                        "Multiple scope annotations: " + scope + " and " + annotation);
                }
                scope = annotation;
            }
        }
        return scope;
    }

    static boolean hasProvidesSingletonInScope(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(ProvidesSingletonInScope.class)) {
                return true;
            }
        }
        return false;
    }

    static void trySet(@Nullable Object instance, Field field, Object value) {
        if ((field.getModifiers() & Modifier.PUBLIC) == 0) {
            field.setAccessible(true);
        }
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to set " + value + " to " + field + " on " + instance, e);
        }
    }

    @Nullable
    static Object tryInvoke(@Nullable Object instance, Method method, Object... arguments) {
        if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to invoke " + method + " on " + instance, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new RuntimeException("Unable to invoke " + method + " on " + instance, cause);
        }
    }

    @NotNull
    static <T> Constructor<T> findInjectConstructor(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getAnnotation(Inject.class) != null) {
                // allow multiple @Inject constructors
                //noinspection unchecked
                return (Constructor<T>) constructor;
            }
        }
        throw new AssertionError("@Inject annotated constructor not found for class " + clazz);
    }

    static <T> T tryInstantiate(Constructor<T> constructor, Object[] arguments) {
        if ((constructor.getModifiers() & Modifier.PUBLIC) == 0) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AssertionError("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new RuntimeException("Unable to invoke " + constructor, cause);
        }
    }

    private static @Nullable Annotation findQualifier(Annotation[] annotations) {
        Annotation qualifier = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getAnnotation(Qualifier.class) != null) {
                if (qualifier != null) {
                    throw new IllegalArgumentException(
                        "Multiple qualifier annotations: " + qualifier + " and " + annotation);
                }
                qualifier = annotation;
            }
        }
        return qualifier;
    }

    private ReflectionUtils() {
        throw new AssertionError();
    }
}