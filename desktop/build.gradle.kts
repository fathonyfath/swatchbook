plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "dev.fathony.swatchbook.MainKt"
        // Skia is loaded through JNI, which newer JDKs warn about unless allowed.
        jvmArgs += "--enable-native-access=ALL-UNNAMED"
    }
}
