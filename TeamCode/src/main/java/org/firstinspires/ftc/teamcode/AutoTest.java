package org.firstinspires.ftc.teamcode;
/* version history 1.0
     -1/29/18 created from blue autonomous
            make autonomous moves into methods
            get rid of loop with switch and change to linear steps
 */


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
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
import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;

//naming the teleop thing
@Autonomous(name="Auto Test", group ="Test")
public class AutoTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DualArmControl Arms = new DualArmControl();
    GrabberControl Grabber = new GrabberControl();
    AmpereControl Ampere = new AmpereControl();
    DriveControl Drive = new DriveControl();

    VuforiaLocalizer vuforia;
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

        telemetry.addLine("Auto Test");

        /* Initialize sub assemblies
         */
        Arms.Initialize(this);
        Grabber.Initialize(this);
        Ampere.Initialize(this);
        Drive.Initialize(this);

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

        double voltage = Drive.Battery.getVoltage();
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
            autoFindVuMark(1.0);
        }

        if (do_glyph && do_jewels) {
            autoGlyphGrab(0.0);
            autoDelaySec(1.0);
            autoArmLift(0.4, 0.0);

            autoFlippersColorEnable(true);
            autoAmpereExtend(0.0);
            autoDelaySec(4.0);
            autoFlippersExtend(0.0);
            autoFlippersColorRecord();
            autoDelaySec(4.0);
            autoAmpereStop(0.0);

            autoFlippersColorFlick(redteam, 0.0);

            autoFlippersColorEnable(false);
            autoAmpereRetract(0.0);
            autoDelaySec(3.0);
            autoFlippersRetract(0.0);
            autoDelaySec(2.0);
            autoArmHome(0.0);
            autoDelaySec(3.0);
            autoAmpereStop(0.0);

        } else if (do_glyph) {
            autoGlyphGrab(1.0);
            autoArmLift(0.4, 3.0);
            autoArmHome(2.0);

        } else if (do_jewels) {

            autoGlyphGrab(0.0);

            autoFlippersColorEnable(true);
            autoAmpereExtend(0.0);
            autoDelaySec(4.0);
            autoFlippersExtend(0.0);
            autoFlippersColorRecord();
            autoDelaySec(4.0);
            autoAmpereStop(0.0);

            autoFlippersColorFlick(redteam, 0.0);

            autoFlippersColorEnable(false);
            autoAmpereRetract(0.0);
            autoDelaySec(3.0);
            autoFlippersRetract(0.0);
            autoDelaySec(5.0);
            autoAmpereStop(0.0);
        }

        if (do_motion) {
            autoMoveBackward(MOVE_SPEED,0.75);
            autoRotateAngle(ROTATE_SPEED,-45);
            autoMoveForward(MOVE_SPEED,1.30);
            autoRotateAngle(ROTATE_SPEED,45);
            if (vuMark == RelicRecoveryVuMark.LEFT){
                autoRotateAngle(ROTATE_SPEED,-25);
                autoMoveForward(MOVE_SPEED,1.5);
            } else if (vuMark == RelicRecoveryVuMark.RIGHT){
                autoRotateAngle(ROTATE_SPEED,25);
                autoMoveForward(MOVE_SPEED,1.5);
            } else {
                autoMoveForward(0.75*MOVE_SPEED,0.75);
            }
            autoGlyphRelease(0.0);
            autoMoveBackward(MOVE_SPEED,0.20);
        }
    }

    void autoFindVuMark(double time_sec) {
        if ( !opModeIsActive() ) return;

        vuMark = RelicRecoveryVuMark.from(relicTemplate);

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            telemetry.addData("Searching","%.1f",timeNow);
            telemetry.update();
            autoUpdate();
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
    void autoRotateAngle(double speed, double target) {
        if ( !opModeIsActive() ) return;

        telemetry.addLine("Rotate Angle");
        telemetry.update();

        double startAngle;
        double turnAngle;
        Orientation angles;

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        startAngle = angles.firstAngle;

        if (target>0) {
            Drive.rotateRight(speed);
            do {
                autoUpdate();
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                turnAngle = -(angles.firstAngle-startAngle);
                if (turnAngle>180) turnAngle -= 360;
                if (turnAngle<-180) turnAngle += 360;
                telemetry.addData("turn/target","%.0f %.0f",turnAngle,target);
                telemetry.update();
            } while ( (turnAngle < target) && opModeIsActive() );
        }
        if (target<0) {
            Drive.rotateLeft(speed);
            do {
                autoUpdate();
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                turnAngle = -(angles.firstAngle-startAngle);
                if (turnAngle>180) turnAngle -= 360;
                if (turnAngle<-180) turnAngle += 360;
                telemetry.addData("turn/target","%.0f %.0f",turnAngle,target);
                telemetry.update();
            } while ( (turnAngle > target) && opModeIsActive() );
        }
        Drive.moveStop();
    }

    void autoRotateRight(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Rotate Right");
        telemetry.update();
        Drive.rotateRight(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoRotateLeft(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Rotate Left");
        telemetry.update();
        Drive.rotateLeft(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoMoveForward(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Forward");
        telemetry.update();
        Drive.moveForward(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoMoveBackward(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Backward");
        telemetry.update();
        Drive.moveBackward(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoMoveLeft(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Left");
        telemetry.update();
        Drive.moveLeft(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoMoveRight(double speed, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Move Right");
        telemetry.update();
        Drive.moveRight(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoGlyphRelease(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Glyph Release");
        telemetry.update();
        Grabber.LeftGripServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
        Grabber.RightGripServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    void autoGlyphGrab(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Glyph Grab");
        telemetry.update();
        Grabber.LeftGripServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
        Grabber.RightGripServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
        autoDelaySec(time_sec);
    }

    void autoArmLift(double position, double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Arm Lift");
        telemetry.update();
        Arms.UpperArm.moveToPosition(position);
        autoDelaySec(time_sec);
    }

    void autoArmHome(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Arm Home");
        telemetry.update();
        Arms.UpperArm.moveHome();

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            autoUpdate();
            timeNow = runtime.seconds() - timeStart;
        } while ( (timeNow<time_sec) && opModeIsActive() && (Arms.UpperArm.Limit.getState()==true));
    }

    void autoAmpereExtend(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Ampere Extend");
        telemetry.update();
        Ampere.moveWinches(AMPERE_POWER);
        autoDelaySec(time_sec);
    }

    void autoAmpereRetract(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Ampere Retract");
        telemetry.update();
        Ampere.moveWinches(-AMPERE_POWER);
        autoDelaySec(time_sec);
    }

    void autoAmpereStop(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Ampere Stop");
        telemetry.update();
        Ampere.moveWinches(0.0);
        autoDelaySec(time_sec);
    }

    void autoFlippersExtend(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Flipper Extend");
        telemetry.update();
        Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.OPEN);
        Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.OPEN);
        autoDelaySec(time_sec);
    }

    void autoFlippersRetract(double time_sec) {
        if ( !opModeIsActive() ) return;
        telemetry.addLine("Flipper Retract");
        telemetry.update();
        Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
        Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
        autoDelaySec(time_sec);
    }

    void autoFlippersColorEnable(boolean enable) {
        if ( !opModeIsActive() ) return;
        Ampere.ColorLeft.enableLed(enable);
        Ampere.ColorRight.enableLed(enable);
    }

    void autoFlippersColorRecord() {
        if ( !opModeIsActive() ) return;
        left_blue = Ampere.ColorLeft.blue();
        right_blue = Ampere.ColorRight.blue();
        left_red = Ampere.ColorLeft.red();
        right_red = Ampere.ColorRight.red();
    }

    void autoFlippersColorFlick(boolean red, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Flipper Flick");

        left_blue = Ampere.ColorLeft.blue() - left_blue;
        left_red = Ampere.ColorLeft.red() - left_red;
        right_blue = Ampere.ColorRight.blue() - right_blue;
        right_red = Ampere.ColorRight.red() - right_red;

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
                Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
            else
                Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
        } else if ( (left_red>0) && (right_blue>0) ) {
            if (red)
                Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
            else
                Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
        }
        autoDelaySec(time_sec);
    }

    void autoDelaySec(double time_sec) {
        if ( !opModeIsActive() ) return;

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            autoUpdate();
            timeNow = runtime.seconds() - timeStart;
        } while ( (timeNow<time_sec) && opModeIsActive() );
    }

    void autoUpdate() {
        if ( !opModeIsActive() ) return;

        Arms.Update(true);
        sleep(40);
    }
}
