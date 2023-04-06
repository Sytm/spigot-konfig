package de.md5lukas.konfig

import kotlin.reflect.KClass

interface RegisteredTypeAdapter<T> : TypeAdapter<T> {

    fun isApplicable(clazz: KClass<*>, firstTypeArgumentClass: KClass<*>?): Boolean

    abstract class Static<T>(
        private val clazz: KClass<*>,
        private val firstTypeParameterClass: KClass<*>? = null,
    ) : RegisteredTypeAdapter<T> {

        final override fun isApplicable(clazz: KClass<*>, firstTypeArgumentClass: KClass<*>?) =
            this.clazz == clazz && this.firstTypeParameterClass == firstTypeArgumentClass
    }
}