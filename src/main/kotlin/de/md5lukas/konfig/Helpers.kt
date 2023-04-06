package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

internal fun <T> validateNotNull(section: ConfigurationSection, path: String, value: T?): T {
    return value ?: throw MissingConfigurationKeyException(section, path)
}

internal operator fun KClass<*>.contains(other: KClass<*>?) = other !== null && this.isSuperclassOf(other)

internal fun getFullConfigPath(configurationSection: ConfigurationSection, path: String) =
    if (configurationSection.currentPath.isNullOrEmpty()) {
        path
    } else {
        "${configurationSection.currentPath}.$path"
    }