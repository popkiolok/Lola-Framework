package com.lola.framework.core.property

/**
 * Interface for retrieving the property name based on the accessor (getter or setter) name.
 */
interface PropertyNameRetriever {
    /**
     * Retrieves the property name by the given accessor name.
     *
     * @param accessorName The accessor name.
     * @return The property name.
     */
    fun getPropertyName(accessorName: String): String
}