package com.pori.portfolio.domain;

public enum PoriGrade {
    EXCELLENT, STRONG, STANDARD, NEEDS_IMPROVEMENT;

    public static PoriGrade from(int score) {
        if (score >= 90) return EXCELLENT;
        if (score >= 80) return STRONG;
        if (score >= 60) return STANDARD;
        return NEEDS_IMPROVEMENT;
    }

    public String display() {
        return switch (this) {
            case EXCELLENT -> "Excellent";
            case STRONG -> "Strong";
            case STANDARD -> "Standard";
            case NEEDS_IMPROVEMENT -> "Needs Improvement";
        };
    }
}
