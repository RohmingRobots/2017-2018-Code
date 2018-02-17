package org.firstinspires.ftc.teamcode;

/**
 * Created by ablauch on 2/15/2018.
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

/********** Arm Control class **********/
public class ArmControl {
    private boolean UpperLower = true;

    /* Private members - use ArmControl methods
    * Devices
    * -------
    * Make sure to control both left and right arms in unison
    * LR - lower right arm DC motor
    * LL - lower left arm DC motor (must be reverse of LR)
    * UR - upper right arm DC motor
    * UL - upper left arm DC motor (must be same of UR)
    */
    private DcMotor RightMotor = null;
    private DcMotor LeftMotor = null;

    /* Arm sensors */
    public DigitalChannel Limit = null;         /* home switch */
    private AnalogInput Potentiometer = null;    /* potentiometers */

    //declaring all my variables in one place for my sake
    private double HomePosition = 0;        /* position value at home */
    public double CurrentPosition = 0;      /* current position relative to home */
    private double LastPosition = 0;        /* last position relative to home, used to calculate velocity */
    private double CurrentVelocity = 0;     /* velocity of arm */
    private double FinalTarget = 0;         /* final target position */
    private double CurrentTarget = 0;       /* target position */
    private boolean Homed = false;          /* arm has been at home - home position valid */
    private boolean AtHome = false;         /* home switch active - currently at home */
    public double Power = 0.0;              /* power to send to motors */
    private double MAX_POS_POWER = 0.0;
    private double MAX_NEG_POWER = 0.0;

    private ElapsedTime OurTime = new ElapsedTime();

    /* Constructor */
    public ArmControl() {
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap, boolean upper_lower) {
        HardwareMap hwMap  = null;

        // save reference to HW Map
        hwMap = ahwMap;
        UpperLower = upper_lower;

        if (UpperLower) {
            // Define and Initialize Motors
            LeftMotor = hwMap.dcMotor.get("UL");
            RightMotor = hwMap.dcMotor.get("UR");
            // reverse those motors
            RightMotor.setDirection(DcMotor.Direction.REVERSE);

            // Define and initialize switch
            Limit = hwMap.digitalChannel.get("upper limit");
            Limit.setMode(DigitalChannel.Mode.INPUT);           // false = pressed

            // Define and initialize potentiometers
            Potentiometer = hwMap.analogInput.get("upper pot");

            // Set power values
            MAX_POS_POWER = 0.8;
            MAX_NEG_POWER = 0.2;
        } else {
            LeftMotor = hwMap.dcMotor.get("LL");
            RightMotor = hwMap.dcMotor.get("LR");
            // reverse those motors
            RightMotor.setDirection(DcMotor.Direction.REVERSE);

            // Define and initialize switch
//AJB            Limit = hwMap.digitalChannel.get("lower limit");
            Limit = hwMap.digitalChannel.get("upper limit");
            Limit.setMode(DigitalChannel.Mode.INPUT);           // false = pressed

            // Define and initialize potentiometers
            Potentiometer = hwMap.analogInput.get("lower pot");

            // Set power values
            MAX_POS_POWER = 0.8;
            MAX_NEG_POWER = 0.2;
        }
        // Set all motors to zero power
        LeftMotor.setPower(0);
        RightMotor.setPower(0);
        // Set all motors to run with encoders.
//        LeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        RightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // ****** DO NOT CHANGE ****** Set all motors to run WITHOUT encoders.
        LeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Set motors to brake on zero power
        LeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        RightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // record default home position
        HomePosition = Potentiometer.getVoltage();
    }

    public void MoveUp() {
        FinalTarget += 0.01;
        if (FinalTarget > 1.0) FinalTarget = 1.0;
    }

    public void MoveDown() {
        FinalTarget -= 0.01;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
    }

    public void MoveHome() {
        FinalTarget = 0.0;
    }

    public void HoldCurrentPosition() {
        FinalTarget = CurrentPosition;
    }

    public void MoveToPosition(double target) {
        FinalTarget = target;
        if (FinalTarget > 0.6) FinalTarget = 0.6;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
    }

    /* Call this method when you want to update the arm motors */
    public void Update(OpMode om) {
        double error;

        /* Check to see if on home switch */
        AtHome = false;
        if (Limit.getState() == false) {
            /* arm in home position */
            AtHome = true;
            Homed = true;
            HomePosition = Potentiometer.getVoltage();

            //adds a lil' version thing to the telemetry so you know you're using the right version
            om.telemetry.addLine("At Home");
        }

        /* determine current position relative to home */
        CurrentPosition = Potentiometer.getVoltage() - HomePosition;

        /* determine velocity */
        CurrentVelocity = 1000 * (CurrentPosition - LastPosition) / OurTime.milliseconds();
        LastPosition = CurrentPosition;
        OurTime.reset();

        /* incrementally change target value */
        if (CurrentTarget < FinalTarget - 0.01) CurrentTarget += 0.02;
        if (CurrentTarget > FinalTarget + 0.01) CurrentTarget -= 0.02;
        if (FinalTarget < 0.01) CurrentTarget = 0.0;
        if (CurrentTarget > 0.6) CurrentTarget = 0.6;
        if (CurrentTarget < 0.0) CurrentTarget = 0.0;

        /*********** control code **********/
        error = CurrentTarget - CurrentPosition;
        if (error > 0.2) error = 0.2;
        if (error < -0.2) error = -0.2;

        if (error > 0.0 ) {
            Power = MAX_POS_POWER * 5 * error;
        } else {
            Power = MAX_NEG_POWER * 5 * error;
        }

/*
        if ( (error>0.0) && (CurrentVelocity<0.0)) {
            // dropping down, give power boost
            Power += MAX_POWER * (-2.0 * CurrentVelocity);
            om.telemetry.addLine("++++ Boost");
        } else if ( (error<0.0) && (CurrentVelocity>0.0)) {
            // passing by, reverse thrusters
            Power += MAX_POWER * (-0.5 * CurrentVelocity);
            om.telemetry.addLine("-- Reverse");
        } else if ((CurrentTarget > 0.0) && (Math.abs(Power) < HOLD_POWER) ) {
            // always use positive power when trying to hold
            Power = HOLD_POWER;
            om.telemetry.addLine("..........");
        }
*/

        /* prevent negative power when...
            at home position or never homed
        */
        if (AtHome || !Homed) {
            if (Power < 0.0) Power = 0.0;
        }

        /* when target is zero ...
        * kill power, let braking bring it down
        */
//        if ( (CurrentTarget < 0.01) && (CurrentPosition<0.1) ) {
//            Power = 0.0;
//        }

        RightMotor.setPower(Power);
        LeftMotor.setPower(Power);
    }

    public void SetPower(double power) {
        RightMotor.setPower(power);
        LeftMotor.setPower(power);
    }
}
