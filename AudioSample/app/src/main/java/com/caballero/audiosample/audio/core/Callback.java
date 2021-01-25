package com.caballero.audiosample.audio.core;

public interface Callback {
    void onBufferAvailable(byte[] buffer);
}
