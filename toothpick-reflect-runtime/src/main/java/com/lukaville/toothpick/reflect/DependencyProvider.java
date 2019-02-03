package com.lukaville.toothpick.reflect;


import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import toothpick.Scope;

import static com.lukaville.toothpick.reflect.ParamInjectionTarget.LAZY;
import static com.lukaville.toothpick.reflect.ParamInjectionTarget.PROVIDER;
import static com.lukaville.toothpick.reflect.ReflectionUtils.findName;
import static com.lukaville.toothpick.reflect.ReflectionUtils.getParamInjectionTarget;

class DependencyProvider {

    @Nullable
    static Object provideDependencyForScope(Scope scope,
                                            Annotation[] annotations,
                                            Class<?> clazz,
                                            Type targetType) {
        return provideDependencyForScope(
            scope,
            findName(annotations),
            clazz,
            targetType
        );
    }

    @Nullable
    private static Object provideDependencyForScope(Scope scope,
                                                    String name,
                                                    Class<?> clazz,
                                                    Type targetType) {
        final ParamInjectionTarget target = getParamInjectionTarget(clazz);

        if (target == PROVIDER) {
            final Class<?> genericClass = extractFirstGenericClass(targetType);
            return scope.getProvider(genericClass, name);
        } else if (target == LAZY) {
            final Class<?> genericClass = extractFirstGenericClass(targetType);
            return scope.getLazy(genericClass, name);
        } else {
            return scope.getInstance(clazz, name);
        }
    }

    private static Class<?> extractFirstGenericClass(Type type) {
        final ParameterizedType parametrizedType = (ParameterizedType) type;
        final Type genericType = parametrizedType.getActualTypeArguments()[0];
        if (genericType instanceof Class<?>) {
            return (Class<?>) genericType;
        } else if (genericType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) genericType).getRawType();
            if (rawType instanceof Class<?>) {
                return (Class<?>) rawType;
            }
            throw new AssertionError("Unsupported type " + genericType.toString());
        } else {
            throw new AssertionError("Unsupported type " + genericType.toString());
        }
    }

}
