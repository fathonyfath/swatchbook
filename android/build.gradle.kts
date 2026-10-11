plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// release.sh appends the number of commits to this for each release.
val baseVersion = "1.0"

android {
    namespace = "dev.fathony.swatchbook"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev.fathony.swatchbook"
        minSdk = 24
        targetSdk = 37
        // release.sh passes the real ones; anything else is a development build.
        versionCode =
            providers.gradleProperty("releaseVersionCode").map { it.toInt() }.getOrElse(1)
        versionName = providers.gradleProperty("releaseVersionName").getOrElse("$baseVersion-dev")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                keepRules {
                    // The default file's broad keep rules target XML layouts, which we don't
                    // have, and its -dontwarn androidx.** would hide the missing-class errors
                    // that make the dependency exclusions below safe.
                    includeDefault = false
                }
            }
            // Smaller APK, at the cost of slightly slower first launches.
            baselineProfile { ignoreFromAllExternalDependencies = true }
            vcsInfo { include = false }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    androidResources { localeFilters += "en" }
    dependenciesInfo { includeInApk = false }
    packaging {
        resources {
            // Licenses, version markers, kotlin_builtins (only needed by kotlin-reflect) and
            // coroutine debug probes. META-INF/services stays: coroutines loads from it.
            excludes += setOf(
                "META-INF/*.version",
                "META-INF/*.kotlin_module",
                "META-INF/androidx/**",
                "kotlin/**",
                "DebugProbesKt.bin",
            )
        }
    }
}

// Artifacts the Compose libraries declare but this app never reaches. With the default keep
// rules off, R8 fails the build on any missing class that live code references, so a passing
// release build proves nothing reachable was removed. Release classpaths only: debug builds and
// previews are not checked by R8, and lint's own classpath needs Guava.
configurations.matching {
    it.name == "releaseRuntimeClasspath" || it.name == "releaseCompileClasspath"
}.configureEach {
    listOf(
        "androidx.autofill",
        "androidx.concurrent",
        "androidx.documentfile",
        "androidx.dynamicanimation",
        "androidx.graphics",
        "androidx.legacy",
        "androidx.loader",
        "androidx.localbroadcastmanager",
        "androidx.print",
        "androidx.profileinstaller",
        "androidx.startup",
        "androidx.transition",
        "androidx.versionedparcelable",
        "com.google.guava",
        "org.jetbrains.kotlinx" to "kotlinx-serialization-core",
        "androidx.lifecycle" to "lifecycle-process",
        "androidx.lifecycle" to "lifecycle-livedata",
        "androidx.lifecycle" to "lifecycle-livedata-core",
        "androidx.lifecycle" to "lifecycle-livedata-core-ktx",
    ).forEach {
        if (it is Pair<*, *>) exclude(group = it.first as String, module = it.second as String)
        else exclude(group = it as String)
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
