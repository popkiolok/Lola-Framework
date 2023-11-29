package com.lola.framework.core.util

/**
 * Represents a path consisting of nodes.
 *
 * @param NODE the type of the nodes in the path.
 */
open class Path<NODE>(nodes: Collection<NODE>) {

    /**
     * The parent path, or null if this path contains only one node.
     */
    val parent: Path<NODE>?
        get() = if (nodes.size <= 1) null else Path(nodes.subList(0, nodes.lastIndex))

    /**
     * The last node in the path.
     *
     * @throws NoSuchElementException If the path is empty.
     */
    val lastNode: NODE
        get() = nodes.last()

    private val nodes: List<NODE> = ArrayList(nodes)
    private val stringRepresentation: String by lazy { nodes.joinToString(".") }

    /**
     * Connects two paths into one.
     *
     * @param another The second path to be connected.
     * @return A new [Path] with [another] path as a child of this path.
     */
    operator fun plus(another: Path<NODE>): Path<NODE> {
        return Path(this.nodes + another.nodes)
    }

    /**
     * Connects this path with a node.
     *
     * @param next The node to add to the new path.
     * @return A new [Path] with [next] as the last node and this path as the parent.
     */
    operator fun plus(next: NODE): Path<NODE> {
        return Path(this.nodes + next)
    }

    /**
     * Returns a string representation of the path with "." as the delimiter.
     */
    override fun toString(): String {
        return stringRepresentation
    }

    /**
     * Returns a string representation of the path.
     *
     * @param delimiter The delimiter to use between path nodes.
     * @return A string representation of the path.
     */
    fun toString(delimiter: String): String {
        return nodes.joinToString(delimiter)
    }

    /**
     * Checks if the path contains a specific node.
     *
     * @param node The node to check.
     * @return true if the path contains the node, false otherwise.
     */
    fun contains(node: NODE): Boolean {
        return nodes.contains(node)
    }

    /**
     * Returns the length of the path.
     *
     * @return The number of nodes in the path.
     */
    val length: Int
        get() = nodes.size

    override fun equals(other: Any?): Boolean {
        if (other === null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other !is Path<*> || other.length != length) {
            return false
        }
        nodes.forEachIndexed { index, node ->
            if (other.nodes[index] != node) {
                return false
            }
        }
        return true
    }

    private val hash: Int by lazy {
        var temp = nodes.size
        for (node in nodes) {
            temp = temp xor node.hashCode()
        }
        temp
    }

    override fun hashCode(): Int {
        return hash
    }
}

private val EMPTY_PATH: Path<*> = Path(emptyList<Any>())

@Suppress("UNCHECKED_CAST")
fun <NODE> emptyPath(): Path<NODE> = EMPTY_PATH as Path<NODE>