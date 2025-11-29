package com.farah.foodapp.chatbot;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiService {
    private static final String API_KEY = "AIzaSyAWcsNPA9Qp24-8B5mN8PCsr5HD_cNSuts";
    private final GenerativeModelFutures model;
    private final Executor executor;

    public interface ChatCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public GeminiService() {
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
        executor = Executors.newSingleThreadExecutor();
    }

    public void sendMessage(String userMessage, String context, ChatCallback callback) {
        String fullPrompt = "You are a helpful restaurant assistant.\n\n" +
                context + "\n\nUser: " + userMessage +
                "\nProvide a helpful, friendly, concise answer.";

        Content content = new Content.Builder()
                .addText(fullPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    callback.onSuccess(result.getText());
                } catch (Exception e) {
                    callback.onError("Exception: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        }, executor);

    }
}
