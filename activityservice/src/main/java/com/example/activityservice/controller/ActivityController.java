package com.example.activityservice.controller;

import com.example.activityservice.controller.dto.ActivityResponse;
import com.example.activityservice.service.ActivityService;
import com.example.activityservice.service.dto.ActivityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request){
        return ResponseEntity.ok(activityService.trackActivity(request));
    }
    @GetMapping("/byuserid/{userid}")
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@PathVariable String userid){
        return ResponseEntity.ok(activityService.getUserActivities(userid));
    }

    @GetMapping("/byactivityid/{activityid}")
    public ResponseEntity<ActivityResponse> getUserActivity(@PathVariable String activityid){
        return ResponseEntity.ok(activityService.getUserActivity(activityid));
    }
}
