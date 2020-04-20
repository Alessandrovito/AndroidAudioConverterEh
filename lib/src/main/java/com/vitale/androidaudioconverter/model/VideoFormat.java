package com.vitale.androidaudioconverter.model;

public enum VideoFormat {
    MOV,
    M4A,
    AVI,
    MP4;

    public String getFormat() {
        return name().toLowerCase();
    }
}