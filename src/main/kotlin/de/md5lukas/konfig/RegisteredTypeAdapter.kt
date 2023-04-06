package de.md5lukas.konfig

import kotlin.reflect.KClass

/**
 * A registered type adapter can be registered into a [Konfig] instance.
 *
 * Instead of marking a lot of properties of the same type with [UseAdapter], this type adapter is preferred
 */
interface RegisteredTypeAdapter<T> : TypeAdapter<T> {

    /**
     * Function that gets called with the class of the property and its type argument classes
     */
    fun isApplicable(clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>): Boolean

    /**
     * Helper class that implements the [RegisteredTypeAdapter.isApplicable] to check against [clazz]
     * and [firstTypeParameterClass]
     */
    abstract class Static<T>(
        private val clazz: KClass<*>,
        private val firstTypeParameterClass: KClass<*>? = null,
    ) : RegisteredTypeAdapter<T> {

        final override fun isApplicable(clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>) =
            this.clazz == clazz && if (firstTypeParameterClass === null) {
                typeArgumentClasses.isEmpty()
            } else {
                this.firstTypeParameterClass == typeArgumentClasses.firstOrNull()
            }
    }
}