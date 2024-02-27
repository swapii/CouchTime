plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass = "MainKt"
}

dependencies {
    implementation(projects.common.m3u)
}
