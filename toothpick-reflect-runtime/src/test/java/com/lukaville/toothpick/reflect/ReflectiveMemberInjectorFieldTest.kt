package com.lukaville.toothpick.reflect

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.hamcrest.core.IsNull
import org.junit.Test
import toothpick.Scope

class ReflectiveMemberInjectorFieldTest {

    private val injector = ReflectiveMemberInjector<Any>()
    private val scope: Scope = mock()

    @Test
    fun `one injected field - injects dependency into field`() {
        val dependency = EmptyConstructorClass()
        whenever(scope.getInstance(eq(EmptyConstructorClass::class.java), isNull()))
            .thenReturn(dependency)
        val target = OneInjectedFieldClass()

        target.inject(scope)

        assertThat(target.other, IsNot(IsNull()))
        assertThat(target.other, IsEqual(dependency))
    }

    @Test
    fun `one injected field in superclass - injects dependency into field`() {
        val dependency = EmptyConstructorClass()
        whenever(scope.getInstance(eq(EmptyConstructorClass::class.java), isNull()))
            .thenReturn(dependency)
        val target = SuperClassInjectedFieldClass()

        target.inject(scope)

        assertThat(target.other, IsNot(IsNull()))
        assertThat(target.other, IsEqual(dependency))
    }

    private inline fun <reified T> T.inject(scope: Scope) {
        injector.inject(this, scope)
    }
}
