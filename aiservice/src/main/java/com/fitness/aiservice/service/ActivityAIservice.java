package com.fitness.aiservice.service;


import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIservice {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String airesponse = geminiService.getAnswer(prompt);
        log.info("Response from ai : {}", airesponse);
        return processAiResponse(activity, airesponse);

    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

             log.info("Parsed Response from ai : {}",jsonContent);
             log.info("Parsed Response from textformat : {}",jsonContent);

             //Extracting Analysins section
            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullanalysis = new StringBuilder();
            addAnalysisSection(fullanalysis, analysisNode, analysisJson, "overall", "OverAll:");
            addAnalysisSection(fullanalysis, analysisNode, analysisJson, "pace", "Pace:");
            addAnalysisSection(fullanalysis, analysisNode, analysisJson, "heartRate", "HeartRate:");
            addAnalysisSection(fullanalysis, analysisNode, analysisJson, "CaloriesBurned", "CaloriesBurned:");

            //Extracting Improvements
            List<String> improvements = extractImprovements(analysisJson.path("improvements"));

            //Extract Suggestion
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));

            //Extract Safety

            List<String> safety = extractSafety(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullanalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }

    }

    private Recommendation createDefaultRecommendation(Activity activity) {

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("No AI recommendation available at the moment.")
                .improvements(List.of(
                        "Maintain consistency in your workouts",
                        "Focus on proper form and technique"
                ))
                .suggestions(List.of(
                        "Try a light recovery workout",
                        "Include stretching or mobility exercises"
                ))
                .safety(List.of(
                        "Stay hydrated",
                        "Do proper warm-up and cool-down"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafety(JsonNode safetyNode) {
           List<String> safety = new ArrayList<>();
           if(safetyNode.isArray()){
               safetyNode.forEach(sf ->{
                   String s = sf.asText();
                   safety.add(s);
               });
           }
           return safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionNode.isArray()){
            suggestionNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s : %s",workout,description));
            });
        }
        return suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementNode.isArray()){
            improvementNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s : %s",area,detail));
            });
        }
        return improvements;
    }

    private void addAnalysisSection(StringBuilder fullanalysis, JsonNode analysisNode, JsonNode analysisJson, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullanalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {

        return String.format(""" 
                  Analyze this fitness activity and provide detailed recommendations in the following format
                  {
                      "analysis" : {
                          "overall": "Overall analysis here",
                          "pace": "Pace analysis here",
                          "heartRate": "Heart rate analysis here",
                          "CaloriesBurned": "Calories Burned here"
                      },
                      "improvements": [
                          {
                              "area": "Area name",
                              "recommendation": "Detailed Recommendation"
                          }
                      ],
                      "suggestions" : [
                          {
                              "workout": "Workout name",
                              "description": "Detailed workout description"
                          }
                      ],
                      "safety": [
                          "Safety point 1",
                          "Safety point 2"
                      ]
                  }
                
                  Analyze this activity:
                  Activity Type: %s
                  Duration: %d minutes
                  calories Burned: %d
                  Additional Metrics: %s
                
                  provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines
                  Ensure the response follows the EXACT JSON format shown above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics()
        );
    }

}
