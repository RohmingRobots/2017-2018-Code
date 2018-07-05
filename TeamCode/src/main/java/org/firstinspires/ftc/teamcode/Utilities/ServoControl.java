package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;

/**
 * Created by ablauch on 7/2018.
 *
 * this class is used to perform basic servo control
 *
 * Example (w/ setpoints):
 *     private EnumMap<Setpoints, Double> MapSetpoints = new EnumMap<Setpoints, Double>(Setpoints.class);
 *     public ServoControl<Setpoints, EnumMap<Setpoints, Double>> TestServo = new ServoControl(MyServo,MapSetpoints);
 *
 * Example (w/o setpoints):
 *     public ServoControl TestServo = new ServoControl(MyServo);
 */

public class ServoControl<W extends EnumWrapper, M extends EnumMap> {

    private Servo servo = null;
    private M map = null;
    private W setpt = null;

    /* constructor for use with setpoints
     * pass servo object to control
     * pass enumerated setpoint map
     */
    public ServoControl(Servo servo, M map) {
        this.servo = servo;
        this.map = map;
        this.setpt = null;
    }

    /* constructor for use with setpoints
     * pass servo object to control
     */
    public ServoControl(Servo servo) {
        this.servo = servo;
    }

    /* Position methods
     */

    /* returns current position of servo */
    public double getPosition() {
        return servo.getPosition();
    }

    /* moves servo to specified position */
    public void setPosition(double position) {
        servo.setPosition(position);
    }

    /* incfementally moves servo by specified amount */
    public void incrementPosition(double increment) {
        servo.setPosition(servo.getPosition() + increment);
    }

    /* Setpoint methods
     */

    /* returns current setpoint (null if not at setpoint) */
    public W getSetpoint() {
        if (servo.getPosition() != (Double)map.get(setpt))
            return null;
        return setpt;
    }

    /* moves servo to next setpoint (no wrap around) */
    public void nextSetpoint() {
        nextSetpoint(false);
    }

    /* moves servo to next setpoint */
    public void nextSetpoint(boolean wrap_around) {
        setpt = (W)setpt.getNext(wrap_around);
        servo.setPosition((Double)map.get(setpt));
    }

    /* moves servo to previous setpoint (no wrap around) */
    public void prevSetpoint() {
        prevSetpoint(false);
    }

    /* moves servo to previous setpoint */
    public void prevSetpoint(boolean wrap_around) {
        setpt = (W)setpt.getPrev(wrap_around);
        servo.setPosition((Double)map.get(setpt));
    }

    /* moves servo to specified setpoint */
    public void setSetpoint(W setpt) {
        this.setpt = setpt;
        servo.setPosition((Double)map.get(setpt));
    }
}
