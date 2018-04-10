package org.firstinspires.ftc.teamcode;
/* version history 1.0
     -1/29/18 created from blue autonomous
            make autonomous moves into methods
            get rid of loop with switch and change to linear steps
 */


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

//naming the teleop thing
@Autonomous(name="Auto Test", group ="Test")
public class AutoTest extends LinearOpMode {
    VuforiaLocalizer vuforia;
    RobotConfig robot = new RobotConfig();
    private ElapsedTime runtime = new ElapsedTime();

    /* VuMark variable */
    RelicRecoveryVuMark vuMark;
    VuforiaTrackable relicTemplate = null;

    /* IMU objects */
    BNO055IMU imu;

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    //speed variables
    double MOVE_SPEED = 0.5;
    double STRAFFE_SPEED = 0.75;
    double ROTATE_SPEED = 0.5;
    double AMPERE_POWER = 0.8;

    //color variables
    int left_blue, left_red, right_blue, right_red;

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        boolean redteam = true;
        boolean do_glyph = false;
        boolean do_jewels = false;
        boolean do_motion = false;

        // Send telemetry message to signify robot waiting;
        telemetry.addLine("Auto Test");    //

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        /* initialize IMU */
        // Send telemetry message to signify robot waiting;
        telemetry.addLine("Init imu");    //
        BNO055IMU.Parameters imu_parameters = new BNO055IMU.Parameters();
        imu_parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu_parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
//        imu_parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
//        imu_parameters.loggingEnabled = true;
        imu_parameters.loggingTag = "IMU";
//        imu_parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(imu_parameters);

        telemetry.addData("Initialize","VuForia");
        telemetry.update();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuforia_parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        vuforia_parameters.vuforiaLicenseKey = "AQepDXf/////AAAAGcvzfI2nd0MHnzIGZ7JtquJk3Yx64l7jwu6XImRkNmBkhjVdVcI47QZ7xQq0PvugAb3+ppJxL4n+pNcnt1+PYpQHVETBEPk5WkofitFuYL8zzXEbs7uLY0dMUepnOiJcLSiVISKWWDyc8BJkKcK3a/KmB2sHaE1Lp2LJ+skW43+pYeqtgJE8o8xStowxPJB0OaSFXXw5dGUurK+ykmPam5oE+t+6hi9o/pO1EOHZFoqKl6tj/wsdu9+3I4lqGMsRutKH6s1rKLfip8s3MdlxqnlRKFmMDFewprELOwm+zpjmrJ1cqdlzzWQ6i/EMOzhcOzrPmH3JiH4CocA/Kcck12IuqvN4l6iAIjntb8b0G8zL";
        vuforia_parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK  ;
        this.vuforia = ClassFactory.createVuforiaLocalizer(vuforia_parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();

        /* Input the team color */
        telemetry.addData("Input: ", "Select Team Color");
        telemetry.update();
        do {
            egamepad1.UpdateEdge();
        } while (!egamepad1.x.pressed && !egamepad1.b.pressed);
        if (egamepad1.x.pressed)
            redteam = false;
        egamepad1.UpdateEdge();

        /* Input options */
        telemetry.addData("Input: ", "Do Glyph?");
        telemetry.update();
        do {
            egamepad1.UpdateEdge();
        } while (!egamepad1.x.pressed && !egamepad1.y.pressed);
        if (egamepad1.y.pressed)
            do_glyph = true;
        egamepad1.UpdateEdge();

        telemetry.addData("Input: ", "Do Jewel?");
        telemetry.update();
        do {
            egamepad1.UpdateEdge();
        } while (!egamepad1.x.pressed && !egamepad1.y.pressed);
        if (egamepad1.y.pressed)
            do_jewels = true;
        egamepad1.UpdateEdge();

        telemetry.addData("Input: ", "Do Motion?");
        telemetry.update();
        do {
            egamepad1.UpdateEdge();
        } while (!egamepad1.x.pressed && !egamepad1.y.pressed);
        if (egamepad1.y.pressed)
            do_motion = true;
        egamepad1.UpdateEdge();

        double voltage = robot.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* Initializes the movement speeds which are scaled based on the starting voltage */
        MOVE_SPEED = 0.5 + ((13.2-voltage)/12);
        STRAFFE_SPEED = 0.75 + ((13.2-voltage)/12);
        ROTATE_SPEED = 0.5 + ((13.2-voltage)/12);

        telemetry.addData("Status", "Initialized");
        if (do_glyph)
            telemetry.addData("Do", "Glyph");
        if (do_jewels)
            telemetry.addData("Do", "Jewels");
        if (redteam)
            telemetry.addData("Team Color", "RED");
        else
            telemetry.addData("Team Color", "BLUE");

        //waits for that giant PLAY button to be pressed on RC
        telemetry.addData(">", "Press Play to start");
        telemetry.update();

        waitForStart();

        telemetry.addData("Go", "...");
        telemetry.update();

        if (do_motion) {
            AutoFindVuMark(1.0);
        }

        if (do_glyph && do_jewels) {
            AutoGlyphGrab(0.0);
            AutoDelaySec(1.0);
            AutoArmLift(0.4, 0.0);

            AutoFlippersColorEnable(true);
            AutoAmpereExtend(0.0);
            AutoDelaySec(4.0);
            AutoFlippersExtend(0.0);
            AutoFlippersColorRecord();
            AutoDelaySec(4.0);
            AutoAmpereStop(0.0);

            AutoFlippersColorFlick(redteam, 0.0);

            AutoFlippersColorEnable(false);
            AutoAmpereRetract(0.0);
            AutoDelaySec(3.0);
            AutoFlippersRetract(0.0);
            AutoDelaySec(2.0);
            AutoArmHome(0.0);
            AutoDelaySec(3.0);
            AutoAmpereStop(0.0);

        } else if (do_glyph) {
            AutoGlyphGrab(1.0);
            AutoArmLift(0.4, 3.0);
            AutoArmHome(2.0);

        } else if (do_jewels) {

            AutoGlyphGrab(0.0);

            AutoFlippersColorEnable(true);
            AutoAmpereExtend(0.0);
            AutoDelaySec(4.0);
            AutoFlippersExtend(0.0);
            AutoFlippersColorRecord();
            AutoDelaySec(4.0);
            AutoAmpereStop(0.0);

            AutoFlippersColorFlick(redteam, 0.0);

            AutoFlippersColorEnable(false);
            AutoAmpereRetract(0.0);
            AutoDelaySec(3.0);
            AutoFlippersRetract(0.0);
            AutoDelaySec(5.0);
            AutoAmpereStop(0.0);
        }

        if (do_motion) {
            AutoMoveBackward(MOVE_SPEED,0.75);
            AutoRotateAngle(ROTATE_SPEED,-45);
            AutoMoveForward(MOVE_SPEED,1.30);
            AutoRotateAngle(ROTATE_SPEED,45);
            if (vuMark == RelicRecoveryVuMark.LEFT){
                AutoRotateAngle(ROTATE_SPEED,-25);
                AutoMoveForward(MOVE_SPEED,1.5);
            } else if (vuMark == RelicRecoveryVuMark.RIGHT){
                AutoRotateAngle(ROTATE_SPEED,25);
                AutoMoveForward(MOVE_SPEED,1.5);
            } else {
                AutoMoveForward(0.75*MOVE_SPEED,0.75);
            }
            AutoGlyphRelease(0.0);
            AutoMoveBackward(MOVE_SPEED,0.20);
        }
    }

