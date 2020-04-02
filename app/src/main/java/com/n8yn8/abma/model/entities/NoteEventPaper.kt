package com.n8yn8.abma.model.entities

import androidx.room.Embedded
import androidx.room.Relation

data class NoteEventPaper(
        @Embedded val note: Note,
        @Relation(parentColumn = "event_id", entityColumn = "object_id") val event: Event,
        @Relation(parentColumn = "paper_id", entityColumn = "object_id") val paper: Paper?
)