package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Phases are groups of interceptors that can be ordered topologically, defining relationships
 * between them.
 *
 * @see Pipeline
 */
@AllArgsConstructor
public final class PipelinePhase {

    /** The pipeline phase name. */
    @Getter private final String name;

    @Override
    public String toString() {
        return "Phase('" + name + "')";
    }
}
