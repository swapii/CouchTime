import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependency
import org.gradle.plugin.use.PluginDependencySpec

@Suppress("NOTHING_TO_INLINE")
inline fun PluginDependenciesSpec.plugin(
    pluginProvider: Provider<PluginDependency>,
): PluginDependencySpec =
    id(pluginProvider.get().pluginId)

@Suppress("NOTHING_TO_INLINE")
inline fun PluginDependenciesSpec.declare(
    pluginProvider: Provider<PluginDependency>,
) {
    alias(pluginProvider) apply false
}
