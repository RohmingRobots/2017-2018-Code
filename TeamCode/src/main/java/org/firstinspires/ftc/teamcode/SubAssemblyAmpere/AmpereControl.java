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
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Ampere Control";

    /* AWL - continuous servo motor for left arm winch
     * AWR - continuous servo motor for right arm winch
     * AFL - left arm flipper servo motor
     * AFR - right arm flipper servo motor
     * CSL - color sensor on left side arm
     * CSR - color sensor on right side arm
     */
    private CRServo AWL = null;
    private CRServo AWR = null;
    private Servo AFL = null;
    private Servo AFR = null;

    private EnumMap<Setpoints, Double> MapLeftFlipper = new EnumMap<Setpoints, Double>(Setpoints.class);
    private EnumMap<Setpoints, Double> MapRightFlipper = new EnumMap<Setpoints, Double>(Setpoints.class);
    public ServoControl<Setpoints, EnumMap<Setpoints, Double>> LeftFlipperServo = new ServoControl(AFL,MapLeftFlipper,Setpoints.CLOSE);
    public ServoControl<Setpoints, EnumMap<Setpoints, Double>> RightFlipperServo = new ServoControl(AFR,MapRightFlipper,Setpoints.CLOSE);


    /* Declare public class objects */
    public ColorSensor ColorLeft = null;
    public ColorSensor ColorRight = null;

    public enum Setpoints implements EnumWrapper<Setpoints> {CLOSE, PARTIAL, OPEN;}


    /* getter methods
     * */


    /* Subassembly constructor */
    public AmpereControl() {
    }

    /* default initialize is to set servos to initial setpoint */
    public void initialize(LinearOpMode opMode) {
        initialize(opMode, true);
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode, boolean init_servos) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Assign setpoint values
         */
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
        // set initial positions
        if (init_servos) {
            LeftFlipperServo.setSetpoint(Setpoints.CLOSE);
            RightFlipperServo.setSetpoint(Setpoints.CLOSE);
        }

        // Define and initialize color sensors
        ColorLeft = hardwareMap.colorSensor.get("left_ampere");
        ColorRight = hardwareMap.colorSensor.get("right_ampere");
        //turns all LEDs off
        ColorLeft.enableLed(false);
        ColorRight.enableLed(false);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
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
