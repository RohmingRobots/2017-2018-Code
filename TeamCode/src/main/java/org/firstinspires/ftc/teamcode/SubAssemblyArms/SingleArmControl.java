package org.firstinspires.ftc.teamcode.SubAssemblyArms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/* Sub Assembly Class
 */
public class SingleArmControl {
    /* Declare private class object
    * */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Single Arm Control";

    private boolean UpperLower = true;      /* which arm to control (true=>upper, false=>lower) */

    private DcMotor RightMotor = null;
    private DcMotor LeftMotor = null;

    //declaring all my variables in one place for my sake
    private double HomePosition = 0;        /* position value at home */
    private double CurrentPosition = 0;     /* current position relative to home */
    private double FinalTarget = 0;         /* final target position */
    private double FinalTime = 0;           /* time to move to final target */
    private double CurrentTarget = 0;       /* current target position */
    private double CurrentTime = 0;         /* current time to target */
    private boolean Homed = false;          /* arm has been at home - home position valid */
    private boolean AtHome = false;         /* home switch active - currently at home */
    private double ErrorSum = 0.0;          /* integral error */
    private double Power = 0.0;             /* power to send to motors */
    private double HIGH_MAX_POWER = 0.0;
    private double LOW_MAX_POWER = 0.0;
    private double INITIAL_ANGLE = 0.0;
    private double POSITION_TO_ANGLE = 0.0;
    private double RelativeAngle = 0.0;
    private double Angle = 0.0;
    private double INTEGRAL_GAIN = 0.0;
    private double MAX_POSITION = 1.5;

    private ElapsedTime OurTime = new ElapsedTime();


    /* Declare public class objects
    * */
    /* Arm sensors */
    public DigitalChannel Limit = null;         /* home switch */
    public AnalogInput Potentiometer = null;    /* potentiometers */

    /* getter methods
     * */
    public double getCurrentPosition() {
        return CurrentPosition;
    }
    public double getPower() {
        return Power;
    }
    public double getAngle() {
        return Angle;
    }

    /* Subassembly constructor */
    public SingleArmControl() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode, boolean upper_lower) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Map hardware devices */
        UpperLower = upper_lower;
        if (UpperLower) {
            //UPPER
            // Define and initialize Motors
            LeftMotor = hardwareMap.dcMotor.get("UL");
            RightMotor = hardwareMap.dcMotor.get("UR");
            // reverse those motors
            RightMotor.setDirection(DcMotor.Direction.REVERSE);

            // Define and initialize switch
            Limit = hardwareMap.digitalChannel.get("upper limit");
            Limit.setMode(DigitalChannel.Mode.INPUT);           // false = pressed

            // Define and initialize potentiometers
            Potentiometer = hardwareMap.analogInput.get("upper pot");

            // Set power values
            INTEGRAL_GAIN = 1.0;
            HIGH_MAX_POWER = 0.5;
            LOW_MAX_POWER = 0.2;

            // Set arm constants
            MAX_POSITION = 2.0;
            INITIAL_ANGLE = -10.0;
            POSITION_TO_ANGLE = 90.0;
        } else {
            //LOWER
            // Define and initialize Motors
            LeftMotor = hardwareMap.dcMotor.get("LL");
            RightMotor = hardwareMap.dcMotor.get("LR");
            // reverse those motors
            RightMotor.setDirection(DcMotor.Direction.REVERSE);

            // Define and initialize switch
            Limit = hardwareMap.digitalChannel.get("lower limit");
            Limit.setMode(DigitalChannel.Mode.INPUT);           // false = pressed

            // Define and initialize potentiometers
            Potentiometer = hardwareMap.analogInput.get("lower pot");

            // Set power values
            INTEGRAL_GAIN = 0.0;
            HIGH_MAX_POWER = 0.8;
            LOW_MAX_POWER = 0.1;

            // Set arm constants
            MAX_POSITION = 2.0;
            INITIAL_ANGLE = -5.0;
            POSITION_TO_ANGLE = 90.0;
        }
        // Set all motors to zero power
        LeftMotor.setPower(0);
        RightMotor.setPower(0);
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

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public void moveUp() {
        FinalTarget += 0.01;
        if (FinalTarget > MAX_POSITION) FinalTarget = MAX_POSITION;
        FinalTime = 0.0;
    }

    public void moveDown() {
        FinalTarget -= 0.01;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
        FinalTime = 0.0;
    }

    public void moveHome() {
        moveToPosition(0.0);
    }

    public void moveHome(double time) {
        moveToPosition(0.0, time);
    }

    public void holdCurrentPosition() {
        FinalTarget = CurrentPosition;
        FinalTime = 0.0;
    }

    public void moveToPosition(double target) {
        moveToPosition(target, 0.0);
    }

    public void moveToPosition(double target, double time) {
        FinalTarget = target;
        if (FinalTarget > MAX_POSITION) FinalTarget = MAX_POSITION;
        if (FinalTarget < 0.0) FinalTarget = 0.0;
        FinalTime = time;
    }

    /* Call this method when you want to update the arm control (must be done on a periodic basis */
    public void Update(double offset, boolean active) {
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

        /* incrementally change target value */
        CurrentTime = OurTime.seconds();
        OurTime.reset();
        if (CurrentTime < FinalTime) {
            CurrentTarget = CurrentTarget + (FinalTarget-CurrentTarget)*CurrentTime/FinalTime;
            FinalTime = FinalTime - CurrentTime;
        } else {
            CurrentTarget = FinalTarget;
            FinalTime = 0.0;
        }

        /* determine current position relative to home */
        CurrentPosition = Potentiometer.getVoltage() - HomePosition;

        /* determine relative and absolute arm angles */
        RelativeAngle = POSITION_TO_ANGLE*CurrentPosition + INITIAL_ANGLE;
        Angle = RelativeAngle + offset;

        /*********** control code **********/
        error = CurrentTarget - CurrentPosition;
        if (error > 0.2) error = 0.2;
        if (error < -0.2) error = -0.2;


        /* update integral */
        ErrorSum += error*CurrentTime;
        /* limit integral gain if never homed */
        if (!Homed) {
            if (ErrorSum > 0.5)
                ErrorSum = 0.5;
        }

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
        }
        Power = max_power * 5 * error;

        /* determine integral gain */
        Power += ErrorSum*INTEGRAL_GAIN;

        /* limit power */
        if (Power>1.0) Power = 1.0;
        if (Power<-1.0) Power = -1.0;

        /* prevent negative power when...
            at home position or never homed
        */
//        if (AtHome || !Homed) {
        if (AtHome) {
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

        if (active) {
            RightMotor.setPower(Power);
            LeftMotor.setPower(Power);
        }

        telemetry.addData("Power Error Angle", "%.2f %.2f %5.0f", Power, error, Angle);
    }

    public void setPower(double power) {
        RightMotor.setPower(power);
        LeftMotor.setPower(power);
    }

}
