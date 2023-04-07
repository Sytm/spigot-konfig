package de.md5lukas.konfig

import org.bukkit.configuration.ConfigurationSection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KonfigTest {

    @Test
    fun missingKey() {
        val yaml = loadConfiguration("empty.yml")
        val config = NormalTypes()

        assertThrows<MissingConfigurationKeyException> {
            Konfig.deserializeInto(yaml, config)
        }
    }

    @Test
    fun normalTypes() {
        val yaml = loadConfiguration("normal.yml")
        val config = NormalTypes()

        Konfig.deserializeInto(yaml, config)

        verifyNormalTypes(config)
    }

    private fun verifyNormalTypes(instance: NormalTypes) = with(instance) {
        assertEquals("Hello World!", aString)

        assertEquals(true, aBoolean)

        assertEquals(123, aByte)
        assertEquals(12345, aShort)
        assertEquals(12345678, anInt)
        assertEquals(12345678910, aLong)
        assertEquals("125.257".toFloat(), aFloat)
        assertEquals("496205.593".toDouble(), aDouble)
    }

    @Configurable
    class NormalTypes {

        lateinit var aString: String

        var aBoolean = false

        var aByte: Byte = 0
        var aShort: Short = 0
        var anInt = 0
        var aLong = 0L
        var aFloat = 0f
        var aDouble = 0.0
    }

    @Test
    fun listTypes() {
        val yaml = loadConfiguration("lists.yml")
        val config = ListTypes()

        Konfig.deserializeInto(yaml, config)

        with(config) {
            assertEquals(listOf("Hello", "World!"), stringList)

            assertEquals(listOf(true, false, true), booleanList)

            assertEquals(listOf(Byte.MIN_VALUE, 0, Byte.MAX_VALUE), byteList)
            assertEquals(listOf(Short.MIN_VALUE, 0, Short.MAX_VALUE), shortList)
            assertEquals(listOf(Int.MIN_VALUE, 0, Int.MAX_VALUE), intList)
            assertEquals(listOf(Char.MIN_VALUE, Char.MAX_VALUE), charList)
            assertEquals(listOf(Long.MIN_VALUE, 0, Long.MAX_VALUE), longList)
            assertEquals(listOf("-2147483648.0".toFloat(), 0f, "2147483647.0".toFloat()), floatList)
            assertEquals(
                listOf("-9223372036854775808.0".toDouble(), 0.0, "9223372036854775807.0".toDouble()),
                doubleList
            )
        }
    }

    class ListTypes {

        lateinit var stringList: List<String>

        lateinit var booleanList: List<Boolean>

        lateinit var byteList: List<Byte>
        lateinit var shortList: List<Short>
        lateinit var intList: List<Int>
        lateinit var charList: List<Char>
        lateinit var longList: List<Long>
        lateinit var floatList: List<Float>
        lateinit var doubleList: List<Double>
    }

    private class NullAdapter : TypeAdapter<String> {
        override fun get(section: ConfigurationSection, path: String): String? = null
    }

    @Test
    fun nullableTypes() {
        val yaml = loadConfiguration("nullable.yml")
        val config = NullableTypes()

        Konfig.deserializeInto(yaml, config)

        with(config) {
            assertEquals("Hello World!", normalString)
            assertNull(nullableString)
        }
    }

    class NullableTypes {

        lateinit var normalString: String

        @UseAdapter(NullAdapter::class)
        var nullableString: String? = "Hello World!"
    }

    @Test
    fun nullableTypesThrows() {
        val yaml = loadConfiguration("nullable.yml")
        val config = NullableTypesThrows()

        assertThrows<NullPointerException> {
            Konfig.deserializeInto(yaml, config)
        }

        assertEquals("Hello World!", config.normalString)
    }

    class NullableTypesThrows {

        @UseAdapter(NullAdapter::class)
        var normalString = "Hello World!"
    }

    @Test
    fun superClass() {
        val yaml = loadConfiguration("superclass.yml")
        val config = BaseClass()

        Konfig.deserializeInto(yaml, config)

        assertEquals("Hello World!", config.inClass)
        assertEquals("Hello World?", config.inSuperClass)
    }

    open class SuperClass {
        lateinit var inSuperClass: String
    }

    class BaseClass : SuperClass() {
        lateinit var inClass: String
    }

    @Test
    fun nested() {
        val yaml = loadConfiguration("nested.yml")
        val config = NestedNormalTypes()

        Konfig.deserializeInto(yaml, config)

        assertEquals("Hello World?", config.aString)
        verifyNormalTypes(config.nested)
    }

    class NestedNormalTypes {
        lateinit var aString: String

        val nested = NormalTypes()
    }

    @Test
    fun differingPath() {
        val yaml = loadConfiguration("differingpath.yml")
        val config = DifferingPath()

        Konfig.deserializeInto(yaml, config)

        assertEquals("Hello there", config.someString)
    }

    class DifferingPath {
        @ConfigPath("anotherPath")
        lateinit var someString: String
    }

    @Test
    fun enums() {
        val yaml = loadConfiguration("enums.yml")
        val config = Enums()

        Konfig.deserializeInto(yaml, config)

        assertEquals(AnEnum.VALUE1, config.first)
        assertEquals(AnEnum.VALUE2, config.second)
        assertEquals(listOf(AnEnum.VALUE1, AnEnum.VALUE2, AnEnum.VALUE1), config.aList)
    }

    @Test
    fun enumsUnknown() {
        val yaml = loadConfiguration("enums-unknown.yml")
        val config = Enums()

        assertThrows<NullPointerException> {
            Konfig.deserializeInto(yaml, config)
        }
    }

    class Enums {
        lateinit var first: AnEnum
        lateinit var second: AnEnum

        lateinit var aList: List<AnEnum>
    }

    enum class AnEnum {
        VALUE1,
        VALUE2,
    }

    @Test
    fun exportConfiguration() {
        val yaml = loadConfiguration("nested.yml")
        val config = ExportConfiguration()

        Konfig.deserializeInto(yaml, config)

        assertEquals(yaml, config.root)
        assertEquals(yaml.getConfigurationSection("nested"), config.nested)
        assertEquals(yaml, config.noBackingField)
    }

    class ExportConfiguration {
        @ExportConfigurationSection(true)
        lateinit var root: ConfigurationSection

        @ExportConfigurationSection
        lateinit var nested: ConfigurationSection

        @SkipConfig
        var skippedRoot: ConfigurationSection? = null

        @ExportConfigurationSection(true)
        var noBackingField: ConfigurationSection?
            set(value) {
                skippedRoot = value
            }
            get() = skippedRoot
    }
}