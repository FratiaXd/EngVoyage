package com.example.engvoyage;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;

//Manages user activity daily streak
public class StreakManager {
    private static final String PREF_NAME_PREFIX = "StreakPrefs_";
    private static final String KEY_LAST_DATE = "lastDate";
    private static final String KEY_STREAK_COUNT = "streakCount";
    private SharedPreferences preferences;
    private String userId;

    //Constructor to save streak for each user
    public StreakManager(Context context, String userId) {
        this.userId = userId;
        preferences = context.getSharedPreferences(PREF_NAME_PREFIX + userId, Context.MODE_PRIVATE);
    }

    //Gets current streak count
    public int getStreakCount() {
        return preferences.getInt(KEY_STREAK_COUNT, 0);
    }

    //Updates streak
    public void updateStreak() {
        Calendar calendar = Calendar.getInstance();
        int lastDay = preferences.getInt(KEY_LAST_DATE, -1);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        //Checks if todays date is different from the last saved date
        if (lastDay != currentDay) {
            //If the difference is more than a day it means user skipped the day
            if (currentDay - lastDay > 1) {
                resetStreak();      //Resets counter to 1
            } else {
                //If user didnt skip a day increases streak counter
                int streakCount = preferences.getInt(KEY_STREAK_COUNT, 0);
                streakCount++;
                preferences.edit().putInt(KEY_STREAK_COUNT, streakCount).apply();
            }
            preferences.edit().putInt(KEY_LAST_DATE, currentDay).apply();
        }
    }

    //Resets streak
    public void resetStreak() {
        preferences.edit().putInt(KEY_STREAK_COUNT, 1).apply();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        preferences.edit().putInt(KEY_LAST_DATE, currentDay).apply();
    }
}
