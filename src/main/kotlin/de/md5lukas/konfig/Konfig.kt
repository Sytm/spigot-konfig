package de.md5lukas.konfig

import de.md5lukas.konfig.builtins.*
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Helper class to deserialize Bukkit configurations into any config instance with reflection.
 *
 * By default the following types are supported:
 * - String
 * - Boolean
 * - Byte
 * - Short
 * - Int
 * - Long
 * - Float
 * - Double
 *  - These types as List<>
 * - List<Char>
 * - Enums
 * - List<Enums>
 *
 * @constructor Creates a new Konfig instance with additional custom [RegisteredTypeAdapter]s
 * @property customAdapters The adapters to register
 */
class Konfig(
    private val customAdapters: List<RegisteredTypeAdapter<out Any>>
) {

    companion object {
        private val instance = Konfig(emptyList())

        /**
         * Deserializes the provided [ConfigurationSection] into the config instance.
         */
        fun deserializeInto(bukkitConfig: ConfigurationSection, configInstance: Any) {
            instance.deserializeInto(bukkitConfig, configInstance)
        }
    }

    private val builtInTypes = listOf(
        StringAdapter,
        BooleanAdapter,
        ByteAdapter,
        ShortAdapter,
        IntAdapter,
        LongAdapter,
        FloatAdapter,
        DoubleAdapter,
        StringListAdapter,
        BooleanListAdapter,
        ByteListAdapter,
        ShortListAdapter,
        IntListAdapter,
        CharListAdapter,
        LongListAdapter,
        FloatListAdapter,
        DoubleListAdapter,
        EnumAdapter,
        EnumListAdapter,
    )

    /**
     * Deserializes the provided [ConfigurationSection] into the config instance taking custom [RegisteredTypeAdapter]s
     * into account.
     */
    fun deserializeInto(bukkitConfig: ConfigurationSection, configInstance: Any) {
        visitClass(bukkitConfig, configInstance)
    }

    @Suppress("UNCHECKED_CAST")
    private fun visitClass(section: ConfigurationSection, configInstance: Any) {
        val clazz = configInstance::class

        clazz.memberProperties.forEach { property ->
            property as KProperty1<Any, Any>
            val propertyClass = property.returnType.classifier as? KClass<*>
                ?: throw IllegalStateException("The property ${property.name} in ${clazz.qualifiedName} has a type that is not available in Kotlin")

            if (property.hasAnnotation<SkipConfig>()) {
                return@forEach
            }

            val exportConfigurationSection = property.findAnnotation<ExportConfigurationSection>()?.root

            if (exportConfigurationSection !== null) {
                if (property is KMutableProperty1) {
                    property.forcedSetterSet(
                        configInstance, if (exportConfigurationSection) {
                            section.root!!
                        } else {
                            section
                        }
                    )
                } else {
                    throw IllegalArgumentException("The property ${property.name} in ${clazz.qualifiedName} must be mutable to receive the ConfigurationSection")
                }
            }

            val path = property.findAnnotation<ConfigPath>()?.path ?: property.name

            if (!section.contains(path)) {
                throw MissingConfigurationKeyException(section, path)
            }

            if (propertyClass.hasAnnotation<Configurable>()) {
                visitClass(
                    validateNotNull(section, path, section.getConfigurationSection(path)),
                    property.get(configInstance)
                )
                return@forEach
            }

            if (property is KMutableProperty1) {
                val useAdapter = property.findAnnotation<UseAdapter>()
                val typeArguments = property.returnType.arguments.mapNotNull { it.type?.classifier as? KClass<*> }
                val adapter = if (useAdapter !== null) {
                    useAdapter.adapter.objectInstance
                        ?: throw IllegalArgumentException("The property ${property.name} in ${clazz.qualifiedName} has an custom TypeAdapter that is not an object")
                } else {
                    getTypeAdapter(propertyClass, typeArguments)
                        ?: throw IllegalArgumentException("Could not find a TypeAdapter for ${property.name} in ${clazz.qualifiedName}")
                }

                val value = if (adapter is RegisteredTypeAdapter) {
                    adapter.get(section, path, propertyClass, typeArguments)
                } else {
                    adapter.get(section, path)
                }

                if (value === null && !property.returnType.isMarkedNullable) {
                    throw NullPointerException("The returned value for ${property.name} in ${clazz.qualifiedName} is null for a non-null type")
                }

                property.forcedSetterSet(configInstance, value)
                return@forEach
            }
        }
    }

    private fun getTypeAdapter(clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>): TypeAdapter<*>? =
        customAdapters.firstOrNull { it.isApplicable(clazz, typeArgumentClasses) }
            ?: builtInTypes.firstOrNull { it.isApplicable(clazz, typeArgumentClasses) }

    private fun <T, V> KMutableProperty1<out T, out V>.forcedSetterSet(instance: T, value: V) {
        if (!setter.isAccessible) {
            setter.isAccessible = true
        }

        setter.call(instance, value)
    }
}