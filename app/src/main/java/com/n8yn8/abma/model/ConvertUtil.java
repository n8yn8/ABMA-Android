package com.n8yn8.abma.model;

import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BSponsor;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.entities.Year;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {

    public static Year convert(BYear bYear) {
        Year year = new Year();
        //TODO
        return year;
    }

    public static Note convert(BNote bNote) {
        Note note = new Note();
        //TODO
        return note;
    }

    public static BNote convert(Note note) {
        BNote bNote = new BNote();
        //TODO
        return bNote;
    }

    public static List<Sponsor> convert(List<BSponsor> bSponsors, String yearId) {
        List<Sponsor> sponsors = new ArrayList<>();
        for (BSponsor bSponsor : bSponsors) {
            sponsors.add(convert(bSponsor));
        }
        return sponsors;
    }

    public static Sponsor convert(BSponsor bSponsor) {
        Sponsor sponsor = new Sponsor();
        //TODO
        return sponsor;
    }
}
