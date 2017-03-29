package com.moodswing.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.moodswing.R;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.widget.DateBlock;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Matthew on 2017-03-28.
 */

public abstract class Journal extends MoodSwingActivity {

    public List<DateBlock> dBlocks = new ArrayList<>();
    public List<Capture> captures = new ArrayList<>();

    public Drawable setEmoji(String emotion) {
        switch (emotion) {
            case "RELAXED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.relaxed_emoji, null);
            case "SMILEY":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.smiley_emoji, null);
            case "LAUGHING":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.laughing_emoji, null);
            case "WINK":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.wink_emoji, null);
            case "SMIRK":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.smirk_emoji, null);
            case "KISSING":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.kissing_emoji, null);
            case "STUCK_OUT_TONGUE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.stuck_out_tongue_emoji, null);
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.stuck_out_tongue_winking_eye_emoji, null);
            case "DISAPPOINTED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.disappointed_emoji, null);
            case "RAGE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.rage_emoji, null);
            case "SCREAM":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.scream_emoji, null);
            case "FLUSHED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.flushed_emoji, null);
            default: // UNKNOWN
                return ResourcesCompat.getDrawable(getResources(), R.drawable.blank_emoji, null);
        }
    }

    public int setColor(String emotion) {
        switch (emotion) {
            case "RELAXED":
                return 0xFFFFF696;
            case "SMILEY":
                return 0xFFFCEF62;
            case "LAUGHING":
                return 0xFFFFAB5A;
            case "WINK":
                return 0xFF8AC980;
            case "SMIRK":
                return 0xFFB26731;
            case "KISSING":
                return 0xFFFF9696;
            case "STUCK_OUT_TONGUE":
                return 0xFFEA9550;
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return 0xFFEA9550;
            case "DISAPPOINTED":
                return 0xFFBFFCFF;
            case "RAGE":
                return 0xFFFF912D;
            case "SCREAM":
                return 0xFFD39EFF;
            case "FLUSHED":
                return 0xFFFF3F3F;
            default: // UNKNOWN
                return 0xFFFFFFFF;
        }
    }

    public String setJournalViewDateFormat(String date){
        DateFormat firstdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat secdf = new SimpleDateFormat("MMM.d, yyyy");
        Date tempDate;
        String rDate = "";
        try {
            tempDate = firstdf.parse(date);
            rDate = secdf.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rDate;
    }
}