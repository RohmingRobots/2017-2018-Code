package org.firstinspires.ftc.teamcode.SubAssemblyDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/* Sub Assembly Class
 */
public class DriveControl {
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Drive Control";

    /* FL - front left DC motor
     * FR - front right DC motor
     * BL - back left DC motor
     * BR - back right DC motor
     */
    private DcMotor FL = null;
    private DcMotor FR = null;
    private DcMotor BL = null;
    private DcMotor BR = null;

    /* Declare public class objects */

    /* ColorLeft- color sensor on left bottom of robot
     * ColorRight - color sensor on right bottom of robot
     */
    public ColorSensor ColorLeft = null;
    public ColorSensor ColorRight = null;

    public VoltageSensor Battery = null;

    /* getter methods
     * */

    /* Subassembly constructor */
    public DriveControl() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Map hardware devices */
        Battery = hardwareMap.voltageSensor.get("Lower hub 3");

        // Define and initialize Motors
        FL = hardwareMap.dcMotor.get("FL");
        FR = hardwareMap.dcMotor.get("FR");
        BL = hardwareMap.dcMotor.get("BL");
        BR = hardwareMap.dcMotor.get("BR");
        // reverse those motors
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);
        // Set all motors to zero power
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
        // Set all motors to run without encoders.
        FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // **** Color sensors ****
        // Define and initialize color sensors
        ColorLeft = hardwareMap.colorSensor.get("left_color");
        ColorRight = hardwareMap.colorSensor.get("right_color");
        //turns all LEDs off
        ColorLeft.enableLed(false);
        ColorRight.enableLed(false);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public void moveCombination(double speed_forward_back, double speed_left_right, double speed_rotate_left_right) {
        double front_right = speed_forward_back + speed_left_right + speed_rotate_left_right;
        double front_left = speed_forward_back - speed_left_right - speed_rotate_left_right;
        double back_left = speed_forward_back + speed_left_right - speed_rotate_left_right;
        double back_right = speed_forward_back - speed_left_right + speed_rotate_left_right;

        FR.setPower(front_right);
        FL.setPower(front_left);
        BL.setPower(back_left);
        BR.setPower(back_right);
    }

    /* forward is positive speed, backward is negative speed */
    public void moveForwardBackward(double speed) {
        FR.setPower(speed);
        FL.setPower(speed);
        BL.setPower(speed);
        BR.setPower(speed);
    }

    /* left is positive speed, right is negative speed */
    public void moveLeftRight(double speed) {
        FR.setPower(speed);
        FL.setPower(-speed);
        BL.setPower(speed);
        BR.setPower(-speed);
    }

    /* rotate left is positive speed, rotate right is negative speed */
    public void rotateLeftRight(double speed) {
        FR.setPower(speed);
        FL.setPower(-speed);
        BL.setPower(-speed);
        BR.setPower(speed);
    }

    /* Short hand movement methods */
    public void moveStop() {
        moveForwardBackward(0.0);
    }

    public void moveForward(double speed) {
        moveForwardBackward(Math.abs(speed));
    }

    public void moveBackward(double speed) {
        moveForwardBackward(-Math.abs(speed));
    }

    public void moveLeft(double speed) {
        moveLeftRight(Math.abs(speed));
    }

    public void moveRight(double speed) {
        moveLeftRight(-Math.abs(speed));
    }

    public void rotateLeft(double speed) {
        rotateLeftRight(Math.abs(speed));
    }

    public void rotateRight(double speed) {
        rotateLeftRight(-Math.abs(speed));
    }

}
