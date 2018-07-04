package org.firstinspires.ftc.teamcode.Utilities;

public interface EnumWrapper<E extends Enum<E>> {
    int ordinal();

    default E getNext() {
        return getNext(false);
    }

    default E getNext(boolean wrap_around) {
        E[] ies = (E[]) this.getClass().getEnumConstants();
        if (wrap_around)
            return (this.ordinal() < ies.length - 1) ? ies[this.ordinal() + 1] : ies[0];
        else
            return (this.ordinal() < ies.length - 1) ? ies[this.ordinal() + 1] : ies[this.ordinal()];
    }

    default E getPrev() {
        return getPrev(false);
    }

    default E getPrev(boolean wrap_around) {
        E[] ies = (E[]) this.getClass().getEnumConstants();
        if (wrap_around)
            return (this.ordinal() > 0) ? ies[this.ordinal() - 1] : ies[ies.length - 1];
        else
            return (this.ordinal() > 0) ? ies[this.ordinal() - 1] : ies[this.ordinal()];
    }
}
