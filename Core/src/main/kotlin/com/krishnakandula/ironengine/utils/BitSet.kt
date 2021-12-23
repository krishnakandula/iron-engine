package com.krishnakandula.ironengine.utils

class BitSet : Cloneable {

    private val words: LongArray = LongArray(4)

    fun get(index: Int): Boolean {
        return if (index >= size()) {
            false
        } else {
            val word: Long = words[(index / Long.SIZE_BITS)]
            val bitIndex: Int = index % Long.SIZE_BITS
            val mask: Long = 1L shl bitIndex

            return (word and mask) != 0L
        }
    }

    fun set(index: Int) {
        val bitIndex: Int = index % Long.SIZE_BITS
        val mask: Long = 1L shl bitIndex
        val wordIndex: Int = index / Long.SIZE_BITS

        words[wordIndex] = words[wordIndex] or mask
    }

    fun clear(index: Int) {
        val wordIndex: Int = index / Long.SIZE_BITS
        val bitIndex: Int = index % Long.SIZE_BITS
        val mask: Long = (1L shl bitIndex).inv()

        words[wordIndex] = words[wordIndex] and mask
    }

    fun isSubsetOf(other: BitSet): Boolean {
        for (i in 0..words.lastIndex) {
            val word = words[i]
            val otherWord = other.getWord(i)

            if (otherWord == null) {
                if (word > 0) {
                    return false
                }
            } else {
                if (word and otherWord != word) {
                    return false
                }
            }
        }

        return true
    }

    public override fun clone(): BitSet {
        val clone = BitSet()
        for (i in 0..words.lastIndex) {
            clone.words[i] = words[i]
        }

        return clone
    }

    private fun getWord(wordIndex: Int): Long? {
        return if (wordIndex >= words.size) {
            null
        } else {
            words[wordIndex]
        }
    }

    private fun size(): Int = words.size * Long.SIZE_BITS

    override fun hashCode(): Int = words.contentHashCode()

    override fun equals(other: Any?): Boolean {
        return if (other == null) {
            false
        } else {
            when (other) {
                is BitSet -> other.words.contentEquals(words)
                else -> false
            }
        }
    }
}
