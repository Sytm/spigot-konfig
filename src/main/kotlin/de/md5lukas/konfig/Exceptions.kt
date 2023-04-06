package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection

class MissingConfigurationKeyException(
    configurationSection: ConfigurationSection,
    path: String,
) : Exception("The configuration is missing an entry for ${getFullConfigPath(configurationSection, path)}")