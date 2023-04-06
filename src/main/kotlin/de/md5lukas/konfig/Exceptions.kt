package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection

open class ConfigurationException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

class MissingConfigurationKeyException(
    configurationSection: ConfigurationSection,
    path: String,
) : ConfigurationException("The configuration is missing an entry for ${getFullConfigPath(configurationSection, path)}")