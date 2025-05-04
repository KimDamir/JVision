import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.okhttp)
            implementation(libs.com.google.code.gson.gson2)
            implementation(libs.androidx.core)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.ktx)
            implementation(libs.androidx.fragment.ktx)
            implementation(libs.androidx.lifecycle.service)
            implementation (libs.kotlinx.serialization.json)
            implementation (libs.androidx.datastore.preferences.rxjava2)
            implementation (libs.androidx.datastore.preferences.rxjava3)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3)
            implementation(libs.kotlinx.coroutines.core)
        }
        desktopMain.dependencies {
            implementation(libs.sonner.v038)
            implementation(compose.desktop.currentOs)
            implementation(libs.system.hook)
            implementation(libs.okhttp)
            implementation(libs.com.google.code.gson.gson2)
            implementation(libs.androidx.lifecycle.livedata.core.ktx)
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = 35
    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE.md"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.databinding.compiler)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.lifecycle.livedata.core.ktx)
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("jdk.unsupported")
            modules("jdk.unsupported.desktop")

            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
