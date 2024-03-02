plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "gradle-common"
            implementationClass = "common.CommonPlugin"
        }
    }
}
