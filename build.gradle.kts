plugins {
    declare(libs.plugins.android.application)
    declare(libs.plugins.kotlin.jvm)
    declare(libs.plugins.kotlin.android)
    declare(libs.plugins.dagger.hilt.android)
    declare(libs.plugins.ksp)
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
