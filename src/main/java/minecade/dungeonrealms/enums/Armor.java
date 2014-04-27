package minecade.dungeonrealms.enums;

public enum Armor {
    HELMET(39), CHESTPLATE(38), LEGGINGS(37), BOOTS(36);
    int slot;

    Armor(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
