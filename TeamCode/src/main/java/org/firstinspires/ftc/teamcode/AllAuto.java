package org.firstinspires.ftc.teamcode;

/**
 * Created by Nicholas on 1/17/2018.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.SubAssemblyAmpere.AmpereControl;
import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;
import org.firstinspires.ftc.teamcode.SubAssemblyGrabber.GrabberControl;


//naming the teleop thing
@Autonomous(name="AllAuto", group ="Drive")
public class AllAuto extends LinearOpMode {

    /* Sub Assemblies
     */
    DualArmControl Arms = new DualArmControl();
    GrabberControl Grabber = new GrabberControl();
    AmpereControl Ampere = new AmpereControl();
    DriveControl Drive = new DriveControl();

    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;

    private ElapsedTime runtime = new ElapsedTime();

    /* Arrays */
    public boolean inputTeamColor() {
        //input the team color
        telemetry.addData("Input: ", "Select Team Color");
        telemetry.update();

        //waits for x or b to be pressed
        while (!gamepad1.x && !gamepad1.b) {
        }

        //color selection
        if (gamepad1.x)
            return false;
        return true;
    }

    public boolean inputPosition() {
        //input the position
        telemetry.addData("Input: ", "Select Position");
        telemetry.update();

        //waits for left or right dpad to be pressed
        while (!gamepad1.dpad_left && !gamepad1.dpad_right) {
        }

        //position selection
        if ((gamepad1.dpad_left && redteam) || (gamepad1.dpad_right && !redteam))
            return true;
        return false;
    }

    public boolean inputJewels() {
        //ask to do jewels
        telemetry.addLine("Do Jewels:");
        telemetry.addLine("Y for yes, X for no");
        telemetry.update();

        //waits for x or b to be pressed
        while (!gamepad1.x && !gamepad1.y) {
        }

        //color selection
        if (gamepad1.x)
            return false;
        return true;
    }

    //Multiple Modes setter
    // in case I want to modify it more easily for one position
    // public void chooseModes() {
    //  //chooses modes based on inputted color and position
    //  if (FI && redteam)
    //      modes = modesRAFI;
    //  if (!FI && redteam)
    //      modes = modesRABI;
    //  if (FI && !redteam)
    //      modes = modesBAFI;
    //  if (!FI && !redteam)
    //      modes = modesBABI;
    //}

    public void displaySelections() {
        //displays the selected color and position
        telemetry.addData("Version", "Worlds 4-13-2018");
        telemetry.addData("Status", "Initialized");
        if (FI)
            telemetry.addData("Position", "FI");
        else
            telemetry.addData("Position", "BI");
        if (redteam)
            telemetry.addData("Team Color", "RED");
        else
            telemetry.addData("Team Color", "BLUE");
        //says it is ready to start so it adds all the prestart telemetry together
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
    }

    /* turns to the specified angle */
    public void turn2angle(int angle) {
        TargetAngle = angle;
        angle2turn = (TargetAngle - turnAngle);

        if (angle2turn > 180){
            angle2turn -= 360;
        }
        if (angle2turn < -180){
            angle2turn += 360;
        }

        //turns until it gets within a certain distance based on how far it has been turning
        if (angle2turn > 15){
            Drive.RotateRight(ROTATE_SPEED);
            if (now > 5.0) {
                Drive.RotateRight(ROTATE_SPEED * 1.5);
            }
        }
        else if (angle2turn < -15){
            Drive.RotateLeft(ROTATE_SPEED);
            if (now > 5.0) {
                Drive.RotateLeft(ROTATE_SPEED * 1.5);
            }
            else if (now > 5.5) {
                resetClock();
            }
        }
        //at this point, it is within 10 degrees, so it tries to narrow down further
        else {
            /*
            if (step == 1){
                robot.MoveStop();
                step++;
            }

            robot.RotateRight(angle2turn*0.1);
            if (2.5 > Math.abs(angle2turn)){
                mode++;
                resetClock();
                robot.MoveStop();
                step = 0;
            }
            */
            mode++;
            resetClock();
            Drive.MoveStop();
            step = 0;
        }
    }

    public void resetClock() {
        //resets the clock
        lastReset = runtime.seconds();
    }

    /* Variables: declaring all my variables in one place for my sake */

    //start position variables
    boolean FI;
    boolean redteam;
    boolean do_jewels;

    //time based variables
    double lastReset = 0;
    double now = 0;

    //speed variables
    double MOVE_SPEED = 0.5;
    double STRAFFE_SPEED = 0.75;
    double ROTATE_SPEED = 0.5;
    double AMPERE_POWER = 0.8;

    //turning variables
    double startAngle;
    double currentAngle;
    double turnAngle;       //actual angle relative to where we started used for turning
    double angle2turn;
    double TargetAngle = 0;

    //navigation color sensor variables
    int leftcolorblue = 0;
    int rightcolorblue = 0;
    boolean leftcolor = false;
    boolean rightcolor = false;

    //ampere color sensor variables
    int leftamperered = 0;
    int leftampereblue = 0;
    int rightamperered = 0;
    int rightampereblue = 0;
    boolean leftampere = false;
    boolean rightampere = false;

    /* IMU objects */
    BNO055IMU imu;
    Orientation angles;

    /* Mode 'stuff' */
    //modes lists which steps and in what order to accomplish them
    int home_sequence = 0;
    int step = 0;
    int mode = 0;
    int [] modes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 100};
    /* List of what the mode numbers do so you don't have to hunt them down elsewhere
      1: Vumark detection + jewel selection
      2: Back off balancing stone
      3: Turn to -90 (red) or 90 (blue)
      4: Strafe right (red) or left (blue) to align on balancing stone (BI)
      5: Move forward for time (FI) or until color (BI)
      6: Turn to 0 (FI)
      7: Tirangulate 'n strafe (FI) or strafe right (RABI) or left till column (BABI)
      8: Drive into cryptobox
      9: Back up from cryptobox
    100: End
    */
    
    /* Troublshooting and Hotwiring modes
    -2 : Grab glyph
    -1 : Release glyph
     0 : Wait 1 sec
     */
    /* in case I want to modify it more easily for one position */
    //modes is set based on inputted color and position
    //int [] modesRAFI = {};
    //int [] modesBAFI = {};
    //int [] modesRABI = {};
    //int [] modesBABI = {};

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("All Auto");

        /* Initialize sub assemblies
         */
        Arms.Initialize(this);
        Grabber.Initialize(this);
        Ampere.Initialize(this);
        Drive.Initialize(this);

        double voltage = Drive.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* Initializes the movement speeds which are scaled based on the starting voltage */
        MOVE_SPEED = 0.5 + ((13.2-voltage)/14);
        STRAFFE_SPEED = 0.75 + ((13.2-voltage)/14);
        ROTATE_SPEED = 0.4 + ((13.2-voltage)/14);

        /* Turns off all the color sensor lights */
        Drive.ColorLeft.enableLed(false);
        Drive.ColorRight.enableLed(false);
        Ampere.ColorLeft.enableLed(false);
        Ampere.ColorRight.enableLed(false);

        /* initialize IMU */
        // Send telemetry message to signify robot waiting;
        telemetry.addLine("Init imu");    //
        BNO055IMU.Parameters imu_parameters = new BNO055IMU.Parameters();
        imu_parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu_parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu_parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imu_parameters.loggingEnabled = true;
        imu_parameters.loggingTag = "IMU";
        imu_parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(imu_parameters);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        /* initialize Vuforia */
        // Send telemetry message to signify robot waiting;
        telemetry.addLine("Init VuForia");    //
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuforia_parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        vuforia_parameters.vuforiaLicenseKey = "AQepDXf/////AAAAGcvzfI2nd0MHnzIGZ7JtquJk3Yx64l7jwu6XImRkNmBkhjVdVcI47QZ7xQq0PvugAb3+ppJxL4n+pNcnt1+PYpQHVETBEPk5WkofitFuYL8zzXEbs7uLY0dMUepnOiJcLSiVISKWWDyc8BJkKcK3a/KmB2sHaE1Lp2LJ+skW43+pYeqtgJE8o8xStowxPJB0OaSFXXw5dGUurK+ykmPam5oE+t+6hi9o/pO1EOHZFoqKl6tj/wsdu9+3I4lqGMsRutKH6s1rKLfip8s3MdlxqnlRKFmMDFewprELOwm+zpjmrJ1cqdlzzWQ6i/EMOzhcOzrPmH3JiH4CocA/Kcck12IuqvN4l6iAIjntb8b0G8zL";
        vuforia_parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK  ;
        this.vuforia = ClassFactory.createVuforiaLocalizer(vuforia_parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

        /* Runs the arrays to receive the position and color from the drivers, set the variables,
           and tell the drivers what it got set to for confirmation */
        redteam = inputTeamColor();
        FI = inputPosition();
        do_jewels = inputJewels();
        displaySelections();

        /* in case I want to modify it more easily for one position */
        //chooseModes();

        //transitions to and initializes teleop once auto is done
        AutoTransitioner.transitionOnStop(this, "TeleOp");

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //resets the clock and sets the start angle once the 30 seconds begins
        resetClock();
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        startAngle = angles.firstAngle;

        // telling the code to run until you press that giant STOP button on RC
        // include opModeIsActive in all while loops so that STOP button terminates all actions
        while (opModeIsActive() && modes[mode] < 100) {

            /* IMU update code */
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            currentAngle = angles.firstAngle;
            turnAngle = startAngle-currentAngle;

            //keeps the angle in a 360 degree range so the is only one number for each direction
            if (turnAngle > 180)
                turnAngle -= 360;
            if (turnAngle < -180)
                turnAngle += 360;

            //keeps now up to date
            now = runtime.seconds() - lastReset;

            //so if the robot starts acting weird, we can check if is because of the color sensors
            telemetry.addData("left_color red", Drive.ColorLeft.red());
            telemetry.addData("left_color blue", Drive.ColorLeft.blue());
            telemetry.addData("right_color red", Drive.ColorRight.red());
            telemetry.addData("right_color blue", Drive.ColorRight.blue());
            telemetry.addData("leftcolor", leftcolor);
            telemetry.addData("rightcolor", rightcolor);
            telemetry.addData("mode", mode);
            telemetry.addData("step", step);
            telemetry.update();

            /* sets the requirements for the left color sensor to be seeing the line it is looking
               for and keeps it up to date */
            if (Drive.ColorLeft.red() > 13 && redteam) {
                leftcolor = true;
            }
            else if (Drive.ColorLeft.blue() > 2 + leftampereblue && !redteam) {
                leftcolor = true;
            }
            else {
                leftcolor = false;
            }

            /* sets the requirements for the right color sensor ...(same as above) */
            if (Drive.ColorRight.red() > 12 && redteam) {
                rightcolor = true;
            }
            else if (Drive.ColorRight.blue() > 2 + rightampereblue && !redteam) {
                rightcolor = true;
            }
            else {
                rightcolor = false;
            }

            /* the switch containing all the preset modes so it switches between them without a
               giant list of if, else ifs */
            switch (modes[mode]) {

                /* says it is done when it finishes */
                default:
                    telemetry.addLine("All done");
                    Drive.MoveStop();
                    break;

                /* vuMark detection + jewel selection */
                case 1:


                    /* vuMark detection */
                    if (step == 0) {
                        //closes grabbers
                        Grabber.SetPosition(1);

                        if (now > 0.2) {
                            //turns ampere LEDs om
                            Ampere.ColorLeft.enableLed(true);
                            Ampere.ColorRight.enableLed(true);

                            //winches out the ampere
                            telemetry.addLine("Extend");
                            Ampere.Extend(AMPERE_POWER);
                        }

                        /* VuForia update code */
                        vuMark = RelicRecoveryVuMark.from(relicTemplate);

                        if (vuMark == RelicRecoveryVuMark.UNKNOWN) {
                            telemetry.addData("VuMark", "not visible");
                        }
                        if (vuMark == RelicRecoveryVuMark.LEFT) {
                            telemetry.addData("VuMark", "left");
                        }
                        if (vuMark == RelicRecoveryVuMark.RIGHT) {
                            telemetry.addData("VuMark", "right");
                        }
                        if (vuMark == RelicRecoveryVuMark.CENTER) {
                            telemetry.addData("VuMark", "center");
                        }

                        switch (home_sequence) {
                            case 0:
                                /* lift arm up, through gate, after glyph grabbed */
                                if (now > 0.5) {
                                    Arms.UpperArm.MoveToPosition(0.2);
                                    home_sequence++;
                                }
                                break;
                            case 1:
                                /* wait until arm has gone through gate, then send arm home */
                                if (Arms.UpperArm.getCurrentPosition() > 0.1) {
                                    Arms.UpperArm.MoveToPosition(0.1);
                                    Arms.UpperArm.MoveHome(2.0);
                                    if (do_jewels) {
                                        step++;
                                    } else {
                                        mode++;
                                    }
                                    resetClock();
                                    Drive.MoveStop();
                                }
                                break;
                        }
                    }
                    /* jewel selection step 1 */
                    else if (step == 1) {
                        //turns ampere LEDs om
                        Ampere.ColorLeft.enableLed(true);
                        Ampere.ColorRight.enableLed(true);

                        //winches out the ampere
                        telemetry.addLine("Extend");
                        Ampere.Extend(AMPERE_POWER);

                        //waits 4 seconds
                        if (now > 3.0) {
                            //extends flickers
                            Ampere.SetPosition(2);

                            //if it hasn't calibrated yet. this makes sure it only runs this bit once
                            if (leftamperered==0) {
                                //calibrates to light of open air
                                leftamperered = Ampere.ColorLeft.red();
                                leftampereblue = Ampere.ColorLeft.blue();
                                rightamperered = Ampere.ColorRight.red();
                                rightampereblue = Ampere.ColorRight.blue();
                            }
                        }

                        //waits till fully extended
                        if (now > 7.7) {
                            //ends the mode
                            step++;
                            resetClock();
                            Drive.MoveStop();
                        }
                    }
                    /* jewel selection step 2 */
                    else if (step == 2) {
                        //stops the winches
                        telemetry.addLine("Stop");
                        Ampere.Extend(0.0);

                        /* checks if both color sensors detect a difference in the change of values and
                           returns true if the side is red and the side is blue */
                        leftampere = false;
                        rightampere = false;
                        if ( ((Ampere.ColorLeft.red() - leftamperered) > 10 + (Ampere.ColorRight.red() - rightamperered)) &&
                                ((Ampere.ColorRight.blue() - rightampereblue) > 10 + (Ampere.ColorLeft.blue() - leftampereblue)) ) {
                            leftampere = true;
                        }
                        if (  ((Ampere.ColorRight.red() - rightamperered) > 10 + (Ampere.ColorLeft.red() - leftamperered)) &&
                                ((Ampere.ColorLeft.blue() - leftampereblue) > 10 + (Ampere.ColorRight.blue() - rightampereblue)) ) {
                            rightampere = true;
                        }

                        //gives it a bit to check
                        if (now > 0.1) {
                        /* if both color sensors agree, the one that is true will be red. it hits the
                           correct one for our color. if both didn't agree, they will both be false,
                           so it will move on without scoring the wrong jewel */
                            if (leftampere) {
                                if (redteam) {
                                    Ampere.SetPositionRight(0);
                                } else {
                                    Ampere.SetPositionLeft(0);
                                }
                            } else if (rightampere) {
                                if (redteam) {
                                    Ampere.SetPositionLeft(0);
                                } else {
                                    Ampere.SetPositionRight(0);
                                }
                            }

                            //reels back in the amperes
                            telemetry.addLine("Retract");
                            Ampere.Retract(AMPERE_POWER);

                            //turns LEDs of
                            Ampere.ColorLeft.enableLed(false);
                            Ampere.ColorRight.enableLed(false);

                            //moves on without knocking one of if it isn't certain it saw it properly
                            step++;
                            resetClock();
                            Drive.MoveStop();
                        }
                    }
                    /* jewel selection step 3 */
                    else if (step == 3) {
                        //reels back in the amperes
                        telemetry.addLine("Retract");
                        Ampere.Retract(AMPERE_POWER);

                        //gives time to get past the jewels
                        if (now > 2.2) {
                            //folds in the servos
                            telemetry.addLine("Fold in");
                            Ampere.SetPosition(0);
                        }

                        //gives time for jewel servos to fold in
                        if (now > 4.0) {
                            mode++;
                            resetClock();
                            step = 0;
                        }
                    }
                    break;

                /* backup 24 inches */
                case 2:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    //backs up for a set time
                    Drive.MoveBackward(MOVE_SPEED);
                    if (now > 0.58) {
                        mode++;
                        resetClock();
                        Drive.MoveStop();
                        step = 0;
                    }
                    break;

                /* turn to -90 (red) or 90 (blue) */
                case 3:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    if (now > 0.5){
                        //turns to angle
                        if (redteam){
                            turn2angle(-90);
                        }
                        if (!redteam){
                            turn2angle(90);
                        }
                    }
                    break;

                /* strafe right (red) or left (blue) to align on balancing stone (BI) */
                case 4:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    //strafes in the direction of the stone, which depends on team color
                    if (now < 0.46 && redteam){
                        Drive.MoveForward(MOVE_SPEED * 0.75);
                    }
                    else if (now > 0.5) {
                        if (redteam){
                            Drive.MoveRight(STRAFFE_SPEED);
/*                            robot.FR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle + 90)/200);
                            robot.FL.setPower(STRAFFE_SPEED * 1.05 - (turnAngle + 90)/200);
                            robot.BL.setPower(-STRAFFE_SPEED * 0.95 - (turnAngle + 90)/200);
                            robot.BR.setPower(STRAFFE_SPEED * 0.95 + (turnAngle + 90)/200);
                            */
                        }
                        else {
                            Drive.MoveLeft(STRAFFE_SPEED);
/*                            robot.FR.setPower(STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                            robot.FL.setPower(-STRAFFE_SPEED * 1.15 - (turnAngle - 90)/200);
                            robot.BL.setPower(STRAFFE_SPEED * 0.95 - (turnAngle - 90)/200);
                            robot.BR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                            */
                        }
                    }
                    else {
                        Drive.MoveStop();
                    }

                    //strafes for a set time
                    if (now > 1.8) {
                        mode++;
                        resetClock();
                        Drive.MoveStop();
                        step = 0;
                    }
                    break;

                /* move forward 36 inches (FI) or till sees a line (BI) */
                case 5:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    //moves forward for a set time (FI) or till it sees a line (BI)
                    if (FI) {
                        Drive.MoveForward(MOVE_SPEED);
                    }
                    else /*if (Math.abs(Math.abs(turnAngle) - 90) < 5)*/ {
                        if (step == -1) {
                            Drive.MoveBackward(MOVE_SPEED);
                            if (now > 0.25) {
                                step++;
                                resetClock();
                            }
                        }

                        /* triangulate */
                        else if (step == 0) {
                            Drive.MoveForward(MOVE_SPEED * 0.7);
                            if (now > 5) {
                                step--;
                                resetClock();
                            }
                        }
                    }

                    if ((FI && now > 1.05) || (!FI && (leftcolor || rightcolor))) {
                        mode++;
                        resetClock();
                        Drive.MoveStop();
                        step = 0;
                        leftcolorblue = Drive.ColorLeft.blue();
                        rightcolorblue = Drive.ColorRight.blue();
                    }
                    break;

                /* turn to 0 (FI) */
                case 6:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    if (FI){
                        //turns to angle
                        turn2angle(0);
                    }
                    else {
                        mode++;
                        resetClock();
                        Drive.MoveStop();
                        step = 0;
                    }
                    break;

                /* tirangulate 'n strafe (FI) or strafe to column (BI) */
                case 7:
                    //turns the LEDs on
                    Drive.ColorLeft.enableLed(true);
                    Drive.ColorRight.enableLed(true);

                    /* triangulate 'n strafe (FI) */
                    if (FI) {
                        if (step == -1) {
                            Drive.MoveBackward(MOVE_SPEED);
                            if (now > 2.5) {
                                step++;
                                resetClock();
                            }
                        }

                        /* triangulate */
                        else if (step == 0) {
                            if (now > 5) {
                                step --;
                                resetClock();
                            }

                            //stops if both see the line
                            if (leftcolor && rightcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }

                            //strafes right at an angle if the left side sees the line
                            else if (leftcolor && !rightcolor) {
                                Drive.MoveLeft(STRAFFE_SPEED);
/*                                robot.FL.setPower(-MOVE_SPEED);
                                robot.BR.setPower(-MOVE_SPEED);
                                robot.BL.setPower(MOVE_SPEED * 1.4);
                                robot.FR.setPower(MOVE_SPEED * 1.4);
                                */
                            }

                            //strafes left at an angle if the right side sees the line
                            else if (!leftcolor && rightcolor) {
                                Drive.MoveRight(STRAFFE_SPEED);
/*                                robot.FL.setPower(MOVE_SPEED * 1.4);
                                robot.BR.setPower(MOVE_SPEED * 1.4);
                                robot.BL.setPower(-MOVE_SPEED);
                                robot.FR.setPower(-MOVE_SPEED);
                                */
                            }

                            //drives forward if it doesn't see anything
                            else {
                                Drive.MoveForward(MOVE_SPEED * 0.7);
                            }
                        }
                        else{
                            /* 'n strafe */
                            if (vuMark == RelicRecoveryVuMark.LEFT) {
                                //strafe left until the right sensor gets to the line on the other side
                                Drive.MoveLeft(STRAFFE_SPEED);
/*                                robot.FR.setPower(STRAFFE_SPEED * 1.05 + turnAngle/200);
                                robot.FL.setPower(-STRAFFE_SPEED * 1.15 - turnAngle/200);
                                robot.BL.setPower(STRAFFE_SPEED * 0.95 - turnAngle/200);
                                robot.BR.setPower(-STRAFFE_SPEED * 1.05 + turnAngle/200);
                                */
                                if (step == 1 && !rightcolor) {
                                    step++;
                                    resetClock();
                                }
                                if (step == 2 && rightcolor){
                                    //lined up, so moves on
                                    mode++;
                                    resetClock();
                                    Drive.MoveStop();
                                    step = 0;
                                }
                            }
                            else if (vuMark == RelicRecoveryVuMark.RIGHT){
                                //strafe right until the left sensor gets to the line on the other side
                                Drive.MoveRight(STRAFFE_SPEED);
/*                                robot.FR.setPower(-STRAFFE_SPEED * 1.05 + turnAngle/200);
                                robot.FL.setPower(STRAFFE_SPEED * 1.05 - turnAngle/200);
                                robot.BL.setPower(-STRAFFE_SPEED * 0.95 - turnAngle/200);
                                robot.BR.setPower(STRAFFE_SPEED * 0.95 + turnAngle/200);
                                */
                                if (step == 1 && !leftcolor) {
                                    step++;
                                    resetClock();
                                }
                                if (step == 2 && leftcolor){
                                    //lined up, so moves on
                                    mode++;
                                    resetClock();
                                    Drive.MoveStop();
                                    step = 0;
                                }
                            }
                            else {
                                //already lined up, so moves on
                                mode++;
                                resetClock();
                                Drive.MoveStop();
                                step = 0;
                            }
                        }
                    }

                    /* strafe right to column (RABI) */
                    else if (redteam){
                        Drive.MoveRight(STRAFFE_SPEED);
/*                        robot.FR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle + 90)/200);
                        robot.FL.setPower(STRAFFE_SPEED * 1.05 - (turnAngle + 90)/200);
                        robot.BL.setPower(-STRAFFE_SPEED * 0.95 - (turnAngle + 90)/200);
                        robot.BR.setPower(STRAFFE_SPEED * 0.95 + (turnAngle + 90)/200);
                        */
                        if (step == 0 && (vuMark == RelicRecoveryVuMark.CENTER || vuMark == RelicRecoveryVuMark.RIGHT)) {
                            //strafe right until the left sensor gets to the line on the other side
                            if (leftcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }
                        }
                        else if (step == 1 && vuMark == RelicRecoveryVuMark.RIGHT){
                            if (!leftcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }
                        }
                        else if (step == 2){
                            if (leftcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }
                        }
                        else {
                            //lined up, so moves on
                            mode++;
                            resetClock();
                            Drive.MoveStop();
                            step = 0;
                        }
                    }

                    /* strafe left till column (BABI) */
                    else {
                        Drive.MoveLeft(STRAFFE_SPEED);
/*                        robot.FR.setPower(STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                        robot.FL.setPower(-STRAFFE_SPEED * 1.15 - (turnAngle - 90)/200);
                        robot.BL.setPower(STRAFFE_SPEED * 0.95 - (turnAngle - 90)/200);
                        robot.BR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
*/
                        if (step == 0 && (vuMark == RelicRecoveryVuMark.CENTER || vuMark == RelicRecoveryVuMark.LEFT)) {
                            //strafe left until the right sensor gets to the line on the other side
                            if (rightcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }
                        }
                        else if (step == 1 && vuMark == RelicRecoveryVuMark.LEFT){
                            if (!rightcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }
                        }
                        else if (step == 2){
                            if (rightcolor) {
                                step++;
                                resetClock();
                                Drive.MoveStop();
                            }
                        }
                        else {
                            //lined up, so moves on
                            mode++;
                            resetClock();
                            Drive.MoveStop();
                            step = 0;
                        }
                    }
                    break;

                /* release glyph and move forward 10 inches into cryptobox */
                case 8:
                    //turns LEDs off
                    Drive.ColorLeft.enableLed(false);
                    Drive.ColorRight.enableLed(false);

                    //opens grabbers
                    Grabber.SetPosition(0);

                    //moves forward for a set time
                    Drive.MoveForward(MOVE_SPEED * 0.8);
                    if (now > 0.4) {
                        mode++;
                        resetClock();
                        Drive.MoveStop();
                        step = 0;
                    }
                    break;

                /* move backward 5 inches */
                case 9:
                    //backs up for a set time
                    Drive.MoveBackward(MOVE_SPEED * 0.8);
                    if (now > 0.15) {
                        Drive.MoveStop();
                        mode++;
                        resetClock();
                        step = 0;
                    }
                    break;

                    //Unused modes
//                /* grab glyph */
//                case -2:
//                    //closes grabbers
//                    robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
//                    robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);
//                    if (now > 0.5) {
//                        mode++;
//                        resetClock();
//                        step = 0;
//                    }
//                    break;
//
//                /* release glyph */
//                case -1:
//                    //opens grabbers
//                    robot.GGL.setPosition(robot.GRABBER_LEFT[0]);
//                    robot.GGR.setPosition(robot.GRABBER_RIGHT[0]);
//                    if (now > 0.5) {
//                        mode++;
//                        resetClock();
//                        step = 0;
//                    }
//                    break;
//
//                /* wait one second *
//                case 0:
//                    //turns all LEDs off
//                    robot.left_ampere.enableLed(false);
//                    robot.right_ampere.enableLed(false);
//                    robot.left_color.enableLed(false);
//                    robot.right_color.enableLed(false);
//
//                    //waits 1 second
//                    if (now > 1.0) {
//                        mode++;
//                        resetClock();
//                        robot.MoveStop();
//                        step = 0;
//                    }
//                    break;

            }  // end of switch

            //updates the arms
            Arms.Update(true);

//            telemetry.update();
            sleep(40);
        }
    }

    //important thing that makes Vuforia do its job
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}
