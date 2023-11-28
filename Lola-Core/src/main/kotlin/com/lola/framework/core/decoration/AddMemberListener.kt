package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.LFunction

/**
 * Makes decoration listening for adding members to class.
 * When [AddMemberListener] decoration applied to class,
 * [onMemberAdded] will be called for every existing member in it.
 */
interface AddMemberListener<T : Any> : Decoration<LClass<T>> {
    /**
     * This function is called when a member is added to the class.
     *
     * @param member The member that was added.
     */
    fun onMemberAdded(member: LCallable<*>)
}