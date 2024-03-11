buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    id("com.google.gms.google-services") version "4.4.1" apply false
}

dependencies {
//    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
//    implementation("com.google.firebase:firebase-analytics-ktx")
//
//    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("com.google.firebase:firebase-firestore-ktx")
}