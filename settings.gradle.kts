rootProject.name="typeboot-executor"

listOf("core", "cassandra", "jdbc").forEach { folder ->
    include(folder)
    project(":${folder}").projectDir = file(folder)
}
