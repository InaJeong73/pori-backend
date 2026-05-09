package com.pori.techstack.dto;

import com.pori.techstack.domain.TechStackOption;

public record TechStackOptionResponse(String key, String label, String category) {
    public static TechStackOptionResponse from(TechStackOption o) {
        return new TechStackOptionResponse(o.getKey(), o.getLabel(), o.getCategory());
    }
}
