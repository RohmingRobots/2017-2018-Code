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

    public void chooseModes() {
        //chooses modes based on inputted color and position
        if (FI && redteam)
            modes = modesRAFI;
        if (!FI && redteam)
            modes = modesRABI;
        if (FI && !redteam)
            modes = modesBAFI;
        if (!FI && !redteam)
            modes = modesBABI;
    }

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

    //navigation color sensor variables
    boolean leftcolor = false;
    boolean rightcolor = false;

    //ampere color sensor variables
    int leftamperered = 0;
    int leftampereblue = 0;
    int rightamperered = 0;
    int rightampereblue = 0;
    boolean leftampere;
    boolean rightampere;

    /* IMU objects */
    BNO055IMU imu;
    Orientation angles;

    /* Mode 'stuff' */
    //modes lists which steps and in what order to accomplish them
    //modes is set based on inputted color and position
    int mode = 0;
    int [] modesRAFI = {-1, -11, -12, -13, 1, 20, 30, 40, 5, 6, -21, 7, 8, 100};
    int [] modesRABI = {-1, -11, -12, -13, 1, 20, 31, 41, 5, 6, -21, 7, 8, 100};
    int [] modesBAFI = {-1, -11, -12, -13, 1, 21, 30, 42, 5, 6, -21, 7, 8, 100};
    int [] modesBABI = {-1, -11, -12, -13, 1, 21, 31, 43, 5, 6, -21, 7, 8, 100};
    int [] modes = {};
    /* List of what the mode numbers do so you don't have to hunt them down elsewhere */
    /* except for the jewel scoring and 95-97, the first number is the step number and the second
       number is which version of the step for when it varies based on location
    -20: Grab glyph
    -21: Release glyph
    -1 : Check Vumark
    -11-13 Score jewel
     0 : Wait 1 sec
     1 : Back off balancing stone
     20: Turn left to -45 (red)
     21: Turn right to 45 (blue)
     30: Drive 1.5 diagonal tile (FI)
     31: Drive 1 diagonal tiles (BI)
     40: Turn right to 0
     41: Turn left to -90
     42: Turn left to 0
     43: Turn right to 90
     5 : Triangulate position
     6 : Straffe to column
     7 : Drive into cryptobox
     8 : Back up from cryptobox
     90: Turn to 180 (FI)
     91: Turn left to 140 (RABI)
     92: Turn right to -140 (BABI)
     95: Drive into glyph pit
     96: Drive back to cryptobox
     97: Straffe to column
    100: End
    */

    @Override
    public void runOpMode() throws InterruptedException {

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        double voltage = robot.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* Initializes the movement speeds which are scaled based on the starting voltage */
        MOVE_SPEED = 0.5 + ((13.2-voltage)/12);
        STRAFFE_SPEED = 0.75 + ((13.2-voltage)/12);
        ROTATE_SPEED = 0.4 + ((13.2-voltage)/12);

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
        chooseModes();
        displaySelections();

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
            telemetry.update();

            /* sets the requirements for the left color sensor to be seeing the line it is looking
               for and keeps it up to date */
            if (robot.left_color.red() > 14 && redteam) {
                leftcolor = true;
            }
            else if (robot.left_color.blue() > 14 && !redteam) {
                leftcolor = true;
            }
            else {
                leftcolor = false;
            }

            /* sets the requirements for the right color sensor ...(same as above) */
            if (robot.right_color.red() > 12 && redteam) {
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

                /* grab glyph */
                case -20:
                    //closes grabbers
                    robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
                    robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);
                    if (now > 0.5) {
                        mode++;
                        resetClock();
                    }
                    break;

                /* release glyph */
                case -21:
                    //opens grabbers
                    robot.GGL.setPosition(robot.GRABBER_LEFT[0]);
                    robot.GGR.setPosition(robot.GRABBER_RIGHT[0]);
                    if (now > 0.5) {
                        mode++;
                        resetClock();
                    }
                    break;

                    /* wait for vuMark detection */
                case -1:
                    //closes grabbers
                    robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
                    robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);

                    /* VuForia update code */
                    vuMark = RelicRecoveryVuMark.from(relicTemplate);

                    if (vuMark == RelicRecoveryVuMark.UNKNOWN) {
                        telemetry.addData("VuMark", "not visible");
                    }
                    if (vuMark == RelicRecoveryVuMark.LEFT){
                        telemetry.addData("VuMark", "left");
                    }
                    if (vuMark == RelicRecoveryVuMark.RIGHT){
                        telemetry.addData("VuMark", "right");
                    }
                    if (vuMark == RelicRecoveryVuMark.CENTER){
                        telemetry.addData("VuMark", "center");
                    }
                    if (now > 1.0){
                        mode++;
                        resetClock();
                        startAngle = angles.firstAngle;
                        robot.MoveStop();
                    }
                    break;

                /* jewel scoring steps part 1 */
                case -11:
                    //raises arm
                    robot.UpperArm.MoveToPosition(0.4);

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
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* jewel scoring steps part 2 */
                case -12:
                    //stops the winches
                    telemetry.addLine("Stop");
                    robot.AWL.setPower(0.0);
                    robot.AWR.setPower(0.0);

                    /* checks if both color sensors detect a difference in the change of values and
                       returns true if the side is red and the side is blue */
                    if (    (robot.left_ampere.red() - leftamperered) > 30 +
                            (robot.right_ampere.red() - rightamperered) &&
                            (robot.right_ampere.blue() - rightampereblue) > 30 +
                            (robot.left_ampere.blue() - leftampereblue)) {
                        leftampere = true;
                    }
                    if (    (robot.right_ampere.red() - rightamperered) > 30 +
                            (robot.left_ampere.red() - leftamperered) &&
                            (robot.left_ampere.blue() - leftampereblue) > 30 +
                            (robot.right_ampere.blue() - rightampereblue)) {
                        rightampere = true;
                    }

                    //gives it a bit to check
                    if (now > 0.1) {
                        //if both color sensors agree, then it hits the correct one for our color
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
                        //moves on without knocking one of if it isn't certain it saw it properly
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* jewel scoring steps part 3 */
                case -13:
                    //reels back in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //gives time to get past the jewels
                    if (now > 3.0) {
                        //folds in the servos
                        telemetry.addLine("Fold in");
                        robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
                        robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
                    }

                    //gives time for jewel servos to fold in
                    if (now > 5.0) {
                        robot.UpperArm.MoveHome();
                        mode++;
                        resetClock();
                    }
                    break;

                /* wait one second */
                case 0:
                    //turns all LEDs off
                    robot.left_ampere.enableLed(false);
                    robot.right_ampere.enableLed(false);
                    robot.left_color.enableLed(false);
                    robot.right_color.enableLed(false);

                    //waits 1 second
                    if (now > 1.0) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* backup 24 inches */
                case 1:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //backs up for a set time
                    robot.MoveBackward(MOVE_SPEED);
                    if (now > 0.7) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* turn left to -45 (red) */
                case 20:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateLeft(ROTATE_SPEED);
                    if (turnAngle < -40) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* turn right to 45 (blue) */
                case 21:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateRight(ROTATE_SPEED);
                    if (turnAngle > 40) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* move forward 50.9 inches (FI) */
                case 30:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //moves forward for a set time
                    robot.MoveForward(MOVE_SPEED);
                    if (now > 1.25) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* move forward 33.9 inches (BI) */
                case 31:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //moves forward for a set time
                    robot.MoveForward(MOVE_SPEED);
                    if (now > 0.9) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* turn right to 0 (RAFI) */
                case 40:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateRight(ROTATE_SPEED);
                    if (turnAngle > -10) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* turn left to -90 (RABI) */
                case 41:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateLeft(ROTATE_SPEED);
                    if (turnAngle < -80) {
                        mode++;
                        resetClock();
                        robot.RotateRight(ROTATE_SPEED);
                        robot.MoveStop();
                    }
                    break;

                /* turn left to 0 (BAFI) */
                case 42:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateLeft(ROTATE_SPEED);
                    if (turnAngle < 10) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* turn right to 90 (BABI) */
                case 43:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);

                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateRight(ROTATE_SPEED);
                    if (turnAngle > 80) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* tirangulate */
                case 5:
                    //turns the LEDs on
                    robot.left_color.enableLed(true);
                    robot.right_color.enableLed(true);

                    //stops if both see the line
                    if (leftcolor && rightcolor) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }

                    //strafes right at an angle if the left side sees the line
                    else if (leftcolor && !rightcolor) {
                        robot.FL.setPower(-MOVE_SPEED/1.5);
                        robot.BR.setPower(-MOVE_SPEED/1.5);

                        robot.BL.setPower(MOVE_SPEED*1.4);
                        robot.FR.setPower(MOVE_SPEED*1.4);
                    }

                    //strafes left at an angle if the right side sees the line
                    else if (!leftcolor && rightcolor){
                        robot.FL.setPower(MOVE_SPEED*1.4);
                        robot.BR.setPower(MOVE_SPEED*1.4);

                        robot.BL.setPower(-MOVE_SPEED/1.5);
                        robot.FR.setPower(-MOVE_SPEED/1.5);
                    }

                    //drives forward if it doesn't see anything
                    else {
                        robot.MoveForward(MOVE_SPEED/1.8);
                    }
                    break;

                /* straffe to column */
                case 6:
                    if (vuMark == RelicRecoveryVuMark.LEFT){
                        //strafe left until the right sensor gets to the line on the other side
                        robot.MoveLeft(STRAFFE_SPEED);
                        if (now > 2 && rightcolor) {
                            mode++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }
                    else if (vuMark == RelicRecoveryVuMark.RIGHT){
                        //strafe right until the left sensor gets to the line on the other side
                        robot.MoveRight(STRAFFE_SPEED);
                        if (now > 2 && leftcolor) {
                            mode++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }
                    else {
                        //already lined up, so moves on
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* move forward 10 inches into cryptobox */
                case 7:
                    //moves forward for a set time
                    robot.MoveForward(MOVE_SPEED/1.2);
                    if (now > 0.6) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* move backward 5 inches */
                case 8:
                    //backs up for a set time
                    robot.MoveBackward(MOVE_SPEED/1.2);
                    if (now > 0.2) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;

                /* straffe back */
                /*case 85:
                    if (vuMark == RelicRecoveryVuMark.LEFT){
                        //strafe right until the right sensor gets to the line on the other side
                        robot.MoveRight(STRAFFE_SPEED);
                        if (now > 1 && rightcolor) {
                            mode++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }
                    else if (vuMark == RelicRecoveryVuMark.RIGHT){
                        //strafe left until the left sensor gets to the line on the other side
                        robot.MoveLeft(STRAFFE_SPEED);
                        if (now > 1 && leftcolor) {
                            mode++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }
                    else {
                        //already lined up, so moves on
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;*/

                /* turn to 180 (FI) */
                /*case 90:
                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateRight(ROTATE_SPEED);
                    if (turnAngle > 175) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;*/

                /* turn left to 140 (RABI) */
                /*case 91:
                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateRight(ROTATE_SPEED);
                    if (turnAngle > 135) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;*/

                /* turn right to -140 (BABI) */
                /*case 92:
                    //turns until it gets passed 5 degrees short of the target angle
                    robot.RotateLeft(ROTATE_SPEED);
                    if (turnAngle < -135) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;*/

                /* drive into glyph pit */
                /*case 95:
                    //moves forward for a set time
                    robot.MoveForward(MOVE_SPEED);
                    if (now > 1.7) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;*/

                /* drive back to cryptobox */
                /*case 96:
                    //backs up for a set time
                    robot.MoveBackward(MOVE_SPEED);
                    if (now > 1.65) {
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    break;*/

                /* straffe to column */
                /*case 97:
                    if (vuMark == RelicRecoveryVuMark.LEFT){
                        //already lined up with a column not filled by the first glyph
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    else if (vuMark == RelicRecoveryVuMark.RIGHT){
                        //already lined up with a column not filled by the first glyph
                        mode++;
                        resetClock();
                        robot.MoveStop();
                    }
                    else {
                        //strafe left until the right sensor gets to the line on the other side
                        robot.MoveLeft(STRAFFE_SPEED);
                        if (now > 1 && rightcolor) {
                            mode++;
                            resetClock();
                            robot.MoveStop();
                        }
                    }
                    break;*/

            }  // end of switch

            //updates the arms
            robot.LowerArm.Update(this);
            robot.UpperArm.Update(this);

            telemetry.update();
            sleep(40);
        }
    }

    //important thing that makes Vuforia do its job
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}