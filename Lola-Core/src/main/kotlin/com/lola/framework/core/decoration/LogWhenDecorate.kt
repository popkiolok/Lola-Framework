package com.lola.framework.core.decoration

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import java.lang.String.format
import kotlin.reflect.full.isSubclassOf

/**
 * Log when annotated decoration or decoration extending annotated class applied to something.
 *
 * @property logger Name of logger that will be used to log.
 * @property level Logging level.
 * @property pattern Message pattern. `{decoration}` in pattern will be replaced with string representation of the
 * decoration object. `{target}` will be replaced with string representation of [Decorated] decoration applied to.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogWhenDecorate(
    val logger: String,
    val level: Level = Level.INFO,
    val pattern: String = "Found {decoration} ({target})."
)

/**
 * [LClass] decoration that implements behavior expected for classes annotated with [LogWhenDecorate].
 */
@ForAnnotated(LogWhenDecorate::class)
class DecorateLogger<T : Decoration<*>>(target: LClass<T>, private val ann: LogWhenDecorate) :
    DecorationClass<T>(target) {
    private val logger by lazy { KotlinLogging.logger(ann.logger) }

    init {
        Lola.decorate(object : DecorateElementListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun onDecoratedElement(decoration: Decoration<LAnnotatedElement>) {
                if (decoration::class.isSubclassOf(target.self)) {
                    logger.at(ann.level) {
                        message = ann.pattern
                            .replace("{decoration}", decoration.toString())
                            .replace("{target}", decoration.target.toString())
                    }
                }
            }
        })
    }
}