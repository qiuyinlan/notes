package com.qiuyinlan.suixinji

import org.junit.Assert.assertEquals
import org.junit.Test

class EditorTagTest {
    @Test fun bookTitlesRemainPartOfHierarchicalTags() {
        val path = "reading/\u300aBook\u300b/notes"
        assertEquals(listOf(path), NoteTagRegex.findAll("#$path ").map { it.groupValues[1] }.toList())
    }

    @Test fun nestedTagsRemainOneTag() {
        assertEquals(listOf("a/b/c"), NoteTagRegex.findAll("#a/b/c ").map { it.groupValues[1] }.toList())
    }
}
