package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cCompassSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;
import com.qualcomm.robotcore.util.ElapsedTime;


public class RobotConfig
{
    /* Public members
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
    public DcMotor  FL   = null;
    public DcMotor  FR  = null;
    public DcMotor  BL   = null;
    public DcMotor  BR = null;

    /* Public members
    * Devices
    * -------
    * Make sure to control both left and right arms in unison
    * LR - lower right arm DC motor
    * LL - lower left arm DC motor (must be reverse of LR)
    * UR - upper right arm DC motor
    * UL - upper left arm DC motor (must be same of UR)
    */
    private DcMotor  LR = null;
    private DcMotor  LL = null;
    private DcMotor  UR = null;
    private DcMotor  UL = null;

    /* Arm sensors */
    public DigitalChannel ArmSwitch = null;         /* home switch */
    public AnalogInput UpperArmPot = null;          /* potentiometers */


    /* Public members
    * Devices
    * -------
    * GGR - gripper grabber right servo motor
    * GGL - gripper grabber right servo motor
    */
    public Servo GGR = null;
    public Servo GGL = null;
    public Servo Claw = null;
    /* open full, closed full, partial open */
    public double[] GRABBER_LEFT = {0.745, .255, .375};
    public double[] GRABBER_RIGHT = {0.44, .89, .765};
    public double[] CLAW = {0.9, 0.15};

    public ColorSensor left_color = null;
    public ColorSensor right_color = null;

    /* Public
    * arm control class
    */
    ArmControl  Arm = new ArmControl();


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
        FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // **** Arm motors ****
        // Define and Initialize Motors
        UL = hwMap.dcMotor.get("UL");
        UR = hwMap.dcMotor.get("UR");
        LL = hwMap.dcMotor.get("LL");
        LR = hwMap.dcMotor.get("LR");
        // reverse those motors
        UR.setDirection(DcMotor.Direction.REVERSE);
        LR.setDirection(DcMotor.Direction.REVERSE);
        // Set all motors to zero power
        LL.setPower(0);
        LR.setPower(0);
        UL.setPower(0);
        UR.setPower(0);
        // Set all motors to run with encoders.
        LR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        UR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        UL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Set motors to brake on zero power
        UR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        UL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // **** Gripper grabbers ****
        // Define and Initialize Motors
        GGR = hwMap.servo.get("GGR");
        GGL = hwMap.servo.get("GGL");
        Claw = hwMap.servo.get("Claw");
        // set initial positions
        GGL.setPosition(GRABBER_LEFT[0]);
        GGR.setPosition(GRABBER_RIGHT[0]);
        Claw.setPosition(CLAW[0]);

        // **** Color sensors ****
        // Define and Initialize color sensors
        left_color = hwMap.colorSensor.get("left_color");
        right_color = hwMap.colorSensor.get("right_color");

        // **** Arm Switch ****
        // Define and initialize switch
        ArmSwitch = hwMap.digitalChannel.get("touch sensor");
        // set the digital channel to input.
        ArmSwitch.setMode(DigitalChannel.Mode.INPUT);
        // false = pressed

        // **** Arm Potentiometers ****
        // Define and initialize potentiometers
        UpperArmPot = hwMap.analogInput.get("upper pot");

        // **** Initialize arm control
        Arm.init();
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


    /********** Arm Control class **********/
    public class ArmControl {
        //declaring all my variables in one place for my sake
        private double UpperArmHomePosition = 0;        /* position value at home */
        private double UpperArmPosition = 0;            /* current position relative to home */
        private double UpperArmLastPosition = 0;
        private double UpperArmVelocity = 0;
        private double UpperArmFinalTarget = 0;         /* final target position */
        private double UpperArmTarget = 0;              /* target position */
        private boolean Homed = false;
        private ElapsedTime Time = new ElapsedTime();

        /* Constructor */
        public ArmControl() {
        }

        /* Initialize standard Hardware interfaces */
        public void init() {
            UpperArmHomePosition = UpperArmPot.getVoltage();
        }

