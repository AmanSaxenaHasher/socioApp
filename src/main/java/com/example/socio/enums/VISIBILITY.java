package com.example.socio.enums;

public enum VISIBILITY {
    PRIVATE("private"),
    PUBLIC("public");

    private final String visibility;

    VISIBILITY(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }
}