package org.firstinspires.ftc.teamcode.SubAssemblyAmpere;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Utilities.EnumWrapper;
import org.firstinspires.ftc.teamcode.Utilities.ServoControl;

import java.util.EnumMap;


/* Sub Assembly Class
 */
public class AmpereControl {
    /* Declare private class objects
     */
    private Telemetry telemetry;            /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap;        /* local copy of HardwareMap object from opmode class */
    private String name = "Ampere Control";

    /* Continuous servos
     * AWL - continuous servo motor for left arm winch
     * AWR - continuous servo motor for right arm winch
     */
    private CRServo AWL;
    private CRServo AWR;

    /* Servos
     * AFL - left arm flipper servo motor
     * AFR - right arm flipper servo motor
     */
    private Servo AFL;
    private Servo AFR;

    /* Setpoint enumeration maps */
    private EnumMap<Setpoints, Double> MapLeftFlipper;
    private EnumMap<Setpoints, Double> MapRightFlipper;

    /* Servo control for setpoint maps */
    public ServoControl<Setpoints, EnumMap<Setpoints, Double>> LeftFlipperServo;
    public ServoControl<Setpoints, EnumMap<Setpoints, Double>> RightFlipperServo;

    /* Declare public class objects
     */

    /* Color sensors
     * CSL - color sensor on left side arm
     * CSR - color sensor on right side arm
     */
    public ColorSensor ColorLeft;
    public ColorSensor ColorRight;

    /* servo setpoints */
    public enum Setpoints implements EnumWrapper<Setpoints> {
        CLOSE, PARTIAL, OPEN;
    }


    /* Getter methods
     */


    /* Subassembly constructor */
    public AmpereControl(LinearOpMode opMode) {
        initialize(opMode, true);
    }

    public AmpereControl(LinearOpMode opMode, boolean init_servos) {
        initialize(opMode, init_servos);
    }

    /* Initialization method - to be called before any other methods are used */
    private void initialize(LinearOpMode opMode, boolean init_servos) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Create setpoint maps */
        MapLeftFlipper = new EnumMap<Setpoints, Double>(Setpoints.class);
        MapRightFlipper = new EnumMap<Setpoints, Double>(Setpoints.class);

        /* Assign setpoint values */
        MapLeftFlipper.put(Setpoints.CLOSE, 0.0);
        MapLeftFlipper.put(Setpoints.PARTIAL, 0.6);
        MapLeftFlipper.put(Setpoints.OPEN, 1.0);

        MapRightFlipper.put(Setpoints.CLOSE, 0.0);
        MapRightFlipper.put(Setpoints.PARTIAL, 0.6);
        MapRightFlipper.put(Setpoints.OPEN, 1.0);

        /* Map hardware devices */
        // Side servos
        AWL = hardwareMap.crservo.get("AWL");
        AWR = hardwareMap.crservo.get("AWR");
        // reverse those motors
        AWR.setDirection(CRServo.Direction.REVERSE);
        // set all motors to zero power
        AWL.setPower(0.0);
        AWR.setPower(0.0);

        // Flippers
        AFL = hardwareMap.servo.get("AFL");
        AFR = hardwareMap.servo.get("AFR");
        // reverse those motors
        AFR.setDirection(Servo.Direction.REVERSE);

        /* Create servo control objects and initialize positions */
        LeftFlipperServo = new ServoControl(AFL, MapLeftFlipper, Setpoints.CLOSE, init_servos);
        RightFlipperServo = new ServoControl(AFR, MapRightFlipper, Setpoints.CLOSE, init_servos);

        // Define and initialize color sensors
        ColorLeft = hardwareMap.colorSensor.get("left_ampere");
        ColorRight = hardwareMap.colorSensor.get("right_ampere");
        //turns all LEDs off
        ColorLeft.enableLed(false);
        ColorRight.enableLed(false);
    }

    /* positive power extends, negative power retracts */
    public void moveLeftWinch(double power) {
        /* make sure power is within [-1,1] */
        power = (power > 1.0) ? 1.0 : ((power < -1.0) ? -1.0 : power);
        AWL.setPower(power);
    }

    /* positive power extends, negative power retracts */
    public void moveRightWinch(double power) {
        /* make sure power is within [-1,1] */
        power = (power > 1.0) ? 1.0 : ((power < -1.0) ? -1.0 : power);
        AWR.setPower(power);
    }

    /* positive power extends, negative power retracts */
    public void moveWinches(double power) {
        moveLeftWinch(power);
        moveRightWinch(power);
    }
}
