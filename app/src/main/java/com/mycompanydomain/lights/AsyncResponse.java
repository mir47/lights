package com.mycompanydomain.lights;

public interface AsyncResponse {
    void processFinish(String output);
    void processFinish(boolean output);
    void processFinish(int output);
}
