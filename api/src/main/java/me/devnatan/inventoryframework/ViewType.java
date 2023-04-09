package me.devnatan.inventoryframework;

import static java.lang.String.format;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ViewType {

    public static final ViewType CHEST = new ViewType("chest", 54, 6, 9, true);
    public static final ViewType HOPPER = new ViewType("hopper", 5, 1, 5, false);
    public static final ViewType DROPPER = new ViewType("dropper", 9, 3, 3, false);
    public static final ViewType DISPENSER = new ViewType("dispenser", 9, 3, 3, false);

    public static final ViewType FURNACE = new ViewType("furnace", 3, 2, 2, false) {
        private final int[] resultSlots = {2};

        @Override
        public int[] getResultSlots() {
            return resultSlots;
        }
    };

    public static final ViewType BLAST_FURNACE = new ViewType("blast-furnace", 3, 2, 2, false) {
        private final int[] resultSlots = {2};

        @Override
        public int[] getResultSlots() {
            return resultSlots;
        }
    };

    public static final ViewType CRAFTING_TABLE = new ViewType("crafting-table", 9, 3, 3, false) {
        private static final int RESULT_SLOT = 3;

        @Override
        public boolean canPlayerInteractOn(int slot) {
            return slot != RESULT_SLOT;
        }
    };
    public static final ViewType BREWING_STAND = new ViewType("brewing-stand", 4, 1, 1, false) {
        private final int[] resultSlots = {0, 1, 2};

        @Override
        public int[] getResultSlots() {
            return resultSlots;
        }

        @Override
        public boolean isAligned() {
            return false;
        }
    };
    public static final ViewType BEACON = new ViewType("beacon", 1, 1, 1, false);
    public static final ViewType ANVIL = new ViewType("anvil", 3, 1, 3, false);
    public static final ViewType SHULKER_BOX = new ViewType("shulker-box", 27, 3, 9, false);
    public static final ViewType SMOKER = new ViewType("smoker", 3, 2, 2, false) {
        private final int[] resultSlots = {2};

        @Override
        public int[] getResultSlots() {
            return resultSlots;
        }
    };
    public static final ViewType VILLAGER_TRADING = new ViewType("villager-trading", 3, 1, 3, false) {
        private final int[] resultSlots = {2};

        @Override
        public int[] getResultSlots() {
            return resultSlots;
        }
    };

    private static final int[] EMPTY_RESULT_SLOTS = new int[0];

    @EqualsAndHashCode.Include
    private final String identifier;

    private final int maxSize, rows, columns;
    private final boolean extendable;

    /**
     * Normalizes the specified parameter to conform to container constraints and does not exceed or
     * fail in an attempt to set the container size, e.g.: if player provides inventory rows count
     * instead of the full inventory size, it will return the inventory size.
     *
     * @param size The expected size of the container.
     * @return The size of the container according to the specified parameter.
     */
    public final int normalize(final int size) {
        if (size == 0) return size;

        final int fullSize;

        if (size <= rows) fullSize = size * columns;
        else {
            if (size % columns != 0)
                throw new IllegalArgumentException(
                        format("Container size must be a multiple of %d (given: %d)", columns, size));

            fullSize = size;
        }

        if (fullSize > getMaxSize())
            throw new IllegalArgumentException(format(
                    "Size cannot exceed container max size of %d (given: %d (%s rows))", getMaxSize(), fullSize, size));

        return fullSize;
    }

    public final int getResultSlot() {
        return getResultSlots()[0];
    }

    public int[] getResultSlots() {
        return EMPTY_RESULT_SLOTS;
    }

    public boolean canPlayerInteractOn(int slot) {
        return true;
    }

    public boolean isAligned() {
        return true;
    }
}
