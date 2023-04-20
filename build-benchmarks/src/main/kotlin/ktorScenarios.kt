import org.jetbrains.kotlin.build.benchmarks.dsl.*

fun ktorBenchmarks() =
    suite {
        defaultTasks("help")

        defaultArguments(
            "--no-build-cache",
            "--info",
            "--watch-fs",
            "-x", "apiCheck",
            "-x", "test",
            "-x", "check",
        )

        val parallelArguments = arrayOf(
            "--parallel",
            "--max-workers=4",
        )

        val nonParallelArguments = arrayOf(
            "--no-parallel",
            "--max-workers=1",
        )

        val k2AdditionalArguments = arrayOf(
            "-Pkotlin_language_version=2.0",
            "-Pkotlin_api_version=2.0"
        )

        defaultJdk = System.getenv("JDK_11")

        data class ArgumentsSuit(val name: String, val arguments: Array<String>, val requiresWarmup: Boolean = false)

        val argumentsSuits = listOf(
            ArgumentsSuit("parallel K1", parallelArguments, requiresWarmup = true),
            ArgumentsSuit("non-parallel K1", nonParallelArguments),
            ArgumentsSuit("parallel K2", parallelArguments + k2AdditionalArguments, requiresWarmup = true),
            ArgumentsSuit("non-parallel K2", nonParallelArguments + k2AdditionalArguments),
        )

        for (suit in argumentsSuits) {

            if (suit.requiresWarmup) {
                scenario("parallel clean compile to warmup daemon") {
                    arguments(*suit.arguments)
                    step {
                        doNotMeasure()
                        runTasks("build")
                    }
                    cleanupTasks("clean")
                    repeat = 3U
                }
            }

            fun _scenario(name: String, body: ScenarioBuilder.() -> Unit) {
                scenario("$name (${suit.name})") {
                    arguments(*suit.arguments)
                    body()
                }
            }

            _scenario("Clean compile") {
                step {
                    runTasks("build")
                }
                cleanupTasks("clean")
            }
            scenario("Stop daemon") {
                stopDaemon()
            }
        }
    }
