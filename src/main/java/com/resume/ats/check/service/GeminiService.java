package com.resume.ats.check.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    public String getApiKey() {
    System.out.println("API KEY = " + apiKey);
    return apiKey;
}
    public String getSuggestions(String resumeText, String jdText) {
        System.out.println("API KEY = " + apiKey);
      

try {

    String prompt =
            "You are an ATS resume reviewer.\n" +
            "Compare this resume with the job description and provide:\n" +
            "1. ATS score out of 100\n" +
            "2. Missing skills\n" +
            "3. 5 improvement suggestions\n\n" +
            "Resume:\n" + resumeText +
            "\n\nJob Description:\n" + jdText;

   OkHttpClient client = new OkHttpClient();
  prompt = prompt
        .replace("\r", "")
        .replace("\"", "\\\"")
        .replace("\n", "\\n");

String requestBodyJson = """
{
  "model": "llama-3.1-8b-instant",
  "messages": [
    {
      "role": "user",
      "content": "%s"
    }
  ]
}
""".formatted(prompt);
System.out.println(requestBodyJson);
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

   String jsonResponse = response.body().string();

JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

String content = jsonObject
        .getAsJsonArray("choices")
        .get(0)
        .getAsJsonObject()
        .getAsJsonObject("message")
        .get("content")
        .getAsString();

return content;

}
    

} catch (Exception e) {
    e.printStackTrace();
    return "Error: " + e.getMessage();
}
    }
}