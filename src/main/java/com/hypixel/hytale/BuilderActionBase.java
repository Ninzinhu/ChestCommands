package com.hypixel.hytale;

/** Minimal stub for builder action base. */
public abstract class BuilderActionBase {
    public abstract String getShortDescription();
    public abstract String getLongDescription();
    // Ajuste: usar Object na assinatura para casar com implementações e com o JAR
    public abstract Action build(Object support);
    public enum BuilderDescriptorState { Stable, Experimental }
    public BuilderDescriptorState getBuilderDescriptorState() { return BuilderDescriptorState.Experimental; }
}
