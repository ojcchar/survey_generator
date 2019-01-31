package output;

import java.util.*;

public class AppNamesMappings {

    // the first element of each set is the representative/main one
    static List<LinkedHashSet<String>> APP_NAMES = new ArrayList<>();

    static {
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Aard Dictionary", "Aard-Dictionary", "aarddict", "Aard")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Mileage", "android-mileage")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Time Tracker", "A Time Tracker", "A-Time-Tracker",
                "TimeTracker",
                "ATimeTracker")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Card Game Scores", "cardgamescores", "CardGameScores",
                "Card Games", "Cardgames", "Card-Game-Scores")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Car Report", "Car-Report", "car-report")));
        APP_NAMES.add(new LinkedHashSet<>(
                Arrays.asList("Document Viewer", "Document-Viewer", "document-viewer", "Dokumentenbetrachter",
                        "Document Viewer", "DocumentViewer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("DroidWeight", "droidweight", "Droid Weight")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("GnuCash", "gnucash-android", "GnuCash - beta")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("NetMBuddy", "netmbuddy")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("NotePad", "notepad_banderlabs")));
        APP_NAMES.add(
                new LinkedHashSet<>(Arrays.asList("Schedule", "schedule-campfahrplan", "31C3 Schedule", "Camp 2015")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Android Comic Viewer", "droid-comic-viewer", "ACV")));

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Android-Token", "Android Token", "Android token",
                "androidtoken", "Android-token")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("RedReader", "redreader")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("ODK-Collect", "ODK Collect", "collect")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("KeePassDroid", "keepassdroid")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Typpy-Tipper", "Typpy Tipper", "tippytipper")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Omni-notes", "omni-notes")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Amaze", "amaze")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Vanilla", "vanilla")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("AntennaPod", "antennapod")));

        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Box Android SDK", "box-android-sdk")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Exo Player", "ExoPlayer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Japanese Traditional Time", "jtt_android")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("K-9 Mail", "k-9")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Twidere", "Twidere-Android")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("NextCloud", "nexcloud")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("BeHe ExploreR", "behe-explorer", "BeHe Explorer")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Tachiyomi", "tachiyomi")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("Android D-B-MVP", "android-d-b-mvp")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("OCReader", "ocreader")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("AnkiDroid", "Anki-Android")));
        APP_NAMES.add(new LinkedHashSet<>(Arrays.asList("ScreenRecorder", "screenrecorder")));


    }

    public static String normalizeAppName(String appName) {
        LinkedHashSet<String> appNames = getAppNames(appName);
        if (appNames == null) return null;
        return new ArrayList<>(appNames).get(0);
    }

    public static LinkedHashSet<String> getAppNames(String appName) {
        Optional<LinkedHashSet<String>> appNameSet = APP_NAMES.stream().filter(aps -> aps.contains(appName))
                .findFirst();

        if (!appNameSet.isPresent()) {
            return null;
        }

        LinkedHashSet<String> appNames = appNameSet.get();
        return appNames;
    }

    //-----------------------------------------------

    private static final HashMap<String, List<String>> APP_NAMES_PACKAGES;

    static {
        APP_NAMES_PACKAGES = new LinkedHashMap<>();

        APP_NAMES_PACKAGES.put("aarddict", Arrays.asList("aarddict.android"));
        APP_NAMES_PACKAGES.put("adsdroid", Arrays.asList("hu.vsza.adsdroid"));
        APP_NAMES_PACKAGES.put("AnagramSolver", Arrays.asList("com.as.anagramsolver"));
        APP_NAMES_PACKAGES.put("ATimeTracker", Arrays.asList("com.markuspage.android.atimetracker", "net.ser1" +
                ".timetracker"));

        APP_NAMES_PACKAGES.put("BMI_Calculator", Arrays.asList("com.zola.bmi"));

        APP_NAMES_PACKAGES.put("car-report", Arrays.asList("me.kuehle.carreport"));
        APP_NAMES_PACKAGES.put("cardgamescores", Arrays.asList("org.systemcall.scores"));

        APP_NAMES_PACKAGES.put("document-viewer", Arrays.asList("org.sufficientlysecure.viewer"));
        APP_NAMES_PACKAGES.put("droid-comic-viewer", Arrays.asList("net.androidcomics.acv"));
        APP_NAMES_PACKAGES.put("droidweight", Arrays.asList("de.delusions.measure"));

        APP_NAMES_PACKAGES.put("eyeCam", Arrays.asList("ch.hsr.eyecam"));

        APP_NAMES_PACKAGES.put("gnucash-android", Arrays.asList("org.gnucash.android"));

        APP_NAMES_PACKAGES.put("mileage", Arrays.asList("com.evancharlton.mileage"));
        APP_NAMES_PACKAGES.put("android-mileage", Arrays.asList("com.evancharlton.mileage"));

        APP_NAMES_PACKAGES.put("netmbuddy", Arrays.asList("free.yhc.netmbuddy"));
        APP_NAMES_PACKAGES.put("notepad_banderlabs", Arrays.asList("bander.notepad"));

        APP_NAMES_PACKAGES.put("Olam", Arrays.asList("com.olam"));
        APP_NAMES_PACKAGES.put("openintents", Arrays.asList("org.openintents.notepad"));

        APP_NAMES_PACKAGES.put("schedule-campfahrplan",
                Arrays.asList("nerd.tuxmobil.fahrplan.camp", "nerd.tuxmobil.fahrplan.congress"));
    }



}
