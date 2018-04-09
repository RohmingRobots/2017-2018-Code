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
    public AnalogInput Potentiometer = null;    /* potentiometers */

    //declaring all my variables in one place for my sake
    private double HomePosition = 0;        /* position value at home */
    public double CurrentPosition = 0;      /* current position relative to home */
    private double FinalTarget = 0;         /* final target position */
    private double FinalTime = 0;           /* time to move to final target */
    private double CurrentTarget = 0;       /* current target position */
    private double CurrentTime = 0;         /* current time to target */
    private boolean Homed = false;          /* arm has been at home - home position valid */
    private boolean AtHome = false;         /* home switch active - currently at home */
    private double ErrorSum = 0.0;          /* integral error */
    public double Power = 0.0;              /* power to send to motors */
    private double MAX_POS_POWER = 0.0;
    private double MAX_NEG_POWER = 0.0;
    private double INTEGRAL_GAIN = 0.0;
    private double MAX_POSITION = 1.5;

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
            //UPPER
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
//            MAX_POS_POWER = 0.5;
//            MAX_NEG_POWER = 0.2;
//            INTEGRAL_GAIN = 0.5;
            MAX_POS_POWER = 0.5;    // 0.8
            MAX_NEG_POWER = 0.2;    // 0.2
            INTEGRAL_GAIN = 0.5;    // 0.0
        } else {
            LeftMotor = hwMap.dcMotor.get("LL");
            RightMotor = hwMap.dcMotor.get("LR");
            // reverse those motors
            RightMotor.setDirection(DcMotor.Direction.REVERSE);

            // Define and initialize switch
            Limit = hwMap.digitalChannel.get("lower limit");
            Limit.setMode(DigitalChannel.Mode.INPUT);           // false = pressed

            // Define and initialize potentiometers
            Potentiometer = hwMap.analogInput.get("lower pot");

            // Set power values
            MAX_POS_POWER = 0.8;
            MAX_NEG_POWER = 0.1;
            INTEGRAL_GAIN = 0.0;    // not needed??
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
        LeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // record default home position
        HomePosition = Potentiometer.getVoltage();

        ErrorSum = 0.0;     // zero integral
    }

    public void MoveUp() {
        FinalTarget += 0.01;
        if (FinalTarget > MAX_POSITION) FinalTarget = MAX_POSITION;
        FinalTime = 0.0;
    }

    public void MoveDown() {
        FinalTarget -= 0.01;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
        FinalTime = 0.0;
    }

    public void MoveHome() {
        MoveToPosition(0.0);
    }

    public void MoveHome(double time) {
        MoveToPosition(0.0, time);
    }

    public void HoldCurrentPosition() {
        FinalTarget = CurrentPosition;
        FinalTime = 0.0;
    }

    public void MoveToPosition(double target) {
        MoveToPosition(target, 0.0);
    }

    public void MoveToPosition(double target, double time) {
        FinalTarget = target;
        if (FinalTarget > MAX_POSITION) FinalTarget = MAX_POSITION;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
        FinalTime = time;
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

            ErrorSum = 0.0;     // zero integral
            //adds a lil' version thing to the telemetry so you know you're using the right version
//            om.telemetry.addLine("At Home");
        }

        /* incrementally change target value */
        CurrentTime = OurTime.seconds();
        if (CurrentTime < FinalTime) {
            CurrentTarget = CurrentTarget + (FinalTarget-CurrentTarget)*CurrentTime/FinalTime;
            FinalTime = FinalTime - CurrentTime;
        } else {
            CurrentTarget = FinalTarget;
            FinalTime = 0.0;
        }

        /* determine current position relative to home */
        CurrentPosition = Potentiometer.getVoltage() - HomePosition;

        /*********** control code **********/
        error = CurrentTarget - CurrentPosition;
        if (error > 0.2) error = 0.2;
        if (error < -0.2) error = -0.2;


        /* update integral */
        ErrorSum += error*OurTime.milliseconds()/1000.0;
        /* limit integral gain if never homed */
        if (!Homed) {
            if (ErrorSum > 0.5)
                ErrorSum = 0.5;
        }

        /* determine proportional gain */
        if (error > 0.0 ) {
            Power = MAX_POS_POWER * 5 * error;
        } else {
            Power = MAX_NEG_POWER * 5 * error;
        }

        /* determine integral gain */
        Power += ErrorSum*INTEGRAL_GAIN;

        /* limit power */
        if (Power>1.0) Power = 1.0;
        if (Power<-1.0) Power = -1.0;

        /* prevent negative power when...
            at home position or never homed
        */
        if (AtHome || !Homed) {
            if (Power < 0.0) {
                ErrorSum = 0.0;     // zero integral
                Power = 0.0;
            }
        }

        /* when target is zero ...
        * kill power, let braking bring it down
        */
        if (CurrentTarget < 0.01) {
            if (UpperLower){
                // upper arm braking is sufficient to bring it down in a controlled manner
                if (CurrentPosition<0.5) {
                    ErrorSum = 0.0;     // zero integral
                    Power = 0.0;
                }
            } else {
                // braking is very strong on lower arm
                // need to bring it all the way down before braking otherwise it will not drop
                if (CurrentPosition<0.02) {
                    ErrorSum = 0.0;     // zero integral
                    Power = 0.0;
                } else if (CurrentPosition<0.1) {
                    // reduce power right before home to lesson impact
                    Power = Power/5.0;
                }
            }
        }

        OurTime.reset();

        RightMotor.setPower(Power);
        LeftMotor.setPower(Power);

        if (om!=null)
            om.telemetry.addData("Power Error Sum", "%.2f %.2f %.2f", Power, error, ErrorSum);
    }

    public void SetPower(double power) {
        RightMotor.setPower(power);
        LeftMotor.setPower(power);
    }
}
