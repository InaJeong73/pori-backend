package com.pori.techstack.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tech_stack_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechStackOption {

    @Id
    @Column(name = "stack_key")
    private String key;
    private String label;
    private String category;

    @Builder
    public TechStackOption(String key, String label, String category) {
        this.key = key;
        this.label = label;
        this.category = category;
    }
}
