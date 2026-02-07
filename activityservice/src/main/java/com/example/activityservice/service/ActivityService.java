package com.example.activityservice.service;

import com.example.activityservice.controller.dto.ActivityResponse;
import com.example.activityservice.model.Activity;
import com.example.activityservice.repository.ActivityRepository;
import com.example.activityservice.service.dto.ActivityRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValid = userValidationService.validate(request.getUserId());
        if(!isValid){
            throw new RuntimeException("No User exits with this id:"+request.getUserId());
        }
        Activity activity =  Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        Activity savedActivity = activityRepository.save(activity);

        //publish for RabbitMQ for ai processing
        try{
             rabbitTemplate.convertAndSend(exchange,routingKey,savedActivity);
        }catch (Exception e){
           log.error("Failed to publish activity to rabbitMQ:"+ e);
        }
        return mapToResponse(savedActivity);

    }
    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setType(activity.getType());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityResponse.setCreatedAt(activity.getCreatedAt());
        activityResponse.setUpdatedAt(activity.getUpdatedAt());
        return activityResponse;
    }

    public List<ActivityResponse> getUserActivities(String userId){
            List<Activity> activities =   activityRepository.findByUserId(userId);
            return activities.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    public ActivityResponse getUserActivity(String userid) {

        Activity activity = activityRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("Activity Not Found"));
        return mapToResponse(activity);
    }
}