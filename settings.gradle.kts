rootProject.name="typeboot-executor"

listOf("core", "cassandra", "com.typeboot.executor.jdbc").forEach { folder ->
    include(folder)
    project(":${folder}").projectDir = file(folder)
}
