package com.lukaville.toothpick.reflect

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Ignore
import org.junit.Test
import toothpick.Scope

class ReflectiveFactoryScopeTest {

    private val scope: Scope = mock()

    @Test
    fun `no scope annotation - returns the same target scope`() {
        val factory = createFactory<EmptyConstructorClass>()

        val targetScope = factory.getTargetScope(scope)

        assertThat(targetScope, IsEqual(scope))
    }

    @Test
    fun `singleton scope annotation - returns root target scope`() {
        val rootScope = mock<Scope>()
        whenever(scope.rootScope).thenReturn(rootScope)
        val factory = createFactory<SingletonEmptyConstructorClass>()

        val targetScope = factory.getTargetScope(scope)

        assertThat(targetScope, IsEqual(rootScope))
    }

    @Test
    fun `custom scope annotation - returns parent target scope`() {
        val parentScope = mock<Scope>()
        whenever(scope.getParentScope(CustomScope::class.java)).thenReturn(parentScope)
        val factory = createFactory<CustomScopeEmptyConstructorClass>()

        val targetScope = factory.getTargetScope(scope)

        assertThat(targetScope, IsEqual(parentScope))
    }

    @Test
    fun `singleton annotation - returns has scope annotation`() {
        val factory = createFactory<SingletonEmptyConstructorClass>()

        val hasScopeAnnotation = factory.hasScopeAnnotation()

        assertThat(hasScopeAnnotation, IsEqual(true))
    }

    @Test
    fun `custom scope annotation - returns has scope annotation`() {
        val factory = createFactory<CustomScopeEmptyConstructorClass>()

        val hasScopeAnnotation = factory.hasScopeAnnotation()

        assertThat(hasScopeAnnotation, IsEqual(true))
    }

    @Ignore("provides singleton in scope annotation is not available in runtime")
    @Test
    fun `provides singleton in scope annotation - returns has provides singleton in scope annotation`() {
        val factory = createFactory<ProvidesSingletonInScopeClass>()

        val hasProvidesSingletonInScopeAnnotation = factory.hasProvidesSingletonInScopeAnnotation()

        assertThat(hasProvidesSingletonInScopeAnnotation, IsEqual(true))
    }

    @Ignore("provides singleton in scope annotation is not available in runtime")
    @Test
    fun `no provides singleton in scope annotation - returns does not have provides singleton in scope annotation`() {
        val factory = createFactory<EmptyConstructorClass>()

        val hasProvidesSingletonInScopeAnnotation = factory.hasProvidesSingletonInScopeAnnotation()

        assertThat(hasProvidesSingletonInScopeAnnotation, IsEqual(false))
    }

    private inline fun <reified T> createFactory(): ReflectiveFactory<T> {
        return ReflectiveFactory(T::class.java)
    }
}
