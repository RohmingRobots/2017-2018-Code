package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;


public class ServoControl<W extends EnumWrapper, M extends EnumMap> {

    private Servo servo = null;
    private M map = null;
    private W setpt = null;

    public ServoControl(Servo servo, M map) {
        this.servo = servo;
        this.map = map;
        this.setpt = null;
    }

    public double getPosition() {
        return servo.getPosition();
    }

    public W getSetpoint() {
        if (servo.getPosition() != (Double)map.get(setpt))
            return null;
        return setpt;
    }

    public void nextSetpoint() {
        nextSetpoint(false);
    }

    public void nextSetpoint(boolean wrap_around) {
        setpt = (W)setpt.getNext(wrap_around);
        servo.setPosition((Double)map.get(setpt));
    }

    public void prevSetpoint() {
        prevSetpoint(false);
    }

    public void prevSetpoint(boolean wrap_around) {
        setpt = (W)setpt.getPrev(wrap_around);
        servo.setPosition((Double)map.get(setpt));
    }

    public void setSetpoint(W setpt) {
        this.setpt = setpt;
        servo.setPosition((Double)map.get(setpt));
    }

    public void setPosition(double position) {
        servo.setPosition(position);
    }

    public void incrementPosition(double increment) {
        servo.setPosition(servo.getPosition() + increment);
    }
}
