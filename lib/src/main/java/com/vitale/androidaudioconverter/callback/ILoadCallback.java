package com.vitale.androidaudioconverter.callback;

public interface ILoadCallback {
    
    void onSuccess();
    
    void onFailure(Exception error);
    
}