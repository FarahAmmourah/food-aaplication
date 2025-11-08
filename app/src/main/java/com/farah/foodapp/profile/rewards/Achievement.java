package com.farah.foodapp.profile.rewards;

public class Achievement {
    private String title;
    private String description;
    private String conditionType;
    private int minCount;
    private boolean unlocked;

    public Achievement() {}

    public Achievement(String title, String description, String conditionType, int minCount, boolean unlocked) {
        this.title = title;
        this.description = description;
        this.conditionType = conditionType;
        this.minCount = minCount;
        this.unlocked = unlocked;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getConditionType() { return conditionType; }
    public int getMinCount() { return minCount; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
