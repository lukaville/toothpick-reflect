package com.lukaville.toothpick.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javax.inject.Inject;

import toothpick.MemberInjector;
import toothpick.Scope;

import static com.lukaville.toothpick.reflect.ReflectionUtils.tryInvoke;
import static com.lukaville.toothpick.reflect.ReflectionUtils.trySet;
import static com.lukaville.toothpick.reflect.DependencyProvider.provideDependencyForScope;

public class ReflectiveMemberInjector<T> implements MemberInjector<T> {

    public ReflectiveMemberInjector() {
    }

    @Override
    public void inject(T targetInstance, Scope scope) {
        Class<?> target = targetInstance.getClass();
        while (target != Object.class) {
            for (Field field : target.getDeclaredFields()) {
                if (field.getAnnotation(Inject.class) == null) {
                    continue;
                }
                if ((field.getModifiers() & Modifier.PRIVATE) != 0) {
                    throw new IllegalArgumentException("Toothpick does not support injection into private fields: "
                        + target.getCanonicalName() + "." + field.getName());
                }
                if ((field.getModifiers() & Modifier.STATIC) != 0) {
                    throw new IllegalArgumentException("Toothpick does not support injection into static fields: "
                        + target.getCanonicalName() + "." + field.getName());
                }

                Object value = provideDependencyForScope(scope,
                    field.getDeclaredAnnotations(),
                    field.getType(),
                    field.getGenericType()
                );

                trySet(targetInstance, field, value);
            }
            for (Method method : target.getDeclaredMethods()) {
                if (method.getAnnotation(Inject.class) == null) {
                    continue;
                }
                if ((method.getModifiers() & Modifier.PRIVATE) != 0) {
                    throw new IllegalArgumentException("Toothpick does not support injection into private methods: "
                        + target.getCanonicalName() + "." + method.getName() + "()");
                }
                if ((method.getModifiers() & Modifier.STATIC) != 0) {
                    throw new IllegalArgumentException("Toothpick does not support injection into static methods: "
                        + target.getCanonicalName() + "." + method.getName() + "()");
                }
                if ((method.getModifiers() & Modifier.ABSTRACT) != 0) {
                    throw new IllegalArgumentException("Methods with @Inject may not be abstract: "
                        + target.getCanonicalName() + "." + method.getName() + "()");
                }

                Type[] parameterTypes = method.getGenericParameterTypes();
                Class<?>[] parameterClasses = method.getParameterTypes();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();

                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    parameters[i] = provideDependencyForScope(
                        scope,
                        parameterAnnotations[i],
                        parameterClasses[i],
                        parameterTypes[i]
                    );
                }

                tryInvoke(targetInstance, method, parameters);
            }

            target = target.getSuperclass();
        }
    }

}
