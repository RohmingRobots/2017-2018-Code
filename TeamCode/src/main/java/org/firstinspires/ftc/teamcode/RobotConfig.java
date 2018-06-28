package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;


public class RobotConfig
{
    /* Public members - Mecanum subassembly
    * Devices
    * -------
    * FL - front left DC motor
    * FR - front right DC motor
    * BL - back left DC motor
    * BR - back right DC motor
    *
    * Methods
    * -------
    * MoveStop()
    * MoveForward(speed)
    * MoveBackward(speed)
    * MoveLeft(speed)
    * MoveRight(speed)
    * RotateLeft(speed)
    * RotateRight(speed)
    */
    public DcMotor  FL = null;
    public DcMotor  FR = null;
    public DcMotor  BL = null;
    public DcMotor  BR = null;

    /* Public members - gripper grabber subassembly
    * Devices
    * -------
    * GGR - gripper grabber right servo motor
    * GGL - gripper grabber right servo motor
    * Claw - top grabber
    * RG - right glyph guide
    * LG - left glyph guide
    */
    public Servo GGR = null;
    public Servo GGL = null;
    public Servo Claw = null;
    public Servo RG = null;
    public Servo LG = null;
    /* open full, closed full, partial open */
    public double[] GRABBER_LEFT = {0.745, .255, .375};
    public double[] GRABBER_RIGHT = {0.44, .89, .765};
    public double[] CLAW = {0.7, 0.0};      // original claw {0.9, 0.1};
    public double[] GUIDESLEFT = {1, 0.2};
    public double[] GUIDESRIGHT = {0.94, 0.14};

    /* Public members - Ampere (side arm) subassembly
    * Devices
    * -------
    * AWL - continuous servo motor for left arm winch
    * AWR - continuous servo motor for right arm winch
    * AFL - left arm flipper servo motor
    * AFR - right arm flipper servo motor
    * left_ampere - color sensor on left side arm
    * right_ampere - color sensor on right side arm
    */
    public CRServo AWL = null;
    public CRServo AWR = null;
    public Servo AFL = null;
    public Servo AFR = null;
    public Servo ANTIREDNEK = null;
    public ColorSensor left_ampere = null;
    public ColorSensor right_ampere = null;
    /* open full, closed full, partial open */
    public double[] AMPERE_FLICKER_LEFT = {0.0, 0.6, 1.0};
    public double[] AMPERE_FLICKER_RIGHT = {0.0, 0.6, 1.0};

    /* Public members - color tracking subassembly
    * Devices
    * -------
    * left_color - color sensor on left bottom of robot
    * right_color - color sensor on right bottom of robot
    */
    public ColorSensor left_color = null;
    public ColorSensor right_color = null;

    /* Public
    * IMU objects
    */
    public VoltageSensor Battery = null;

    /* Local OpMode members. */
    HardwareMap hwMap  = null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public RobotConfig() {
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // save reference to HW Map
        hwMap = ahwMap;

        // **** Mecanum drive ****
        // Define and Initialize Motors
        FL = hwMap.dcMotor.get("FL");
        FR = hwMap.dcMotor.get("FR");
        BL = hwMap.dcMotor.get("BL");
        BR = hwMap.dcMotor.get("BR");
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

        // **** Gripper grabbers & guides ****
        // Define and Initialize Motors
        GGR = hwMap.servo.get("GGR");
        GGL = hwMap.servo.get("GGL");
        Claw = hwMap.servo.get("Claw");
        LG = hwMap.servo.get("LG");
        RG = hwMap.servo.get("RG");
        // reverse those motors
        LG.setDirection(Servo.Direction.REVERSE);
        // set initial positions
        GGL.setPosition(GRABBER_LEFT[0]);
        GGR.setPosition(GRABBER_RIGHT[0]);
// don't initialize position, let it set on arm        Claw.setPosition(CLAW[0]);
        LG.setPosition(GUIDESLEFT[0]);
        RG.setPosition(GUIDESRIGHT[0]);


        // **** Ampere (side arms and flippers) ****
        // Side arms
        // Define and Initialize Motors
        AWL = hwMap.crservo.get("AWL");
        AWR = hwMap.crservo.get("AWR");
        ANTIREDNEK = hwMap.servo.get("ANTIREDNEK");
        // reverse those motors
        AWR.setDirection(CRServo.Direction.REVERSE);
        // set all motors to zero power
        AWL.setPower(0.0);
        AWR.setPower(0.0);

        // Define and Initialize Motors
        AFL = hwMap.servo.get("AFL");
        AFR = hwMap.servo.get("AFR");
        // reverse those motors
        AFR.setDirection(Servo.Direction.REVERSE);
        // set initial positions
        AFL.setPosition(AMPERE_FLICKER_LEFT[0]);
        AFR.setPosition(AMPERE_FLICKER_RIGHT[0]);

        // Define and Initialize color sensors
        left_ampere = hwMap.colorSensor.get("left_ampere");
        right_ampere = hwMap.colorSensor.get("right_ampere");
        //turns all LEDs off
        left_ampere.enableLed(false);
        right_ampere.enableLed(false);

        // **** Color sensors ****
        // Define and Initialize color sensors
        left_color = hwMap.colorSensor.get("left_color");
        right_color = hwMap.colorSensor.get("right_color");
        //turns all LEDs off
        left_color.enableLed(false);
        right_color.enableLed(false);

        // **** IMU objects ****
        Battery = hwMap.voltageSensor.get("Lower hub 3");
    }

    /* forward is positive speed, backward is negative speed */
    public void MoveForwardBackward(double speed) {
        FR.setPower(speed);
        FL.setPower(speed);
        BL.setPower(speed);
        BR.setPower(speed);
    }
    /* left is positive speed, right is negative speed */
    public void MoveLeftRight(double speed) {
        FR.setPower(speed);
        FL.setPower(-speed);
        BL.setPower(speed);
        BR.setPower(-speed);
    }
    /* rotate left is positive speed, rotate right is negative speed */
    public void RotateLeftRight(double speed) {
        FR.setPower(speed);
        FL.setPower(-speed);
        BL.setPower(-speed);
        BR.setPower(speed);
    }

    /* Short hand movement methods */
    public void MoveStop() {
        MoveForwardBackward(0.0);
    }
    public void MoveForward(double speed) {
        MoveForwardBackward(speed);
    }
    public void MoveBackward(double speed) {
        MoveForwardBackward(-speed);
    }
    public void MoveLeft(double speed) {
        MoveLeftRight(speed);
    }
    public void MoveRight(double speed) {
        MoveLeftRight(-speed);
    }
    public void RotateLeft(double speed) {
        RotateLeftRight(speed);
    }
    public void RotateRight(double speed) {
        RotateLeftRight(-speed);
    }


    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     * @throws InterruptedException
     */
    public void waitForTick(long periodMs)  throws InterruptedException {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0)
            Thread.sleep(remaining);

        // Reset the cycle clock for the next pass.
        period.reset();
    }
}
