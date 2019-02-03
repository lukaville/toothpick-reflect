package com.lukaville.toothpick.reflect

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.hamcrest.core.IsNull
import org.junit.Test
import toothpick.Lazy
import toothpick.Scope
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
class ReflectiveFactoryCreateTest {

    private val scope: Scope = mock()

    @Test
    fun `empty constructor`() {
        val instance = createUsingFactory<EmptyConstructorClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        verifyZeroInteractions(scope)
    }

    @Test
    fun `one instance parameter constructor`() {
        val dependency = EmptyConstructorClass()
        whenever(scope.getInstance(eq(EmptyConstructorClass::class.java), isNull()))
            .thenReturn(dependency)

        val instance = createUsingFactory<OneParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `two instance parameter constructor`() {
        val dependency1 = EmptyConstructorClass()
        val dependency2 = SecondEmptyConstructorClass()
        whenever(scope.getInstance(eq(EmptyConstructorClass::class.java), isNull()))
            .thenReturn(dependency1)
        whenever(scope.getInstance(eq(SecondEmptyConstructorClass::class.java), isNull()))
            .thenReturn(dependency2)

        val instance = createUsingFactory<TwoParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.first, IsEqual(dependency1))
        assertThat(instance.second, IsEqual(dependency2))
    }

    @Test
    fun `lazy parameter constructor`() {
        val dependency = Lazy { EmptyConstructorClass() }
        whenever(scope.getLazy(eq(EmptyConstructorClass::class.java), isNull()))
            .thenReturn(dependency)

        val instance = createUsingFactory<LazyParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `provider parameter constructor`() {
        val dependency = Provider { EmptyConstructorClass() }
        whenever(scope.getProvider(eq(EmptyConstructorClass::class.java), isNull())).thenReturn(
            dependency
        )

        val instance = createUsingFactory<ProviderParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `generic parameter constructor`() {
        val dependency = listOf<EmptyConstructorClass>()
        whenever(scope.getInstance(eq(List::class.java), isNull()))
            .thenReturn(dependency)

        val instance = createUsingFactory<GenericParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `lazy generic parameter constructor`() {
        val dependency = Lazy { listOf<EmptyConstructorClass>() }
        whenever(scope.getLazy(eq(List::class.java), isNull()))
            .thenReturn(dependency as Lazy<List<*>>)

        val instance = createUsingFactory<LazyGenericParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `provider generic parameter constructor`() {
        val dependency = Provider { listOf<EmptyConstructorClass>() }
        whenever(scope.getProvider(eq(List::class.java), isNull()))
            .thenReturn(dependency as Provider<List<*>>)

        val instance = createUsingFactory<ProviderGenericParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `qualified parameter constructor - gets qualified instance`() {
        val dependency = EmptyConstructorClass()
        whenever(
            scope.getInstance(
                eq(EmptyConstructorClass::class.java),
                eq(TestQualifier::class.java.name)
            )
        )
            .thenReturn(dependency)

        val instance = createUsingFactory<OneQualifiedParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `named parameter constructor - gets qualified instance`() {
        val dependency = EmptyConstructorClass()
        whenever(
            scope.getInstance(
                eq(EmptyConstructorClass::class.java),
                eq(TestQualifier::class.java.name)
            )
        )
            .thenReturn(dependency)

        val instance = createUsingFactory<OneQualifiedParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    @Test
    fun `named parameter constructor - gets named instance`() {
        val dependency = EmptyConstructorClass()
        whenever(scope.getInstance(eq(EmptyConstructorClass::class.java), eq("test")))
            .thenReturn(dependency)

        val instance = createUsingFactory<OneNamedParameterClass>(scope)

        assertThat(instance, IsNot(IsNull()))
        assertThat(instance!!.other, IsEqual(dependency))
    }

    private inline fun <reified T> createUsingFactory(scope: Scope = this.scope): T? {
        return ReflectiveFactory(T::class.java).createInstance(scope)
    }
}