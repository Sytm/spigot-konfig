package de.md5lukas.konfig

import org.bukkit.configuration.file.YamlConfiguration

private object TestHelpers

fun loadConfiguration(path: String) =
    YamlConfiguration.loadConfiguration(TestHelpers.javaClass.classLoader.getResourceAsStream(path)!!.reader())
