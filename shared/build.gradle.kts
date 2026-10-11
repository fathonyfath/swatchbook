import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "dev.fathony.swatchbook.shared"
        compileSdk {
            version = release(37)
        }
        minSdk = 24
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    jvm("desktop")

    compilerOptions {
        // Clickable ListItem is stable in the androidx release Android uses, but still
        // experimental in the multiplatform port the other targets compile against.
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.material3)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.navigation3.ui)
            implementation(libs.androidx.navigationevent.compose)
        }
        androidMain.dependencies {
            // Android stays on the androidx release the multiplatform port has not reached yet.
            api(libs.androidx.compose.material3)
        }
    }
}

dependencies {
    // Lets Android Studio render the previews in commonMain; not part of what :android consumes.
    "androidRuntimeClasspath"(libs.compose.ui.tooling)
}
