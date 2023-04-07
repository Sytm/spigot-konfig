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
 * - Char
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
        CharAdapter,
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

    private val adapterCache = mutableMapOf<KClass<*>, TypeAdapter<out Any>>()

    /**
     * Deserializes the provided [ConfigurationSection] into the config instance taking custom [RegisteredTypeAdapter]s
     * into account.
     */
    fun deserializeInto(bukkitConfig: ConfigurationSection, configInstance: Any) {
        try {
            visitClass(bukkitConfig, configInstance)
        } finally {
            adapterCache.clear()
        }
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

            val path = property.findAnnotation<ConfigPath>()?.path ?: property.name
            val exportConfigurationSection = property.findAnnotation<ExportConfigurationSection>()?.root

            if (exportConfigurationSection !== null) {
                if (property !is KMutableProperty1) {
                    throw IllegalArgumentException("The property ${property.name} in ${clazz.qualifiedName} must be mutable to receive the ConfigurationSection")
                }
                if (propertyClass != ConfigurationSection::class) {
                    throw IllegalArgumentException("The property ${property.name} in ${clazz.qualifiedName} must be of type ConfigurationSection")
                }

                property.forcedSetterSet(
                    configInstance, if (exportConfigurationSection) {
                        section.root!!
                    } else {
                        section.getConfigurationSection(path)
                    }
                )
                return@forEach
            }


            if (propertyClass.hasAnnotation<Configurable>()) {
                if (!section.contains(path)) {
                    throw MissingConfigurationKeyException(section, path)
                }
                visitClass(
                    validateNotNull(section, path, section.getConfigurationSection(path)),
                    property.get(configInstance)
                )
                return@forEach
            }

            if (property is KMutableProperty1) {
                if (!section.contains(path)) {
                    if (property.returnType.isMarkedNullable) {
                        property.forcedSetterSet(configInstance, null)
                        return@forEach
                    } else {
                        throw MissingConfigurationKeyException(section, path)
                    }
                }

                val useAdapter = property.findAnnotation<UseAdapter>()
                val typeArguments = property.returnType.arguments.mapNotNull { it.type?.classifier as? KClass<*> }
                val adapter = if (useAdapter !== null) {
                    getAdapterInstance(useAdapter.adapter)
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

    @Suppress("UNCHECKED_CAST")
    private fun getAdapterInstance(clazz: KClass<*>): TypeAdapter<out Any> = adapterCache.computeIfAbsent(clazz) {
        val constructor = it.constructors.firstOrNull { constructor -> constructor.parameters.isEmpty() }
            ?: throw IllegalArgumentException("The class ${clazz.qualifiedName} has no zero-parameter constructor")

        if (!constructor.isAccessible) {
            constructor.isAccessible = true
        }

        constructor.call() as TypeAdapter<out Any>
    }
}