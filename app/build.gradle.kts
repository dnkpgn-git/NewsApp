import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())
val apiKey = localProperties.getProperty("NEWS_API_KEY")

android {
    namespace = "com.example.news"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.news"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Retrofit и gson (запрос в сеть)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ViewModel и LiveData (для MVVM)
    val lifecycle_version = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Coroutines (корутины)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Glide (загрузка картинок)
    implementation("com.github.bumptech.glide:glide:4.16.0")
}