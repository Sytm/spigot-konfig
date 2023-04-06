package de.md5lukas.konfig

import de.md5lukas.konfig.builtins.*
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

class Konfig(
    private val customAdapters: List<RegisteredTypeAdapter<out Any>> = emptyList()
) {

    companion object {
        private val instance = Konfig()

        fun deserializeInto(bukkitConfig: ConfigurationSection, configObject: Any) {
            instance.deserializeInto(bukkitConfig, configObject)
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
    )

    fun deserializeInto(bukkitConfig: ConfigurationSection, configObject: Any) {
        visitClass(bukkitConfig, configObject)
    }

    @Suppress("UNCHECKED_CAST")
    private fun visitClass(section: ConfigurationSection, configObject: Any) {
        val clazz = configObject::class

        clazz.memberProperties.forEach { property ->
            property as KProperty1<Any, Any>
            val propertyClass = property.returnType.classifier as? KClass<*>
                ?: throw IllegalStateException("The property ${property.name} in ${clazz.qualifiedName} has a type that is not available in Kotlin")

            val path = property.findAnnotation<ConfigPath>()?.key ?: property.name

            if (!section.contains(path)) {
                throw MissingConfigurationKeyException(section, path)
            }

            if (propertyClass.hasAnnotation<Configurable>()) {
                visitClass(
                    validateNotNull(section, path, section.getConfigurationSection(path)),
                    property.get(configObject)
                )
                return@forEach
            }

            if (property is KMutableProperty1) {
                val useAdapter = property.findAnnotation<UseAdapter>()
                val adapter = if (useAdapter !== null) {
                    useAdapter.adapter.objectInstance
                        ?: throw IllegalArgumentException("The property ${property.name} in ${clazz.qualifiedName} has an custom TypeAdapter that is not an object")
                } else {
                    val typeArgument = property.returnType.arguments.firstOrNull()?.type?.classifier as? KClass<*>
                    getTypeAdapter(propertyClass, typeArgument)
                        ?: throw IllegalArgumentException("Could not find a TypeAdapter for ${property.name} in ${clazz.qualifiedName}")
                }

                val value = adapter.get(section, path)

                if (value === null && !property.returnType.isMarkedNullable) {
                    throw NullPointerException("The returned value for ${property.name} in ${clazz.qualifiedName} is null for a non-null type")
                }

                property.setter.call(configObject, value)
                return@forEach
            }
        }
    }

    private fun getTypeAdapter(clazz: KClass<*>, firstTypeArgumentClass: KClass<*>?): TypeAdapter<*>? =
        customAdapters.firstOrNull { it.isApplicable(clazz, firstTypeArgumentClass) }
            ?: builtInTypes.firstOrNull { it.isApplicable(clazz, firstTypeArgumentClass) }
}