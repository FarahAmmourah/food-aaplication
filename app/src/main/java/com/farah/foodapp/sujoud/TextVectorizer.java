package com.farah.foodapp.sujoud;

import java.util.HashMap;
import java.util.Map;

public class TextVectorizer {

    private static final Map<String, Integer> vocab = new HashMap<>();

    static {
        vocab.put("am", 0);
        vocab.put("amazing", 1);
        vocab.put("angry", 2);
        vocab.put("bad", 3);
        vocab.put("best", 4);
        vocab.put("day", 5);
        vocab.put("disappointed", 6);
        vocab.put("don", 7);
        vocab.put("ever", 8);
        vocab.put("everything", 9);
        vocab.put("feel", 10);
        vocab.put("good", 11);
        vocab.put("happy", 12);
        vocab.put("hate", 13);
        vocab.put("is", 14);
        vocab.put("it", 15);
        vocab.put("like", 16);
        vocab.put("love", 17);
        vocab.put("made", 18);
        vocab.put("much", 19);
        vocab.put("my", 20);
        vocab.put("neutral", 21);
        vocab.put("not", 22);
        vocab.put("nothing", 23);
        vocab.put("okay", 24);
        vocab.put("perfect", 25);
        vocab.put("sad", 26);
        vocab.put("satisfied", 27);
        vocab.put("so", 28);
        vocab.put("special", 29);
        vocab.put("terrible", 30);
        vocab.put("the", 31);
        vocab.put("thing", 32);
        vocab.put("this", 33);
        vocab.put("very", 34);
        vocab.put("worst", 35);
    }

    public static float[] vectorize(String text) {
        float[] vector = new float[36];

        text = text.toLowerCase();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (vocab.containsKey(word)) {
                int index = vocab.get(word);
                vector[index] += 1f;
            }
        }

        return vector;
    }
}