        public void MoveUp() {
            UpperArmFinalTarget += 0.01;
            if (UpperArmFinalTarget > 1.0) UpperArmFinalTarget = 1.0;
        }

        public void MoveDown() {
            UpperArmFinalTarget -= 0.01;
            if (UpperArmFinalTarget < 0.0) UpperArmFinalTarget = 0.0;
        }

        public void MoveHome() {
            UpperArmFinalTarget = 0.0;
        }

        public void HoldCurrentPosition() {
            UpperArmFinalTarget = UpperArmPosition;
        }

        public void MoveToPosition(double target) {
            UpperArmFinalTarget = target;
            if (UpperArmFinalTarget > 0.6) UpperArmFinalTarget = 0.6;
            if (UpperArmFinalTarget < 0.0) UpperArmFinalTarget = 0.0;
        }

        /* Call this method when you want to update the arm motors */
        public void Update(OpMode om) {
            boolean at_home;                 /* home switch active */
            double upper_arm;
            double error;
            final double UPPER_ARM_HOLD_POWER = 0.01;
            final double UPPER_ARM_POWER = 0.2;

            /* Check to see if on home switch */
            at_home = false;
            if (ArmSwitch.getState() == false) {
                /* arm in home position */
                at_home = true;
                Homed = true;
                UpperArmHomePosition = UpperArmPot.getVoltage();

                //adds a lil' version thing to the telemetry so you know you're using the right version
                om.telemetry.addLine("At Home");
            }

            /* determine current position relative to home */
            UpperArmPosition = UpperArmPot.getVoltage() - UpperArmHomePosition;

            /* determine velocity */
            UpperArmVelocity = 1000 * (UpperArmPosition - UpperArmLastPosition) / Time.milliseconds();
            UpperArmLastPosition = UpperArmPosition;
            Time.reset();

            /* incrementally change target value */
            if (UpperArmTarget < UpperArmFinalTarget - 0.01)    UpperArmTarget += 0.02;
            if (UpperArmTarget > UpperArmFinalTarget + 0.01)    UpperArmTarget -= 0.02;
            if (UpperArmFinalTarget < 0.01) UpperArmTarget = 0.0;
            if (UpperArmTarget > 0.6) UpperArmTarget = 0.6;
            if (UpperArmTarget < 0.0) UpperArmTarget = 0.0;

            /*********** control code **********/
            error = UpperArmTarget - UpperArmPosition;
            if (error > 0.2) error = 0.2;
            if (error < -0.2) error = -0.2;

            upper_arm = UPPER_ARM_POWER * 5 * error;

            if ( (error>0.0) && (UpperArmVelocity<0.0)) {
                /* dropping down, give power boost */
                upper_arm += UPPER_ARM_POWER * (-2.0 * UpperArmVelocity);
                om.telemetry.addLine("++++ Boost");
            } else if ( (error<0.0) && (UpperArmVelocity>0.0)) {
                /* passing by, reverse thrusters */
                upper_arm += UPPER_ARM_POWER * (-0.5 * UpperArmVelocity);
                om.telemetry.addLine("-- Reverse");
            } else if ((UpperArmTarget > 0.0) && (Math.abs(upper_arm) < UPPER_ARM_HOLD_POWER) ) {
                /* always use positive power when trying to hold */
                upper_arm = UPPER_ARM_HOLD_POWER;
                om.telemetry.addLine("..........");
            }

            /* prevent negative power when...
                at home position or never homed
            */
            if (at_home || !Homed) {
                if (upper_arm < 0.0) upper_arm = 0.0;
            }

            /* when target is zero ...
            * kill power, let braking bring it down
            */
            if (UpperArmTarget < 0.01) {
                upper_arm = 0.0;
            }

            om.telemetry.addData("Velocity", "%.3f", UpperArmVelocity);
            om.telemetry.addData("Target Position", "%.2f %.2f", UpperArmTarget, UpperArmPosition);
            om.telemetry.addData("Error  Power   ", "%.2f %.2f", error, upper_arm);

            UR.setPower(upper_arm);
            UL.setPower(upper_arm);
        }
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
