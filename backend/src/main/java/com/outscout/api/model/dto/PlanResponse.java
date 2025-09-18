package com.outscout.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * Response DTO for trip planning results
 */
public class PlanResponse {
    
    private String requestId;
    private String status;
    private WeatherWindow[] windows;
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;
    
    // Nested weather window class
    public static class WeatherWindow {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private OffsetDateTime startTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private OffsetDateTime endTime;
        
        private Double score;
        private String explanation;
        private WeatherConditions conditions;
        
        // Nested weather conditions class
        public static class WeatherConditions {
            private Double tempC;
            private Double precipProb;
            private Double windMps;
            private Double uvIndex;
            
            // Constructors
            public WeatherConditions() {}
            
            public WeatherConditions(Double tempC, Double precipProb, Double windMps, Double uvIndex) {
                this.tempC = tempC;
                this.precipProb = precipProb;
                this.windMps = windMps;
                this.uvIndex = uvIndex;
            }
            
            // Getters and Setters
            public Double getTempC() { return tempC; }
            public void setTempC(Double tempC) { this.tempC = tempC; }
            public Double getPrecipProb() { return precipProb; }
            public void setPrecipProb(Double precipProb) { this.precipProb = precipProb; }
            public Double getWindMps() { return windMps; }
            public void setWindMps(Double windMps) { this.windMps = windMps; }
            public Double getUvIndex() { return uvIndex; }
            public void setUvIndex(Double uvIndex) { this.uvIndex = uvIndex; }
        }
        
        // Constructors
        public WeatherWindow() {}
        
        public WeatherWindow(OffsetDateTime startTime, OffsetDateTime endTime, Double score, 
                           String explanation, WeatherConditions conditions) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.score = score;
            this.explanation = explanation;
            this.conditions = conditions;
        }
        
        // Getters and Setters
        public OffsetDateTime getStartTime() { return startTime; }
        public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
        public OffsetDateTime getEndTime() { return endTime; }
        public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public WeatherConditions getConditions() { return conditions; }
        public void setConditions(WeatherConditions conditions) { this.conditions = conditions; }
    }
    
    // Constructors
    public PlanResponse() {}
    
    public PlanResponse(String requestId, String status, WeatherWindow[] windows, String message) {
        this.requestId = requestId;
        this.status = status;
        this.windows = windows;
        this.message = message;
        this.createdAt = OffsetDateTime.now();
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public WeatherWindow[] getWindows() {
        return windows;
    }
    
    public void setWindows(WeatherWindow[] windows) {
        this.windows = windows;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
