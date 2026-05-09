package com.pori.portfolio.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryScores {
    private int projectClarity;
    private int problemSolving;
    private int techJustification;
    private int contribution;
    private int impact;
    private int evidence;
    private int jobFit;
    private int readability;

    public int average() {
        return (projectClarity + problemSolving + techJustification
                + contribution + impact + evidence + jobFit + readability) / 8;
    }
}
