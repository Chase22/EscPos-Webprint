import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.0.2"
}

task<NpmTask>("build") {
    dependsOn(tasks.npmInstall)
    args.addAll("run", "build")
}

task<NpmTask>("dev") {
    dependsOn(tasks.npmInstall)
    args.addAll("run", "dev")
}
