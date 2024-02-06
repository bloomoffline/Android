plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
//    iosX64()
    iosArm64()
//    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.coroutine.core)
            implementation(libs.serialization)
            implementation(libs.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(project(":entities"))
            implementation(project(":database"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.annotation.jvm)
            implementation(libs.android.appcompat)
            implementation(libs.signal.client.android)
            implementation(libs.signal.client.java)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.lab.tb.distributed.chat"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}