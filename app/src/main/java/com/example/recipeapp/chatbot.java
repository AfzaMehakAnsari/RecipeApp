package com.example.recipeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.*;

public class chatbot extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText userInput;
    private View typingDotsView;
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey = "BUy2f2BnmyymJgQOu00Iv51M5jCUyDQj57dn8rqq9XAHIhRJ1XFKJQQJ99BEACHYHv6XJ3w3AAAAACOGqkWQ"; // Replace securely
    private final String endpoint = "https://02-13-mausns7h-eastus2.cognitiveservices.azure.com/openai/deployments/chatbot/chat/completions?api-version=2025-01-01-preview";
    private final List<JsonObject> messageHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initial system prompt
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are a food assistant. Only answer food-related questions.");
        messageHistory.add(systemMessage);

        setContentView(R.layout.activity_chatbot);
        chatLayout = findViewById(R.id.chatLayout);
        userInput = findViewById(R.id.userInput);
        ImageButton sendButton = findViewById(R.id.sendButton);

        // Show welcome message
        String welcome = "Hi, I'm Cooksy Bot! How may I help you?";
        addChatMessage(welcome, false);

        JsonObject welcomeMessage = new JsonObject();
        welcomeMessage.addProperty("role", "assistant");
        welcomeMessage.addProperty("content", welcome);
        messageHistory.add(welcomeMessage);

        sendButton.setOnClickListener(view -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                addChatMessage(input, true);
                userInput.setText("");
                sendToOpenAI(input);
            }
        });
    }
    private void addChatMessage(String message, boolean isUser) {
        View messageView;
        LayoutInflater inflater = getLayoutInflater();

        if (isUser) {
            messageView = inflater.inflate(R.layout.item_chat_user, null);
            TextView userText = messageView.findViewById(R.id.userMessageText);
            userText.setText(message);
        } else {
            messageView = inflater.inflate(R.layout.item_chat_bot, null);
            TextView botText = messageView.findViewById(R.id.botMessageText);
            botText.setText(message);
        }

        chatLayout.addView(messageView);
    }

    private void showTypingDots() {
        typingDotsView = getLayoutInflater().inflate(R.layout.typing_dots, null);
        chatLayout.addView(typingDotsView);
    }

    private void hideTypingDots() {
        if (typingDotsView != null) {
            chatLayout.removeView(typingDotsView);
            typingDotsView = null;
        }
    }

    private void sendToOpenAI(String userMessage) {
        showTypingDots();

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messageHistory.add(userMsg);

        JsonArray messages = new JsonArray();
        for (JsonObject msg : messageHistory) {
            messages.add(msg);
        }

        JsonObject jsonBody = new JsonObject();
        jsonBody.add("messages", messages);
        jsonBody.addProperty("temperature", 0.7);
        jsonBody.addProperty("max_tokens", 800);
        jsonBody.addProperty("top_p", 0.95);
        jsonBody.addProperty("frequency_penalty", 0);
        jsonBody.addProperty("presence_penalty", 0);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    hideTypingDots();
                    addChatMessage("Error: " + e.getMessage(), false);
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> hideTypingDots());

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> addChatMessage("Error: " + response.code(), false));
                } else {
                    String responseBody = response.body().string();
                    JsonObject resJson = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray choices = resJson.getAsJsonArray("choices");
                    String reply = choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();

                    JsonObject botReply = new JsonObject();
                    botReply.addProperty("role", "assistant");
                    botReply.addProperty("content", reply);
                    messageHistory.add(botReply);

                    runOnUiThread(() -> addChatMessage(reply, false));
                }
            }
        });
    }
}

