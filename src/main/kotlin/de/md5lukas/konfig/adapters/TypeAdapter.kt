package de.md5lukas.konfig.adapters

import org.bukkit.configuration.ConfigurationSection

interface TypeAdapter<T> {
    fun get(section: ConfigurationSection, path: String): T?
}