package de.md5lukas.konfig.adapters

import org.bukkit.configuration.ConfigurationSection

object StringAdapter : TypeAdapter<String> {
    override fun get(section: ConfigurationSection, path: String): String? = section.getString(path)
}

object BooleanAdapter : TypeAdapter<Boolean> {
    override fun get(section: ConfigurationSection, path: String): Boolean = section.getBoolean(path)
}

object ByteAdapter : TypeAdapter<Byte> {
    override fun get(section: ConfigurationSection, path: String): Byte = section.getInt(path).toByte()
}

object ShortAdapter : TypeAdapter<Short> {
    override fun get(section: ConfigurationSection, path: String): Short = section.getInt(path).toShort()
}

object IntAdapter : TypeAdapter<Int> {
    override fun get(section: ConfigurationSection, path: String): Int = section.getInt(path)
}

object LongAdapter : TypeAdapter<Long> {
    override fun get(section: ConfigurationSection, path: String): Long = section.getLong(path)
}

object FloatAdapter : TypeAdapter<Float> {
    override fun get(section: ConfigurationSection, path: String): Float = section.getDouble(path).toFloat()
}

object DoubleAdapter : TypeAdapter<Double> {
    override fun get(section: ConfigurationSection, path: String): Double = section.getDouble(path)
}

object StringListAdapter : TypeAdapter<List<String>> {
    override fun get(section: ConfigurationSection, path: String): List<String> = section.getStringList(path)
}

object BooleanListAdapter : TypeAdapter<List<Boolean>> {
    override fun get(section: ConfigurationSection, path: String): List<Boolean> = section.getBooleanList(path)
}

object ByteListAdapter : TypeAdapter<List<Byte>> {
    override fun get(section: ConfigurationSection, path: String): List<Byte> = section.getByteList(path)
}

object ShortListAdapter : TypeAdapter<List<Short>> {
    override fun get(section: ConfigurationSection, path: String): List<Short> = section.getShortList(path)
}

object IntListAdapter : TypeAdapter<List<Int>> {
    override fun get(section: ConfigurationSection, path: String): List<Int> = section.getIntegerList(path)
}

object CharListAdapter : TypeAdapter<List<Char>> {
    override fun get(section: ConfigurationSection, path: String): List<Char> = section.getCharacterList(path)
}

object LongListAdapter : TypeAdapter<List<Long>> {
    override fun get(section: ConfigurationSection, path: String): List<Long> = section.getLongList(path)
}

object FloatListAdapter : TypeAdapter<List<Float>> {
    override fun get(section: ConfigurationSection, path: String): List<Float> = section.getFloatList(path)
}

object DoubleListAdapter : TypeAdapter<List<Double>> {
    override fun get(section: ConfigurationSection, path: String): List<Double> = section.getDoubleList(path)
}
