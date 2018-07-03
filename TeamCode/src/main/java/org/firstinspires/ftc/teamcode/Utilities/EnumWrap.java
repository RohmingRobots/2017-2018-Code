package org.firstinspires.ftc.teamcode.Utilities;

public interface EnumWrap<E extends Enum<E>> {
    int ordinal();

    default E getNext() {
        E[] ies = (E[]) this.getClass().getEnumConstants();
        return (this.ordinal() < ies.length - 1) ? ies[this.ordinal() + 1] : ies[0];
    }

    default E getPrev() {
        E[] ies = (E[]) this.getClass().getEnumConstants();
        return (this.ordinal() > 0) ? ies[this.ordinal() - 1] : ies[ies.length - 1];
    }
}
