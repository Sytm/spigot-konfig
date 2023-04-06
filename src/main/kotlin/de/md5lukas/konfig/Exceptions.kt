package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection

/**
 * Exception that is thrown when the class contains a variable that has no matching entry in the configuration file
 */
class MissingConfigurationKeyException(
    configurationSection: ConfigurationSection,
    path: String,
) : Exception("The configuration is missing an entry for ${getFullConfigPath(configurationSection, path)}")