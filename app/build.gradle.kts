plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ma.ensa.comptes"
    compileSdk = 34

    defaultConfig {
        applicationId = "ma.ensa.comptes"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX and Material Design libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")

    // OkHttp for logging network requests and responses
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Gson for JSON serialization/deserialization
    implementation("com.google.code.gson:gson:2.8.9")

    // Simple XML for XML serialization
    implementation("org.simpleframework:simple-xml:2.7.1")

    // Unit testing dependencies
    // Unit testing dependencies
    testImplementation(libs.junit)
// Jackson Core and Databind for general JSON processing
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

// Jackson Dataformat for XML processing
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")

    // Android testing dependencies
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
