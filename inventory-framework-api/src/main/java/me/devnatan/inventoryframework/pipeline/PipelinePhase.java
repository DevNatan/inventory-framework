package me.devnatan.inventoryframework.pipeline;

/**
 * Phases are groups of interceptors that can be ordered topologically, defining relationships
 * between them.
 *
 * @see Pipeline
 */
public final class PipelinePhase {

    private final String name;

    public PipelinePhase(String name) {
        this.name = name;
    }

    /**
     * The pipeline phase name.
     *
     * @return The name of this pipeline phase.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PipelinePhase{" + "name='" + name + '\'' + '}';
    }
}
