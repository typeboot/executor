tasks.wrapper {
    gradleVersion = "6.2.2"
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "java")


    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }

}

subprojects {
    val sourcesJar by tasks.registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
    }


    val fatJar by tasks.registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        from(project.configurations["runtimeClasspath"].files.map{  zipTree(it)})
        from(project.the<SourceSetContainer>()["main"].output)
        archiveClassifier.set("uber")
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", fatJar)
    }
}