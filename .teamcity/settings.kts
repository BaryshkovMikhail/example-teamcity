import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.07"

project {
    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target/*.jar => artifacts"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        // Шаг для master: deploy
        maven {
            id = "MavenDeploy"
            name = "Maven Deploy"
            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }

        // Шаг для остальных веток: test
        maven {
            id = "MavenTest"
            name = "Maven Test"
            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }

    // Пункт 16: артефакты сборки
    artifactRules = "target/*.jar => artifacts"
})