plugins {

    val kotlin_version: String by System.getProperties()
    val spring_boot_version: String by System.getProperties()


    id("org.springframework.boot") version spring_boot_version apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version kotlin_version apply false
    kotlin("plugin.spring") version kotlin_version apply false
    kotlin("plugin.jpa") version kotlin_version apply false
}
