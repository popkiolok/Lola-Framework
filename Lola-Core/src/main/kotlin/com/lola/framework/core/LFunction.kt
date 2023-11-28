package com.lola.framework.core

import kotlin.reflect.KFunction

interface LFunction<R> : LCallable<R>, KFunction<R>