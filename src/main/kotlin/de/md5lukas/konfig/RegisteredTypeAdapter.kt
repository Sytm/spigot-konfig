package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass

/**
 * A registered type adapter can be registered into a [Konfig] instance.
 *
 * Instead of marking a lot of properties of the same type with [UseAdapter], this type adapter is preferred
 */
interface RegisteredTypeAdapter<T> : TypeAdapter<T> {

    /**
     * If this method returns a null value for a property that is not marked as nullable, a [NullPointerException]
     * will be thrown
     *
     * @param section The ConfigurationSection to read from
     * @param path The path in the ConfigurationSection to use
     * @param clazz The runtime class of the property
     * @param typeArgumentClasses The runtime type argument classes of the property
     * @return The parsed Object
     */
    fun get(section: ConfigurationSection, path: String, clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>) =
        get(section, path)

    override fun get(section: ConfigurationSection, path: String): T? =
        throw NotImplementedError()

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