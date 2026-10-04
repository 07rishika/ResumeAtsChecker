
package com.resume.ats.check.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    public String getSuggestions(String resumeText, String jdText) {

        try {
            String prompt =
                    "You are a careful ATS resume reviewer.\n" +
                    "Compare the resume with the job description using only evidence from the text.\n\n" +
                    "Provide these sections:\n" +
                    "1. Estimated ATS score out of 100. Explain that this is an estimate, not an actual employer ATS score.\n" +
                    "2. Required skills matched.\n" +
                    "3. Required skills missing.\n" +
                    "4. Preferred skills matched and missing, separately.\n" +
                    "5. Five practical improvement suggestions.\n\n" +
                    "Rules:\n" +
                    "- Use only evidence from the resume and job description.\n" +
                    "- Do not invent skills, experience, projects, or achievements.\n" +
                    "- Do not recommend claiming experience the candidate does not have.\n" +
                    "- Do not say all requirements are met when some are missing.\n" +
                    "- Count skills carefully and keep all totals consistent.\n" +
                    "- Treat related terms such as REST and REST APIs as related, not automatically as separate skills.\n" +
                    "- Classify skills as required or preferred only when the job description supports that classification.\n\n" +
                    "Resume:\n" + resumeText +
                    "\n\nJob Description:\n" + jdText;

            OkHttpClient client = new OkHttpClient();

            JsonObject requestJson = new JsonObject();
          requestJson.addProperty("model", "openai/gpt-oss-20b");

            com.google.gson.JsonArray messages =
                    new com.google.gson.JsonArray();

            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);
            messages.add(message);

            requestJson.add("messages", messages);

            String requestBodyJson = requestJson.toString();

            RequestBody body = RequestBody.create(
                    requestBodyJson,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (response.body() == null) {
                    return "Error: Empty response from Groq API.";
                }

                String jsonResponse = response.body().string();

                if (!response.isSuccessful()) {
                    return "Groq API error (" + response.code() + "): "
                            + jsonResponse;
                }

                JsonObject jsonObject =
                        JsonParser.parseString(jsonResponse).getAsJsonObject();

                return jsonObject
                        .getAsJsonArray("choices")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content")
                        .getAsString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating AI suggestions: " + e.getMessage();
        }
    }
}
