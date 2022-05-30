import org.jetbrains.kotlin.build.benchmarks.dsl.*

fun ktorBenchmarks() =
    suite {
        defaultTasks("help")

        defaultArguments(
            "--no-build-cache",
            "--info",
            "--watch-fs",
        )

        val parallelArguments = arrayOf(
            "--parallel",
            "--max-workers=4",
        )

        val nonParallelArguments = arrayOf(
            "--no-parallel",
            "--max-workers=1",
        )

        defaultJdk = System.getenv("JDK_11")

        scenario("parallel clean compile to warmup daemon") {
            arguments(*parallelArguments)
            step {
                doNotMeasure()
                runTasks("assembleAllKotlin")
            }
            cleanupTasks("clean")
        }

        for ((isParallel, arguments) in listOf(true to parallelArguments, false to nonParallelArguments)) {
            fun _scenario(name: String, body: ScenarioBuilder.() -> Unit) {
                scenario("$name (${if (isParallel) "parallel" else "non-parallel"})") {
                    arguments(*arguments)
                    body()
                }
            }

            _scenario("Clean compile") {
                step {
                    runTasks("assembleAllKotlin")
                }
                cleanupTasks("clean")
            }
        }
    }
