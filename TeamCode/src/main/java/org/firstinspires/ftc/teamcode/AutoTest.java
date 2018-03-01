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

    /* IMU objects */
    BNO055IMU imu;

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        boolean redteam = true;
        boolean FI = false;

        // Send telemetry message to signify robot waiting;
        telemetry.addLine("Auto Test");    //

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

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

        /* Input the team color */
        telemetry.addData("Input: ", "Select Team Color");
        telemetry.update();
        while (!gamepad1.x && !gamepad1.b) { }
        if (gamepad1.x)
            redteam = false;

        /* Input the position */
        telemetry.addData("Input: ", "Select Position");
        telemetry.update();
        while (!gamepad1.dpad_left && !gamepad1.dpad_right) { }
        if ((gamepad1.dpad_left && redteam) || (gamepad1.dpad_right && !redteam))
            FI = true;

        /* check voltage level */
        VoltageSensor vs = hardwareMap.voltageSensor.get("Lower hub 2");
        double voltage = vs.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* adjust speeds based on voltage level */
        final double MOVE_SPEED = 0.5 + ((13.2-voltage)/8);
        final double STRAFFE_SPEED = 0.75 + ((13.2-voltage)/8);
        final double ROTATE_SPEED = 0.4 + ((13.2-voltage)/8);

        telemetry.addData("Status", "Initialized");
        if (FI)
            telemetry.addData("Position", "FI");
        else
            telemetry.addData("Position", "BI");
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

        /* Common initial moves */
        AutoFindVuMark(5.0);
        AutoGlyphGrab(1.0);                 AutoDelaySec(1.0);
        AutoArmLift(2.0);                   AutoDelaySec(1.0);
        AutoArmHome(2.0);                   AutoDelaySec(1.0);

        /* back off stone */
        AutoMoveBackward(MOVE_SPEED,0.92);   AutoDelaySec(1.0);

        if (redteam){
            if (FI) {
                AutoRotateAngle(ROTATE_SPEED,-45);     AutoDelaySec(1.0);
                // Drive 1.5 diagonal tile
                AutoMoveForward(MOVE_SPEED,1.46);    AutoDelaySec(1.0);
                // Turn to column
                if (vuMark == RelicRecoveryVuMark.LEFT){
                    AutoRotateAngle(ROTATE_SPEED,-25);
                }
                if (vuMark == RelicRecoveryVuMark.RIGHT){
                    AutoRotateAngle(ROTATE_SPEED,25);
                }
            } else {
                AutoRotateAngle(ROTATE_SPEED,-45);     AutoDelaySec(1.0);
                // Drive 1 diagonal tiles
                AutoMoveForward(MOVE_SPEED,1.12);    AutoDelaySec(1.0);
                AutoRotateAngle(ROTATE_SPEED,-90);     AutoDelaySec(1.0);
                // Turn to column
                if (vuMark == RelicRecoveryVuMark.LEFT){
                    AutoRotateAngle(ROTATE_SPEED,-115);
                }
                if (vuMark == RelicRecoveryVuMark.RIGHT){
                    AutoRotateAngle(ROTATE_SPEED,-65);
                }
            }
        } else {
            if (FI) {
                AutoRotateAngle(ROTATE_SPEED,45);     AutoDelaySec(1.0);
                // Drive 1.5 diagonal tile
                AutoMoveForward(MOVE_SPEED,1.46);    AutoDelaySec(1.0);
                // Turn to column
                if (vuMark == RelicRecoveryVuMark.LEFT){
                    AutoRotateAngle(ROTATE_SPEED,-25);
                }
                if (vuMark == RelicRecoveryVuMark.RIGHT){
                    AutoRotateAngle(ROTATE_SPEED,25);
                }
            } else {
                AutoRotateAngle(ROTATE_SPEED,45);     AutoDelaySec(1.0);
                // Drive 1 diagonal tiles
                AutoMoveForward(MOVE_SPEED,1.12);    AutoDelaySec(1.0);
                AutoRotateAngle(ROTATE_SPEED,90);     AutoDelaySec(1.0);
                // Turn to column
                if (vuMark == RelicRecoveryVuMark.LEFT){
                    AutoRotateAngle(ROTATE_SPEED,-65);
                }
                if (vuMark == RelicRecoveryVuMark.RIGHT){
                    AutoRotateAngle(ROTATE_SPEED,115);
                }
            }
        }

        AutoGlyphRelease(1.0);              AutoDelaySec(1.0);
        // Drive into cryptobox
        if (vuMark == RelicRecoveryVuMark.CENTER){
            AutoMoveForward(MOVE_SPEED,0.35);    AutoDelaySec(1.0);
        }
        else {
            AutoMoveForward(MOVE_SPEED,0.45);    AutoDelaySec(1.0);
        }
        // Back up from cryptobox
        if (vuMark == RelicRecoveryVuMark.CENTER){
            AutoMoveBackward(MOVE_SPEED,0.3);    AutoDelaySec(1.0);
        }
        else {
            AutoMoveBackward(MOVE_SPEED,0.45);    AutoDelaySec(1.0);
        }

        /* line up for autonomous */
        if (redteam){
            if (FI) {
                AutoRotateAngle(ROTATE_SPEED,170);     AutoDelaySec(1.0);
            } else {
                AutoRotateAngle(ROTATE_SPEED,140);     AutoDelaySec(1.0);
            }
        } else {
            if (FI) {
                AutoRotateAngle(ROTATE_SPEED,170);     AutoDelaySec(1.0);
            } else {
                AutoRotateAngle(ROTATE_SPEED,-140);     AutoDelaySec(1.0);
            }
        }

    }

    void AutoFindVuMark(double time_sec) {
        if ( !opModeIsActive() ) return;

        telemetry.addData("Initialize","VuForia");
        telemetry.update();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuforia_parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        vuforia_parameters.vuforiaLicenseKey = "AQepDXf/////AAAAGcvzfI2nd0MHnzIGZ7JtquJk3Yx64l7jwu6XImRkNmBkhjVdVcI47QZ7xQq0PvugAb3+ppJxL4n+pNcnt1+PYpQHVETBEPk5WkofitFuYL8zzXEbs7uLY0dMUepnOiJcLSiVISKWWDyc8BJkKcK3a/KmB2sHaE1Lp2LJ+skW43+pYeqtgJE8o8xStowxPJB0OaSFXXw5dGUurK+ykmPam5oE+t+6hi9o/pO1EOHZFoqKl6tj/wsdu9+3I4lqGMsRutKH6s1rKLfip8s3MdlxqnlRKFmMDFewprELOwm+zpjmrJ1cqdlzzWQ6i/EMOzhcOzrPmH3JiH4CocA/Kcck12IuqvN4l6iAIjntb8b0G8zL";
        vuforia_parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK  ;
        this.vuforia = ClassFactory.createVuforiaLocalizer(vuforia_parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();

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

    void AutoArmLift(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Arm Lift");
        telemetry.update();
        robot.UpperArm.MoveToPosition(0.2);
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

        robot.UpperArm.Update(this);
        sleep(40);
    }
}
