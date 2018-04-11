package org.firstinspires.ftc.teamcode;

/**
 * Created by Nicholas on 1/17/2018.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
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

import java.lang.annotation.Target;


//naming the teleop thing
@Autonomous(name="AllAuto", group ="Drive")
public class AllAuto extends LinearOpMode {

    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;
    RobotConfig robot = new RobotConfig();
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
            robot.RotateRight(ROTATE_SPEED);
        }
        else if (angle2turn < -15){
            robot.RotateLeft(ROTATE_SPEED);
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
            robot.MoveStop();
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

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        double voltage = robot.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* Initializes the movement speeds which are scaled based on the starting voltage */
        MOVE_SPEED = 0.5 + ((13.2-voltage)/14);
        STRAFFE_SPEED = 0.75 + ((13.2-voltage)/14);
        ROTATE_SPEED = 0.4 + ((13.2-voltage)/14);

        /* Turns off all the color sensor lights */
        robot.left_color.enableLed(false);
        robot.right_color.enableLed(false);
        robot.left_ampere.enableLed(false);
        robot.right_ampere.enableLed(false);

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
            telemetry.addData("left_color red", robot.left_color.red());
            telemetry.addData("left_color blue", robot.left_color.blue());
            telemetry.addData("right_color red", robot.right_color.red());
            telemetry.addData("right_color blue", robot.right_color.blue());
            telemetry.addData("leftcolor", leftcolor);
            telemetry.addData("rightcolor", rightcolor);
            telemetry.addData("mode", mode);
            telemetry.addData("step", step);
            telemetry.update();

            /* sets the requirements for the left color sensor to be seeing the line it is looking
               for and keeps it up to date */
            if (robot.left_color.red() > 13 && redteam) {
                leftcolor = true;
            }
            else if (robot.left_color.blue() > 13 && !redteam) {
                leftcolor = true;
            }
            else {
                leftcolor = false;
            }

            /* sets the requirements for the right color sensor ...(same as above) */
            if (robot.right_color.red() > 15 && redteam) {
                rightcolor = true;
            }
            else if (robot.right_color.blue() > 9 && !redteam) {
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
                    robot.MoveStop();
                    break;

                /* vuMark detection + jewel selection */
                case 1:
                    /* vuMark detection */
                    if (step == 0) {
                        //closes grabbers
                        robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
                        robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);

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
                                    robot.UpperArm.MoveToPosition(0.3);
                                    home_sequence++;
                                }
                                break;
                            case 1:
                                /* wait until arm has gone through gate, then send arm home */
                                if (robot.UpperArm.CurrentPosition > 0.1) {
                                    robot.UpperArm.MoveHome();
                                    //step++;
                                    mode++;
                                    resetClock();
                                    robot.MoveStop();
                                }
                                break;
                        }
                    }

                    /* jewel selection step 1 */
                    if (step == 1) {
                        //turns ampere LEDs om
                        robot.left_ampere.enableLed(true);
                        robot.right_ampere.enableLed(true);

                        //winches out the ampere
                        telemetry.addLine("Extend");
                        robot.AWL.setPower(AMPERE_POWER);
                        robot.AWR.setPower(AMPERE_POWER);

                        //waits 4 seconds
                        if (now > 4.0) {
                            //extends flickers
                            robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[2]);
                            robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[2]);

                            //if it hasn't calibrated yet. this makes sure it only runs this bit once
                            if (leftamperered==0) {
                                //calibrates to light of open air
                                leftamperered = robot.left_ampere.red();
                                leftampereblue = robot.left_ampere.blue();
                                rightamperered = robot.right_ampere.red();
                                rightampereblue = robot.right_ampere.blue();
                            }
                        }

                        //waits till fully extended
                        if (now > 8.0) {
                            //ends the mode
                            step++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }

                    /* jewel selection step 2 */
                    if (step == 2) {
                        //stops the winches
                        telemetry.addLine("Stop");
                        robot.AWL.setPower(0.0);
                        robot.AWR.setPower(0.0);

                        /* checks if both color sensors detect a difference in the change of values and
                           returns true if the side is red and the side is blue */
                        if (    (robot.left_ampere.red() - leftamperered) > 10 +
                                (robot.right_ampere.red() - rightamperered) &&
                                (robot.right_ampere.blue() - rightampereblue) > 10 +
                                        (robot.left_ampere.blue() - leftampereblue)) {
                            leftampere = true;
                        }
                        if (    (robot.right_ampere.red() - rightamperered) > 10 +
                                (robot.left_ampere.red() - leftamperered) &&
                                (robot.left_ampere.blue() - leftampereblue) > 10 +
                                        (robot.right_ampere.blue() - rightampereblue)) {
                            rightampere = true;
                        }

                        //gives it a bit to check
                        if (now > 0.1) {
                        /* if both color sensors agree, the one that is true will be red. it hits the
                           correct one for our color. if both didn't agree, they will both be false,
                           so it will move on without scoring the wrong jewel */
                            if (leftampere) {
                                if (redteam) {
                                    robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
                                } else {
                                    robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
                                }
                            } else if (rightampere) {
                                if (redteam) {
                                    robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
                                } else {
                                    robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
                                }
                            }

                            //turns LEDs of
                            robot.left_ampere.enableLed(false);
                            robot.right_ampere.enableLed(false);

                            //moves on without knocking one of if it isn't certain it saw it properly
                            step++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }

                    /* jewel selection step 3 */
                    if (step == 3) {
                        //reels back in the amperes
                        telemetry.addLine("Retract");
                        robot.AWL.setPower(-AMPERE_POWER);
                        robot.AWR.setPower(-AMPERE_POWER);

                        //gives time to get past the jewels
                        if (now > 2.4) {
                            //folds in the servos
                            telemetry.addLine("Fold in");
                            robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
                            robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
                        }

                        //gives time for jewel servos to fold in
                        if (now > 6.0) {
                            robot.UpperArm.MoveHome();
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
                    robot.MoveBackward(MOVE_SPEED);
                    if (now > 0.6) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
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
                    if (now < 0.4 && redteam){
                        robot.MoveForward(MOVE_SPEED * 0.75);
                    }
                    else if (now > 0.5) {
                        if (redteam){
                            robot.FR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle + 90)/200);
                            robot.FL.setPower(STRAFFE_SPEED * 1.05 - (turnAngle + 90)/200);
                            robot.BL.setPower(-STRAFFE_SPEED * 0.95 - (turnAngle + 90)/200);
                            robot.BR.setPower(STRAFFE_SPEED * 0.95 + (turnAngle + 90)/200);
                        }
                        else {
                            robot.FR.setPower(STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                            robot.FL.setPower(-STRAFFE_SPEED * 1.15 - (turnAngle - 90)/200);
                            robot.BL.setPower(STRAFFE_SPEED * 0.95 - (turnAngle - 90)/200);
                            robot.BR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                        }
                    }
                    else {
                        robot.MoveStop();
                    }

                    //strafes for a set time
                    if (now > 1.8) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
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
                        robot.MoveForward(MOVE_SPEED);
                    }
                    else /*if (Math.abs(Math.abs(turnAngle) - 90) < 5)*/ {
                        robot.MoveForward(MOVE_SPEED * 0.5);
                    }
                    /*else {
                        if (now < 0.5) {
                            if (redteam) {
                                robot.MoveLeft(STRAFFE_SPEED);
                            }
                            else {
                                robot.MoveRight(STRAFFE_SPEED);
                            }
                        }
                        else if(now > 0.5) {
                            if (!redteam && turnAngle - 90 > 5) {
                                robot.RotateLeft(ROTATE_SPEED * ((turnAngle - 20)/90));
                            }
                            else if (!redteam && turnAngle - 90 < -5) {
                                robot.RotateRight(ROTATE_SPEED * ((160 - turnAngle)/90));
                            }
                            else if (redteam && turnAngle + 90 > 5) {
                                robot.RotateLeft(ROTATE_SPEED * ((160 + turnAngle)/90));
                            }
                            else if (redteam && turnAngle + 90 < -5) {
                                robot.RotateRight(ROTATE_SPEED * ((-20 - turnAngle)/90));
                            }
                        }
                    }*/

                    if ((FI && now > 1.1) || (!FI && (leftcolor || rightcolor))) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                        step = 0;
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
                        robot.MoveStop();
                        step = 0;
                    }
                    break;

                /* tirangulate 'n strafe (FI) or strafe to column (BI) */
                case 7:
                    //turns the LEDs on
                    robot.left_color.enableLed(true);
                    robot.right_color.enableLed(true);

                    /* triangulate 'n strafe (FI) */
                    if (FI) {
                        if (step == -1) {
                            robot.MoveBackward(MOVE_SPEED);
                            if (now > 0.3) {
                                step++;
                                resetClock();
                                robot.MoveStop();
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
                                robot.MoveStop();
                            }

                            //strafes right at an angle if the left side sees the line
                            else if (leftcolor && !rightcolor) {
                                robot.FL.setPower(-MOVE_SPEED);
                                robot.BR.setPower(-MOVE_SPEED);

                                robot.BL.setPower(MOVE_SPEED * 1.4);
                                robot.FR.setPower(MOVE_SPEED * 1.4);
                            }

                            //strafes left at an angle if the right side sees the line
                            else if (!leftcolor && rightcolor) {
                                robot.FL.setPower(MOVE_SPEED * 1.4);
                                robot.BR.setPower(MOVE_SPEED * 1.4);

                                robot.BL.setPower(-MOVE_SPEED);
                                robot.FR.setPower(-MOVE_SPEED);
                            }

                            //drives forward if it doesn't see anything
                            else {
                                robot.MoveForward(MOVE_SPEED * 0.5);
                            }
                        }
                        else{
                            /* 'n strafe */
                            if (vuMark == RelicRecoveryVuMark.LEFT) {
                                //strafe left until the right sensor gets to the line on the other side
                                robot.MoveLeft(STRAFFE_SPEED);
                                if (step == 1 && !rightcolor) {
                                    step++;
                                    resetClock();
                                }
                                if (step == 2 && rightcolor){
                                    //lined up, so moves on
                                    mode++;
                                    resetClock();
                                    robot.MoveStop();
                                    step = 0;
                                }
                            }
                            else if (vuMark == RelicRecoveryVuMark.RIGHT){
                                //strafe right until the left sensor gets to the line on the other side
                                robot.MoveRight(STRAFFE_SPEED);
                                if (step == 1 && !leftcolor) {
                                    step++;
                                    resetClock();
                                }
                                if (step == 2 && leftcolor){
                                    //lined up, so moves on
                                    mode++;
                                    resetClock();
                                    robot.MoveStop();
                                    step = 0;
                                }
                            }
                            else {
                                //already lined up, so moves on
                                mode++;
                                resetClock();
                                robot.MoveStop();
                                step = 0;
                            }
                        }
                    }

                    /* strafe right to column (RABI) */
                    else if (redteam){
                        if (step == 0 && (vuMark == RelicRecoveryVuMark.CENTER || vuMark == RelicRecoveryVuMark.RIGHT)) {
                            //strafe right until the left sensor gets to the line on the other side
                            robot.MoveRight(STRAFFE_SPEED);
                            if (leftcolor) {
                                step++;
                                resetClock();
                                robot.MoveStop();
                            }
                        }
                        else if (step == 1 && vuMark == RelicRecoveryVuMark.RIGHT){
                            robot.MoveRight(STRAFFE_SPEED);
                            if (!leftcolor) {
                                step++;
                                resetClock();
                                robot.MoveStop();
                            }
                        }
                        else if (step == 2){
                            robot.MoveRight(STRAFFE_SPEED);
                            if (leftcolor) {
                                step++;
                                resetClock();
                                robot.MoveStop();
                            }
                        }
                        else {
                            //lined up, so moves on
                            mode++;
                            resetClock();
                            robot.MoveStop();
                            step = 0;
                        }
                    }

                    /* strafe left till column (BABI) */
                    else {
                        if (step == 0 && (vuMark == RelicRecoveryVuMark.CENTER || vuMark == RelicRecoveryVuMark.RIGHT)) {
                            //strafe left until the right sensor gets to the line on the other side
                            robot.MoveLeft(STRAFFE_SPEED);
                            if (rightcolor) {
                                step++;
                                resetClock();
                                robot.MoveStop();
                            }
                        }
                        else if (step == 1 && vuMark == RelicRecoveryVuMark.RIGHT){
                            robot.MoveLeft(STRAFFE_SPEED);
                            if (!rightcolor) {
                                step++;
                                resetClock();
                                robot.MoveStop();
                            }
                        }
                        else if (step == 2){
                            robot.MoveLeft(STRAFFE_SPEED);
                            if (rightcolor) {
                                step++;
                                resetClock();
                                robot.MoveStop();
                            }
                        }
                        else {
                            //lined up, so moves on
                            mode++;
                            resetClock();
                            robot.MoveStop();
                            step = 0;
                        }
                    }
                    break;

                /* release glyph and move forward 10 inches into cryptobox */
                case 8:
                    //turns LEDs off
                    robot.left_color.enableLed(false);
                    robot.right_color.enableLed(false);

                    //opens grabbers
                    robot.GGL.setPosition(robot.GRABBER_LEFT[0]);
                    robot.GGR.setPosition(robot.GRABBER_RIGHT[0]);

                    //moves forward for a set time
                    robot.MoveForward(MOVE_SPEED * 0.8);
                    if (now > 0.4) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                        step = 0;
                    }
                    break;

                /* move backward 5 inches */
                case 9:
                    //backs up for a set time
                    robot.MoveBackward(MOVE_SPEED * 0.8);
                    if (now > 0.15) {
                        robot.MoveStop();
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
            robot.LowerArm.Update(this);
            robot.UpperArm.Update(this);

//            telemetry.update();
            sleep(40);
        }
    }

    //important thing that makes Vuforia do its job
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}