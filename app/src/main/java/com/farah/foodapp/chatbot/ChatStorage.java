package com.farah.foodapp.chatbot;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatStorage {

    private static final String PREF_NAME = "chat_history";
    private static final String KEY_MESSAGES = "messages";

    public static void saveMessages(Context context, List<Message> messages) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(messages);

        editor.putString(KEY_MESSAGES, json);
        editor.apply();
    }

    public static List<Message> loadMessages(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_MESSAGES, null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Message>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void clearHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_MESSAGES).apply();
    }
}
