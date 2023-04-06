package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection

/**
 * A type adapter to load data from the configuration at the given path as an usable instance that is used only for
 * fields annotated with [UseAdapter]
 */
interface TypeAdapter<T> {

    /**
     * If this method returns a null value for a property that is not marked as nullable, a [NullPointerException]
     * will be thrown
     *
     * @param section The ConfigurationSection to read from
     * @param path The path in the ConfigurationSection to use
     * @return The parsed Object
     */
    fun get(section: ConfigurationSection, path: String): T?
}