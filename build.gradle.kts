plugins {
    declare(libs.plugins.android.application)
    declare(libs.plugins.kotlin.jvm)
    declare(libs.plugins.kotlin.android)
    declare(libs.plugins.dagger.hilt.android)
    declare(libs.plugins.kotlin.ksp)
    declare(libs.plugins.protobuf)
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
