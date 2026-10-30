package com.qiuyinlan.suixinji

import org.junit.Assert.assertEquals
import org.junit.Test

class EditorTagTest {
    @Test fun lineStartHandlesNewlineAtBeginning() {
        assertEquals(0, editorLineStart("\n", 0))
        assertEquals(0, editorLineStart("hello\n", 5))
        assertEquals(6, editorLineStart("hello\nworld\n", 11))
    }

    @Test fun bookTitlesRemainPartOfHierarchicalTags() {
        val path = "reading/\u300aBook\u300b/notes"
        assertEquals(listOf(path), NoteTagRegex.findAll("#$path ").map { it.groupValues[1] }.toList())
    }

    @Test fun nestedTagsRemainOneTag() {
        assertEquals(listOf("a/b/c"), NoteTagRegex.findAll("#a/b/c ").map { it.groupValues[1] }.toList())
    }
}
