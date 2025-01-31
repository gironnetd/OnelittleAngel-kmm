import dev.icerock.gradle.MRVisibility

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.moko.multiplatform)
    alias(libs.plugins.baselineprofile)
}

multiplatformResources {
    multiplatformResourcesPackage = "com.onelittleangel.themes" // required
    multiplatformResourcesClassName = "Resources" // optional, default MR
    multiplatformResourcesVisibility = MRVisibility.Public // optional, default Public
    iosBaseLocalizationRegion = "fr" // optional, default "en"
    multiplatformResourcesSourceSet = "commonMain"  // optional, default "commonMain"
    disableStaticFrameworkWarning = true
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "themes"
            isStatic = true
            binaryOption("bundleId", "com.onelittleangel.themes")
        }
    }

    task("testClasses")

    sourceSets {
        getByName("androidMain") {
            kotlin.srcDir("build/generated/moko/androidMain/src")
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.koin.core)
            implementation(libs.stately.common)
            implementation(libs.molecule.runtime)
            implementation(libs.bundles.precompose)
            implementation(projects.core.model)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.core.datastore)
            implementation(projects.core.cache)
            implementation(projects.core.designsystem)
            implementation(projects.core.ui)
            implementation(projects.core.common)
            api(libs.moko.resources)
            api(libs.moko.resources.compose)
        }
    }
}

android {
    namespace = "com.onelittleangel.themes"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    ndkVersion = "26.2.11394342"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        // Multiple dependency bring these files in. Exclude them to enable
        // our test APK to build (has no effect on our AARs)
        resources.excludes += "/META-INF/AL2.0"
        resources.excludes += "/META-INF/LGPL2.1"
    }

    buildTypes {
        /*getByName("debug") {
            //isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
        }*/

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-rules.pro"
            )
            // In real app, this would use its own release keystore
            signingConfig = signingConfigs.getByName("debug")
        }

        /*create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }*/
    }

    dependencies {
        debugImplementation(libs.androidx.compose.ui.tooling)
        implementation(libs.androidx.profileinstaller)
        androidTestImplementation(libs.androidx.benchmark.macro.junit4)
        //baselineProfile(project(":baselineprofile"))
        //baselineProfile(project(":macrobenchmark"))
    }
}
