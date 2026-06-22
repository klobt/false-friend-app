plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // TODO libs.versions.toml
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
}
