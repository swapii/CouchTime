plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "common-version-catalog"
            implementationClass = "gvcs.GvcsSettingsPlugin"
        }
    }
}

dependencies {
    implementation("com.github.swapii.gvcs:gvcs-gradle-plugin:603230b9")
}
