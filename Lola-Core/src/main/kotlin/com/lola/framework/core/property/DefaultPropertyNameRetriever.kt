package com.lola.framework.core.property

object DefaultPropertyNameRetriever : PropertyNameRetriever {
    private val accessorRegex = Regex("(get|set|is)?(\\p{Lu}.+|_.+|.+)", RegexOption.IGNORE_CASE)

    override fun getPropertyName(accessorName: String): String {
        return accessorRegex.matchEntire(accessorName)?.groups?.get(2)?.value?.removePrefix("_")
            ?.replaceFirstChar { it.uppercaseChar() } ?: accessorName
    }
}