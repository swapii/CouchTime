plugins {
    declare(libs.plugins.android.application)
    declare(libs.plugins.dagger.hilt.android)
    declare(libs.plugins.protobuf)
    declare(libs.plugins.kotlin.jvm)
    declare(libs.plugins.kotlin.android)
    declare(libs.plugins.kotlin.serialization)
    declare(libs.plugins.kotlin.ksp)
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
