package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection

interface TypeAdapter<T> {
    fun get(section: ConfigurationSection, path: String): T?
}