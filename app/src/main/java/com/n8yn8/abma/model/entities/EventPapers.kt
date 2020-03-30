package com.n8yn8.abma.model.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EventPapers(
        @Embedded val event: Event,
        @Relation(parentColumn = "object_id", entityColumn = "event_id") val papers: List<Paper>
)