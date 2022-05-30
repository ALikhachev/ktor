import org.jetbrains.kotlin.build.benchmarks.*

fun main() {
    mainImpl(ktorBenchmarks(), "../.") // expected working dir is %KTOR_PROJECT_PATH%/build-benchmarks/
}
