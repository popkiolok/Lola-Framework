package com.lola.framework.event

/**
 * Allows controlling executors call order. [EventListener]s with higher
 * [Priority] called firstly. If the [Event] is cancelled
 * executors with not enough high priorities might not be called.
 */
enum class Priority {
	HIGHEST, HIGH, DEFAULT, LOW, LOWEST
}