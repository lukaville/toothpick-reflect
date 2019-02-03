package com.lukaville.toothpick.reflect

import toothpick.Lazy
import toothpick.ProvidesSingletonInScope
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Scope
import javax.inject.Singleton

class EmptyConstructorClass @Inject constructor()

@Singleton
class SingletonEmptyConstructorClass @Inject constructor()

@CustomScope
class CustomScopeEmptyConstructorClass @Inject constructor()

@ProvidesSingletonInScope
class ProvidesSingletonInScopeClass @Inject constructor()

class SecondEmptyConstructorClass @Inject constructor()

class OneParameterClass @Inject constructor(val other: EmptyConstructorClass)

class OneQualifiedParameterClass @Inject constructor(@TestQualifier val other: EmptyConstructorClass)

class OneNamedParameterClass @Inject constructor(@Named("test") val other: EmptyConstructorClass)

class TwoParameterClass @Inject constructor(
    val first: EmptyConstructorClass,
    val second: SecondEmptyConstructorClass
)

class LazyParameterClass @Inject constructor(val other: Lazy<EmptyConstructorClass>)

class ProviderParameterClass @Inject constructor(val other: Provider<EmptyConstructorClass>)

class GenericParameterClass @Inject constructor(val other: List<EmptyConstructorClass>)

class LazyGenericParameterClass @Inject constructor(val other: Lazy<List<EmptyConstructorClass>>)

class ProviderGenericParameterClass @Inject constructor(val other: Provider<List<EmptyConstructorClass>>)

open class OneInjectedFieldClass {
    @Inject
    @JvmField
    var other: EmptyConstructorClass? = null
}

open class OneInjectedMethodClass {

    var other: EmptyConstructorClass? = null

    @Inject
    fun someMethod(other: EmptyConstructorClass) {
        this.other = other
    }
}

class SuperClassInjectedFieldClass : OneInjectedFieldClass()

class SuperClassInjectedMethodClass : OneInjectedMethodClass()

@Qualifier
annotation class TestQualifier

@Scope
annotation class CustomScope