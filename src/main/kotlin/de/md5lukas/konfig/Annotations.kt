package de.md5lukas.konfig

import kotlin.reflect.KClass

/**
 * Nested classes must be annotated with this annotation if they should also be deserialized into
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configurable

/**
 * Set a custom config path for this value
 * @property path The path to use
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigPath(
    val path: String
)

/**
 * Use a custom type adapter just for this property.
 * @property adapter The adapter implementation to use
 * @throws IllegalArgumentException If the adapter is not a Kotlin object
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class UseAdapter(
    val adapter: KClass<out TypeAdapter<*>>
)