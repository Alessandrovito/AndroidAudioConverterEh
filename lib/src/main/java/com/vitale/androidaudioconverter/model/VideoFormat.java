package com.vitale.androidaudioconverter.model;

public enum VideoFormat {
    MOV,
    MP4;

    public String getFormat() {
        return name().toLowerCase();
    }
}