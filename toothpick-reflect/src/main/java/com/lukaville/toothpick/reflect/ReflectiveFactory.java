package com.lukaville.toothpick.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import javax.inject.Singleton;

import toothpick.Factory;
import toothpick.Scope;

import static com.lukaville.toothpick.reflect.DependencyProvider.provideDependencyForScope;
import static com.lukaville.toothpick.reflect.ReflectionUtils.findInjectConstructor;
import static com.lukaville.toothpick.reflect.ReflectionUtils.findScopeAnnotation;
import static com.lukaville.toothpick.reflect.ReflectionUtils.hasProvidesSingletonInScope;
import static com.lukaville.toothpick.reflect.ReflectionUtils.tryInstantiate;

public class ReflectiveFactory<T> implements Factory<T> {

    @NotNull
    private final Constructor<T> constructor;

    @Nullable
    private Annotation scopeAnnotation;

    private final boolean hasProvidesSingletonInScope;

    public ReflectiveFactory(@NotNull Class<T> clazz) {
        this.constructor = findInjectConstructor(clazz);
        this.scopeAnnotation = findScopeAnnotation(clazz.getAnnotations());

        // TODO: support super class that needs member injection
        // FIXME: provides singleton in scope annotation is not available in runtime
        this.hasProvidesSingletonInScope = hasProvidesSingletonInScope(clazz.getAnnotations());
    }

    @Override
    public T createInstance(final Scope scope) {
        final Type[] parameterTypes = constructor.getGenericParameterTypes();
        final Class<?>[] parameterClasses = constructor.getParameterTypes();
        if (parameterTypes.length > 0) {
            final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            final Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                final Type type = parameterTypes[i];
                parameters[i] = provideDependencyForScope(
                    getTargetScope(scope),
                    parameterAnnotations[i],
                    parameterClasses[i],
                    type
                );
            }
            return tryInstantiate(constructor, parameters);
        } else {
            return tryInstantiate(constructor, new Object[0]);
        }
    }

    @Override
    public Scope getTargetScope(Scope scope) {
        if (scopeAnnotation == null) {
            return scope;
        } else if (scopeAnnotation.annotationType().equals(Singleton.class)) {
            return scope.getRootScope();
        } else {
            return scope.getParentScope(scopeAnnotation.annotationType());
        }
    }

    @Override
    public boolean hasScopeAnnotation() {
        return scopeAnnotation != null;
    }

    @Override
    public boolean hasProvidesSingletonInScopeAnnotation() {
        return hasProvidesSingletonInScope;
    }
}
