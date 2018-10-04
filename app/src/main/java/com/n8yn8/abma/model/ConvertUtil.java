package com.n8yn8.abma.model;

import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.BSponsor;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.entities.Year;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConvertUtil {

    public static Year convert(BYear bYear) {
        Year year = new Year();
        year.name = bYear.getName();
        year.welcome = bYear.getWelcome();
        year.objectId = bYear.getObjectId();
        year.info = bYear.getInfo();
        return year;
    }

    public static List<Event> convertEvents(List<BEvent> bEvents, String yearId) {
        List<Event> events = new ArrayList<>(bEvents.size());
        for (BEvent bEvent : bEvents) {
            events.add(convert(bEvent, yearId));
        }
        return events;
    }

    public static Event convert(BEvent bEvent, String yearId) {
        Event event = new Event();
        event.title = bEvent.getTitle();
        event.startDate = bEvent.getStartDate().getTime();
        if (bEvent.getEndDate() != null) {
            event.endDate = bEvent.getEndDate().getTime();
        }
        event.yearId = yearId;
        event.subtitle = bEvent.getSubtitle();
        event.place = bEvent.getLocation();
        event.objectId = bEvent.getObjectId();
        event.details = bEvent.getDetails();
        return event;
    }

    public static List<Paper> convertPapers(List<BPaper> bPapers, String eventId) {
        List<Paper> papers = new ArrayList<>(bPapers.size());
        for (BPaper bPaper : bPapers) {
            papers.add(ConvertUtil.convert(bPaper, eventId));
        }
        return papers;
    }

    public static Paper convert(BPaper bPaper, String eventId) {
        Paper paper = new Paper();
        paper.title = bPaper.getTitle();
        paper.author = bPaper.getAuthor();
        paper.eventId = eventId;
        paper.objectId = bPaper.getObjectId();
        paper.order = bPaper.getOrder();
        paper.synopsis = bPaper.getSynopsis();
        return paper;
    }

    public static Note convert(BNote bNote) {
        Note note = new Note();
        note.paperId = bNote.getPaperId();
        note.eventId = bNote.getEventId();
        note.content = bNote.getContent();
        note.created = bNote.getCreated().getTime();
        note.updated = bNote.getCreated().getTime();
        note.objectId = bNote.getObjectId();

        return note;
    }

    public static BNote convert(Note note) {
        BNote bNote = new BNote();
        bNote.setPaperId(note.paperId);
        bNote.setEventId(note.eventId);
        bNote.setContent(note.content);
        bNote.setCreated(new Date(note.created));
        bNote.setUpdated(new Date(note.updated));
        bNote.setObjectId(note.objectId);
        return bNote;
    }

    public static List<Sponsor> convertSponsors(List<BSponsor> bSponsors, String yearId) {
        List<Sponsor> sponsors = new ArrayList<>();
        for (BSponsor bSponsor : bSponsors) {
            sponsors.add(convert(bSponsor, yearId));
        }
        return sponsors;
    }

    public static Sponsor convert(BSponsor bSponsor, String yearId) {
        Sponsor sponsor = new Sponsor();
        sponsor.imageUrl = bSponsor.getImageUrl();
        sponsor.objectId = bSponsor.getObjectId();
        sponsor.url = bSponsor.getUrl();
        sponsor.yearId = yearId;
        return sponsor;
    }
}
