package org.firstinspires.ftc.teamcode.SubAssemblyAmpere;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


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
    public ColorSensor ColorLeft = null;
    public ColorSensor ColorRight = null;

    /* open full, closed full, partial open */
    private double[] AMPERE_FLICKER_LEFT = {0.0, 0.6, 1.0};
    private double[] AMPERE_FLICKER_RIGHT = {0.0, 0.6, 1.0};

    private int LeftIndex = 0;
    private int RightIndex = 0;

    /* Declare public class objects */

    /* getter methods
     * */

    /* Subassembly constructor */
    public AmpereControl() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void Initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

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
        AFL.setPosition(AMPERE_FLICKER_LEFT[LeftIndex]);
        AFR.setPosition(AMPERE_FLICKER_RIGHT[RightIndex]);

        // Define and Initialize color sensors
        ColorLeft = hardwareMap.colorSensor.get("left_ampere");
        ColorRight = hardwareMap.colorSensor.get("right_ampere");
        //turns all LEDs off
        ColorLeft.enableLed(false);
        ColorRight.enableLed(false);
    }

    /* Cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void Cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public void ExtendLeft(double power) {
        /* make sure power is within [0,1] */
        power = Math.min(Math.abs(power), 1.0);
        AWL.setPower(power);
    }

    public void ExtendRight(double power) {
        /* make sure power is within [0,1] */
        power = Math.min(Math.abs(power), 1.0);
        AWR.setPower(power);
    }

    public void Extend(double power) {
        /* make sure power is within [0,1] */
        power = Math.min(Math.abs(power), 1.0);
        AWL.setPower(power);
        AWR.setPower(power);
    }

    public void RetractLeft(double power) {
        /* make sure power is within [-1,0] */
        power = -Math.min(Math.abs(power), 1.0);
        AWL.setPower(power);
    }

    public void RetractRight(double power) {
        /* make sure power is within [-1,0] */
        power = -Math.min(Math.abs(power), 1.0);
        AWR.setPower(power);
    }

    public void Retract(double power) {
        /* make sure power is within [-1,0] */
        power = -Math.min(Math.abs(power), 1.0);
        AWL.setPower(power);
        AWR.setPower(power);
    }

    public void IncrementLeft() {
        LeftIndex = (LeftIndex < AMPERE_FLICKER_LEFT.length - 1) ? LeftIndex + 1 : 0;
        AFL.setPosition(AMPERE_FLICKER_LEFT[LeftIndex]);
    }

    public void IncrementRight() {
        RightIndex = (RightIndex < AMPERE_FLICKER_RIGHT.length - 1) ? RightIndex + 1 : 0;
        AFR.setPosition(AMPERE_FLICKER_RIGHT[RightIndex]);
    }

    public void DecrementLeft() {
        LeftIndex = (LeftIndex > 0) ? LeftIndex - 1 : 0;
        AFL.setPosition(AMPERE_FLICKER_LEFT[LeftIndex]);
    }

    public void DecrementRight() {
        RightIndex = (RightIndex > 0) ? RightIndex - 1 : 0;
        AFR.setPosition(AMPERE_FLICKER_RIGHT[RightIndex]);
    }

    public void SetPositionLeft(int index) {
        if (index < 0) index = 0;
        if (index > AMPERE_FLICKER_LEFT.length - 1) index = AMPERE_FLICKER_LEFT.length - 1;

        LeftIndex = index;
        AFL.setPosition(AMPERE_FLICKER_LEFT[LeftIndex]);
    }

    public void SetPositionRight(int index) {
        if (index < 0) index = 0;
        if (index > AMPERE_FLICKER_RIGHT.length - 1) index = AMPERE_FLICKER_RIGHT.length - 1;

        RightIndex = index;
        AFR.setPosition(AMPERE_FLICKER_RIGHT[RightIndex]);
    }

    public void SetPosition(int index) {
        if (index < 0) index = 0;
        if (index > AMPERE_FLICKER_LEFT.length - 1) index = AMPERE_FLICKER_LEFT.length - 1;

        LeftIndex = index;
        RightIndex = index;
        AFL.setPosition(AMPERE_FLICKER_LEFT[LeftIndex]);
        AFR.setPosition(AMPERE_FLICKER_RIGHT[RightIndex]);
    }
}
