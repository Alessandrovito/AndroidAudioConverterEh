package com.vitale.androidaudioconverter.model;

public enum VideoEncoder {
    ENCODER_MPEG4("mpeg4"),
    ENCODER_H264("libx264");

    private String name;

    private VideoEncoder(String name) {
        this.name = name;
    }

    public String getEncoder() {
        return name;
    }

}
