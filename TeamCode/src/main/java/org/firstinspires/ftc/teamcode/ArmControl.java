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
    private double TargetTime = 0;
    private double CurrentTarget = 0;       /* target position */
    private boolean Homed = false;          /* arm has been at home - home position valid */
    private boolean AtHome = false;         /* home switch active - currently at home */
    private double ErrorSum = 0.0;          /* integral error */
    public double Power = 0.0;              /* power to send to motors */
    private double HIGH_MAX_POWER = 0.0;
    private double LOW_MAX_POWER = 0.0;
    private double INTEGRAL_GAIN = 0.0;
<<<<<<< HEAD
    private double MAX_POSITION = 0.0;
    private double NINETY_ANGLE_RANGE = 0.0;
    private double INITIAL_ANGLE = 0.0;
    private double POSITION_TO_ANGLE = 0.0;
    private double RelativeAngle = 0.0;
    public double Angle = 0.0;
=======
>>>>>>> 7de7784c395016a13bc6a43fde21d61cd3b680be

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
            // 2V is approximately 180 degrees
            Potentiometer = hwMap.analogInput.get("upper pot");

            // Set arm constants
            HIGH_MAX_POWER = 0.8;    //0.8
            LOW_MAX_POWER = 0.2;    //0.2
            INTEGRAL_GAIN = 0.0;

            MAX_POSITION = 2.0;
            NINETY_ANGLE_RANGE = 1.0;
            INITIAL_ANGLE = -10.0;
            POSITION_TO_ANGLE = 90.0;
        } else {
            //LOWER
            // Define and Initialize Motors
            LeftMotor = hwMap.dcMotor.get("LL");
            RightMotor = hwMap.dcMotor.get("LR");
            // reverse those motors
            RightMotor.setDirection(DcMotor.Direction.REVERSE);

            // Define and initialize switch
            Limit = hwMap.digitalChannel.get("lower limit");
            Limit.setMode(DigitalChannel.Mode.INPUT);           // false = pressed

            // Define and initialize potentiometers
            // 1V is approximately 180 degrees
            Potentiometer = hwMap.analogInput.get("lower pot");

            // Set arm constants
            HIGH_MAX_POWER = 0.8;    //0.8
            LOW_MAX_POWER = 0.1;    //0.1
            INTEGRAL_GAIN = 0.0;    // not needed??

            MAX_POSITION = 1.0;
            NINETY_ANGLE_RANGE = 0.5;
            INITIAL_ANGLE = -5.0;
            POSITION_TO_ANGLE = 180.0;
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

    public void MoveIncrement(double inc, double time) {
        FinalTarget += inc;
        if (FinalTarget > MAX_POSITION) FinalTarget = MAX_POSITION;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
        TargetTime = time;
    }

    public void MoveHome() {
        FinalTarget = 0.0;
    }

    public void HoldCurrentPosition() {
        FinalTarget = CurrentPosition;
    }

    public void MoveToPosition(double target, double time) {
        FinalTarget = target;
        if (FinalTarget > MAX_POSITION) FinalTarget = MAX_POSITION;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
        TargetTime = time;
    }

    /* Call this method when you want to update the arm motors */
    public void Update(OpMode om, double offset) {
        double seconds;
        double error;
        double max_power;

        /* Check to see if on home switch */
        AtHome = false;
        if (Limit.getState() == false) {
            /* arm in home position */
            AtHome = true;
            Homed = true;
            HomePosition = Potentiometer.getVoltage();

            ErrorSum = 0.0;     // zero integral
        }

        seconds = OurTime.seconds();
        OurTime.reset();

        /* incrementally change target value */
        if (TargetTime > seconds) {
            CurrentTarget = CurrentTarget + (FinalTarget-CurrentTarget)*seconds/TargetTime;
            TargetTime = TargetTime - seconds;
        } else {
            CurrentTarget = FinalTarget;
            TargetTime = 0.0;
        }

        /* determine current position relative to home */
        CurrentPosition = Potentiometer.getVoltage() - HomePosition;

        RelativeAngle = POSITION_TO_ANGLE*CurrentPosition + INITIAL_ANGLE;
        Angle = RelativeAngle + offset;

        /*********** control code **********/
        error = CurrentTarget - CurrentPosition;
        if (error > 0.2) error = 0.2;
        if (error < -0.2) error = -0.2;
<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> parent of 2853f73... reverting
=======
>>>>>>> parent of 1298d51... Merge branch 'master' of https://github.com/RohmingRobots/2017-2018-Code

        /* update integral */
        ErrorSum += error*seconds;
        om.telemetry.addData("Time Sum","%7.3f %6.3f",seconds,ErrorSum);

        /* limit integral gain if never homed */
        if (!Homed) {
            if (ErrorSum > 0.5)
                ErrorSum = 0.5;
        }

<<<<<<< HEAD
        /* determine proportional gain */
        if (error > 0.0 ) {
            if (Angle < 120.0) {
                max_power = (LOW_MAX_POWER-HIGH_MAX_POWER)*Angle/120.0 + HIGH_MAX_POWER;
            } else {
                max_power = LOW_MAX_POWER;
            }
        } else {
            if (Angle < 60.0) {
                max_power = LOW_MAX_POWER;
            } else {
                max_power = (HIGH_MAX_POWER-LOW_MAX_POWER)*(Angle-60.0)/120.0 + LOW_MAX_POWER;
            }
=======
>>>>>>> 7de7784c395016a13bc6a43fde21d61cd3b680be
        }
        Power = max_power * 5 * error;

        /* determine integral gain */
        Power += ErrorSum*INTEGRAL_GAIN;

        /* limit power */
<<<<<<< HEAD
        if (Power>1.0) Power = 1.0;
        if (Power<-1.0) Power = -1.0;
=======
>>>>>>> 7de7784c395016a13bc6a43fde21d61cd3b680be

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

        RightMotor.setPower(Power);
        LeftMotor.setPower(Power);

        if (om!=null)
            om.telemetry.addData("Power Error Angle", "%.2f %.2f %5.0f", Power, error, RelativeAngle);
    }

    public void SetPower(double power) {
        RightMotor.setPower(power);
        LeftMotor.setPower(power);
    }
}
