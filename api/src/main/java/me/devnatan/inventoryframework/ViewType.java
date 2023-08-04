package me.devnatan.inventoryframework;

import static java.lang.String.format;

import java.util.Objects;

public final class ViewType {

    public static final ViewType CHEST = new ViewType("chest", 54, 6, 9, true);
    public static final ViewType HOPPER = new ViewType("hopper", 5, 1, 5);
    public static final ViewType DROPPER = new ViewType("dropper", 9, 3, 3);
    public static final ViewType DISPENSER = new ViewType("dispenser", 9, 3, 3);
    public static final ViewType FURNACE = new ViewType("furnace", 3, 2, 2, false, new int[] {2});
    public static final ViewType BLAST_FURNACE = new ViewType("blast-furnace", 3, 2, 2, false, new int[] {2});
    public static final ViewType CRAFTING_TABLE = new ViewType("crafting-table", 9, 3, 3, false, new int[] {3});
    public static final ViewType BREWING_STAND =
            new ViewType("brewing-stand", 4, 1, 1, false, new int[] {0, 1, 2}, false);
    public static final ViewType BEACON = new ViewType("beacon", 1, 1, 1);
    public static final ViewType ANVIL = new ViewType("anvil", 3, 1, 3);
    public static final ViewType SHULKER_BOX = new ViewType("shulker-box", 27, 3, 9);
    public static final ViewType SMOKER = new ViewType("smoker", 3, 2, 2, false, new int[] {2});
    public static final ViewType VILLAGER_TRADING = new ViewType("villager-trading", 3, 1, 3, false, new int[] {2});

    private static final int[] EMPTY_RESULT_SLOTS = new int[0];

    private final String identifier;
    private final int maxSize, rows, columns;
    private final boolean extendable;
    private final int[] resultSlots;
    private final boolean aligned;

    ViewType(String identifier, int maxSize, int rows, int columns) {
        this(identifier, maxSize, rows, columns, false);
    }

    ViewType(String identifier, int maxSize, int rows, int columns, boolean extendable) {
        this(identifier, maxSize, rows, columns, extendable, EMPTY_RESULT_SLOTS);
    }

    ViewType(String identifier, int maxSize, int rows, int columns, boolean extendable, int[] resultSlots) {
        this(identifier, maxSize, rows, columns, extendable, resultSlots, true);
    }

    ViewType(
            String identifier,
            int maxSize,
            int rows,
            int columns,
            boolean extendable,
            int[] resultSlots,
            boolean aligned) {
        this.identifier = identifier;
        this.maxSize = maxSize;
        this.rows = rows;
        this.columns = columns;
        this.extendable = extendable;
        this.resultSlots = resultSlots;
        this.aligned = aligned;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isExtendable() {
        return extendable;
    }

    public int[] getResultSlots() {
        return resultSlots;
    }

    public boolean isAligned() {
        return aligned;
    }

    public boolean canPlayerInteractOn(int slot) {
        if (getResultSlots() != null) {
            for (final int resultSlot : getResultSlots()) {
                if (resultSlot == slot) return false;
            }
        }
        return true;
    }

    /**
     * Normalizes the specified parameter to conform to container constraints and does not exceed or
     * fail in an attempt to set the container size, e.g.: if player provides inventory rows count
     * instead of the full inventory size, it will return the inventory size.
     *
     * @param size The expected size of the container.
     * @return The size of the container according to the specified parameter.
     */
    public int normalize(final int size) {
        if (size == 0) return size;

        final int fullSize;

        if (size <= getRows()) fullSize = size * getColumns();
        else {
            if (size % getColumns() != 0)
                throw new IllegalArgumentException(
                        format("Container size must be a multiple of %d (given: %d)", getColumns(), size));

            fullSize = size;
        }

        if (fullSize > getMaxSize())
            throw new IllegalArgumentException(format(
                    "Size cannot exceed container max size of %d (given: %d (%s rows))", getMaxSize(), fullSize, size));

        return fullSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewType viewType = (ViewType) o;
        return Objects.equals(getIdentifier(), viewType.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }

    @Override
    public String toString() {
        return "ViewType{" + "identifier='"
                + identifier + '\'' + ", maxSize="
                + maxSize + ", rows="
                + rows + ", columns="
                + columns + ", extendable="
                + extendable + '}';
    }
}
