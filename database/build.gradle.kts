plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "database"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.sql.common)
            implementation(projects.entities)
        }
        androidMain.dependencies {
            implementation(libs.sql.android)
            //put your android dependencies here
        }

        iosMain.dependencies {
            implementation(libs.sql.ios)
            //put your ios dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

sqldelight {
    database("BloomDatabase") {
        packageName = "com.lab.tb.distributed.database"
        sourceFolders = listOf("kotlin")
        schemaOutputDirectory = file("build/generated/sqldelight/test_schema")
    }
}

android {
    namespace = "com.lab.tb.distributed.database"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}