    void AutoFindVuMark(double time_sec) {
        if ( !opModeIsActive() ) return;

        vuMark = RelicRecoveryVuMark.from(relicTemplate);

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            telemetry.addData("Searching","%.1f",timeNow);
            telemetry.update();
            AutoUpdate();
            timeNow = runtime.seconds() - timeStart;
        } while ( (timeNow<time_sec) && opModeIsActive() && (vuMark==RelicRecoveryVuMark.UNKNOWN));

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
        telemetry.update();
    }

    /*!!!! make absolute rotation option (from initial start angle) */
    void AutoRotateAngle(double speed, double target) {
        if ( !opModeIsActive() ) return;

        telemetry.addLine("Rotate Angle");
        telemetry.update();

        double startAngle;
        double turnAngle;
        Orientation angles;

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        startAngle = angles.firstAngle;

        if (target>0) {
            robot.RotateRight(speed);
            do {
                AutoUpdate();
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                turnAngle = -(angles.firstAngle-startAngle);
                if (turnAngle>180) turnAngle -= 360;
                if (turnAngle<-180) turnAngle += 360;
                telemetry.addData("turn/target","%.0f %.0f",turnAngle,target);
                telemetry.update();
            } while ( (turnAngle < target) && opModeIsActive() );
        }
        if (target<0) {
            robot.RotateLeft(speed);
            do {
                AutoUpdate();
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                turnAngle = -(angles.firstAngle-startAngle);
                if (turnAngle>180) turnAngle -= 360;
                if (turnAngle<-180) turnAngle += 360;
                telemetry.addData("turn/target","%.0f %.0f",turnAngle,target);
                telemetry.update();
            } while ( (turnAngle > target) && opModeIsActive() );
        }
        robot.MoveStop();
    }

    void AutoRotateRight(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Rotate Right");
        telemetry.update();
        robot.RotateRight(speed);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoRotateLeft(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Rotate Left");
        telemetry.update();
        robot.RotateLeft(speed);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoMoveForward(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Forward");
        telemetry.update();
        robot.MoveForward(speed);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoMoveBackward(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Backward");
        telemetry.update();
        robot.MoveBackward(speed);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoMoveLeft(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Left");
        telemetry.update();
        robot.MoveLeft(speed);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoMoveRight(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Right");
        telemetry.update();
        robot.MoveRight(speed);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoGlyphRelease(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Glyph Release");
        telemetry.update();
        robot.GGL.setPosition(robot.GRABBER_LEFT[0]);
        robot.GGR.setPosition(robot.GRABBER_RIGHT[0]);
        AutoDelaySec(time_sec);
        robot.MoveStop();
    }

    void AutoGlyphGrab(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Glyph Grab");
        telemetry.update();
        robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
        robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);
        AutoDelaySec(time_sec);
    }

    void AutoArmLift(double position, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Arm Lift");
        telemetry.update();
        robot.UpperArm.MoveToPosition(position);
        AutoDelaySec(time_sec);
    }

    void AutoArmHome(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Arm Home");
        telemetry.update();
        robot.UpperArm.MoveHome();

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            AutoUpdate();
            timeNow = runtime.seconds() - timeStart;
        } while ( (timeNow<time_sec) && opModeIsActive() && (robot.UpperArm.Limit.getState()==true));
    }

    void AutoAmpereExtend(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Ampere Extend");
        telemetry.update();
        robot.AWL.setPower(AMPERE_POWER);
        robot.AWR.setPower(AMPERE_POWER);
        AutoDelaySec(time_sec);
    }

    void AutoAmpereRetract(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Ampere Retract");
        telemetry.update();
        robot.AWL.setPower(-AMPERE_POWER);
        robot.AWR.setPower(-AMPERE_POWER);
        AutoDelaySec(time_sec);
    }

    void AutoAmpereStop(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Ampere Stop");
        telemetry.update();
        robot.AWL.setPower(0.0);
        robot.AWR.setPower(0.0);
        AutoDelaySec(time_sec);
    }

    void AutoFlippersExtend(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Flipper Extend");
        telemetry.update();
        robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[2]);
        robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[2]);
        AutoDelaySec(time_sec);
    }

    void AutoFlippersRetract(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Flipper Retract");
        telemetry.update();
        robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
        robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
        AutoDelaySec(time_sec);
    }

    void AutoFlippersColorEnable(boolean enable) {
        if ( !opModeIsActive() ) return;
        robot.left_ampere.enableLed(enable);
        robot.right_ampere.enableLed(enable);
    }

    void AutoFlippersColorRecord() {
        if ( !opModeIsActive() ) return;
        left_blue = robot.left_ampere.blue();
        right_blue = robot.right_ampere.blue();
        left_red = robot.left_ampere.red();
        right_red = robot.right_ampere.red();
    }

    void AutoFlippersColorFlick(boolean red, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Flipper Flick");

        left_blue = robot.left_ampere.blue() - left_blue;
        left_red = robot.left_ampere.red() - left_red;
        right_blue = robot.right_ampere.blue() - right_blue;
        right_red = robot.right_ampere.red() - right_red;

        if ((left_blue - left_red) >= 3) {
            left_blue = 1;
            left_red = 0;
        } else if ((left_red - left_blue) >= 3) {
            left_blue = 0;
            left_red = 1;
        } else {
            left_blue = 0;
            left_red = 0;
        }
        if ((right_blue - right_red) >= 3) {
            right_blue = 1;
            right_red = 0;
        } else if ((right_red - right_blue) >= 3) {
            right_blue = 0;
            right_red = 1;
        } else {
            right_blue = 0;
            right_red = 0;
        }
        telemetry.update();

        if ( (left_blue>0) && (right_red>0) ) {
            if (red)
                robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[1]);
            else
                robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[1]);
        } else if ( (left_red>0) && (right_blue>0) ) {
            if (red)
                robot.AFR.setPosition(robot.AMPERE_FLICKER_LEFT[1]);
            else
                robot.AFL.setPosition(robot.AMPERE_FLICKER_RIGHT[1]);
        }
        AutoDelaySec(time_sec);
    }

    void AutoDelaySec(double time_sec) {
        if ( !opModeIsActive() ) return;

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            AutoUpdate();
            timeNow = runtime.seconds() - timeStart;
        } while ( (timeNow<time_sec) && opModeIsActive() );
    }

    void AutoUpdate() {
        if ( !opModeIsActive() ) return;

        robot.ArmUpdate(this, true);
        sleep(40);
    }
}
