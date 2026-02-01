package com.hypixel.hytale;

/** Minimal stub for Ref used by Hytale API. */
public class Ref<T> {
    private final T value;
    public Ref(T value) { this.value = value; }
    public T get() { return value; }
}
