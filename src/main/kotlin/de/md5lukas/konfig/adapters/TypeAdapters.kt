package de.md5lukas.konfig.adapters

import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object StringAdapter : RegisteredTypeAdapter.Static<String>(String::class) {
    override fun get(section: ConfigurationSection, path: String): String? = section.getString(path)
}

object BooleanAdapter : RegisteredTypeAdapter.Static<Boolean>(Boolean::class) {
    override fun get(section: ConfigurationSection, path: String): Boolean = section.getBoolean(path)
}

object ByteAdapter : RegisteredTypeAdapter.Static<Byte>(Byte::class) {
    override fun get(section: ConfigurationSection, path: String): Byte = section.getInt(path).toByte()
}

object ShortAdapter : RegisteredTypeAdapter.Static<Short>(Short::class) {
    override fun get(section: ConfigurationSection, path: String): Short = section.getInt(path).toShort()
}

object IntAdapter : RegisteredTypeAdapter.Static<Int>(Int::class) {
    override fun get(section: ConfigurationSection, path: String): Int = section.getInt(path)
}

object LongAdapter : RegisteredTypeAdapter.Static<Long>(Long::class) {
    override fun get(section: ConfigurationSection, path: String): Long = section.getLong(path)
}

object FloatAdapter : RegisteredTypeAdapter.Static<Float>(Float::class) {
    override fun get(section: ConfigurationSection, path: String): Float = section.getDouble(path).toFloat()
}

object DoubleAdapter : RegisteredTypeAdapter.Static<Double>(Double::class) {
    override fun get(section: ConfigurationSection, path: String): Double = section.getDouble(path)
}

object StringListAdapter : RegisteredTypeAdapter<List<String>> {
    override fun get(section: ConfigurationSection, path: String): List<String> = section.getStringList(path)

    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && String::class == firstTypeParameterClass
}

object BooleanListAdapter : RegisteredTypeAdapter<List<Boolean>> {
    override fun get(section: ConfigurationSection, path: String): List<Boolean> = section.getBooleanList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Boolean::class == firstTypeParameterClass
}

object ByteListAdapter : RegisteredTypeAdapter<List<Byte>> {
    override fun get(section: ConfigurationSection, path: String): List<Byte> = section.getByteList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Byte::class == firstTypeParameterClass
}

object ShortListAdapter : RegisteredTypeAdapter<List<Short>> {
    override fun get(section: ConfigurationSection, path: String): List<Short> = section.getShortList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Short::class == firstTypeParameterClass
}

object IntListAdapter : RegisteredTypeAdapter<List<Int>> {
    override fun get(section: ConfigurationSection, path: String): List<Int> = section.getIntegerList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Int::class == firstTypeParameterClass
}

object CharListAdapter : RegisteredTypeAdapter<List<Char>> {
    override fun get(section: ConfigurationSection, path: String): List<Char> = section.getCharacterList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Char::class == firstTypeParameterClass
}

object LongListAdapter : RegisteredTypeAdapter<List<Long>> {
    override fun get(section: ConfigurationSection, path: String): List<Long> = section.getLongList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Long::class == firstTypeParameterClass
}

object FloatListAdapter : RegisteredTypeAdapter<List<Float>> {
    override fun get(section: ConfigurationSection, path: String): List<Float> = section.getFloatList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Float::class == firstTypeParameterClass
}

object DoubleListAdapter : RegisteredTypeAdapter<List<Double>> {
    override fun get(section: ConfigurationSection, path: String): List<Double> = section.getDoubleList(path)
    override fun isApplicable(clazz: KClass<*>, firstTypeParameterClass: KClass<*>?) =
        clazz.isSubclassOf(List::class) && Double::class == firstTypeParameterClass
}