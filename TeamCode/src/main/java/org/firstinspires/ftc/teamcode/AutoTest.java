package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.SubAssemblyAmpere.AmpereControl;
import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;
import org.firstinspires.ftc.teamcode.SubAssemblyGrabber.GrabberControl;
import org.firstinspires.ftc.teamcode.Utilities.ImuControl;
import org.firstinspires.ftc.teamcode.Utilities.UserControl;
import org.firstinspires.ftc.teamcode.Utilities.VuforiaControl;

//naming the teleop thing
@Autonomous(name = "Auto Test", group = "Test")
public class AutoTest extends LinearOpMode {

    /* Sub Assemblies
     */
//AJB    DualArmControl Arms = null;
    GrabberControl Grabber = null;
    AmpereControl Ampere = null;
    DriveControl Drive = null;
    VuforiaControl Vuforia = null;
    ImuControl Imu = null;
    UserControl User = null;

    ElapsedTime runtime = new ElapsedTime();

    /* constant speed values */
    double MOVE_SPEED = 0.5;            /* adjusted by battery level */
    double STRAFFE_SPEED = 0.75;        /* adjusted by battery level */
    double ROTATE_SPEED = 0.5;          /* adjusted by battery level */
    final double AMPERE_POWER = 0.8;

    /* color variables to record background (default) color readings */
    int left_blue, left_red, right_blue, right_red;


    @Override
    public void runOpMode() throws InterruptedException {

        boolean redteam, do_glyph, do_jewels, do_motion;    /* user options */

        telemetry.addLine("Auto Test");

        /* initialize sub assemblies
         */
//AJB        Arms = new DualArmControl(this);
        Grabber = new GrabberControl(this);
        Ampere = new AmpereControl(this);
        Drive = new DriveControl(this);
        Vuforia = new VuforiaControl(this);
        Imu = new ImuControl(this, true);
        User = new UserControl(this);

        /* Input options */
        redteam = User.getRedBlue("Select Team Color");
        do_glyph = User.getYesNo("Do Glyph?");
        do_jewels = User.getYesNo("Do Jewel?");
        do_motion = User.getYesNo("Do Motion?");


        double voltage = Drive.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* Initializes the movement speeds which are scaled based on the starting voltage */
        MOVE_SPEED = 0.5 + ((13.2 - voltage) / 12);
        STRAFFE_SPEED = 0.75 + ((13.2 - voltage) / 12);
        ROTATE_SPEED = 0.5 + ((13.2 - voltage) / 12);

        telemetry.addData("Status", "Initialized");
        if (do_glyph)
            telemetry.addData("Do", "Glyph");
        if (do_jewels)
            telemetry.addData("Do", "Jewels");
        if (do_motion)
            telemetry.addData("Do", "Motion");
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

        do_glyph = false;
        do_jewels = false;
        do_motion = false;

        autoRotateAngle(ROTATE_SPEED, 90.0);
        telemetry.addData("angle", Imu.getRelativeAngle());
        User.getYesNo("Continue...");
        autoRotateAngle(ROTATE_SPEED, -90.0);
        telemetry.addData("angle", Imu.getRelativeAngle());
        User.getYesNo("Continue...");

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
            autoMoveBackward(MOVE_SPEED, 0.75);
            autoRotateAngle(ROTATE_SPEED, -45);
            autoMoveForward(MOVE_SPEED, 1.30);
            autoRotateAngle(ROTATE_SPEED, 45);
            if (Vuforia.getVuMark() == RelicRecoveryVuMark.LEFT) {
                autoRotateAngle(ROTATE_SPEED, -25);
                autoMoveForward(MOVE_SPEED, 1.5);
            } else if (Vuforia.getVuMark() == RelicRecoveryVuMark.RIGHT) {
                autoRotateAngle(ROTATE_SPEED, 25);
                autoMoveForward(MOVE_SPEED, 1.5);
            } else {
                autoMoveForward(0.75 * MOVE_SPEED, 0.75);
            }
            autoGlyphRelease(0.0);
            autoMoveBackward(MOVE_SPEED, 0.20);
        }
    }

    /* IMPORTANT
     * Call either autoDelaySec or autoUpdate inside every auto method
     */

    /* Wait for VuMark to be detected */
    void autoFindVuMark(double time_sec) {
        if (!opModeIsActive()) return;

        Vuforia.findVuMark();

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            telemetry.addData("Searching", "%.1f", timeNow);
            telemetry.update();
            autoUpdate();
            timeNow = runtime.seconds() - timeStart;
        }
        while ((timeNow < time_sec) && opModeIsActive() && (Vuforia.getVuMark() == RelicRecoveryVuMark.UNKNOWN));

        if (Vuforia.getVuMark() == RelicRecoveryVuMark.UNKNOWN) {
            telemetry.addData("VuMark", "not visible");
        }
        if (Vuforia.getVuMark() == RelicRecoveryVuMark.LEFT) {
            telemetry.addData("VuMark", "left");
        }
        if (Vuforia.getVuMark() == RelicRecoveryVuMark.RIGHT) {
            telemetry.addData("VuMark", "right");
        }
        if (Vuforia.getVuMark() == RelicRecoveryVuMark.CENTER) {
            telemetry.addData("VuMark", "center");
        }
        telemetry.update();
    }

    /* CW from TOP is positive direction */
    void autoRotateAngle(double speed, double target) {
        if (!opModeIsActive()) return;

        telemetry.addLine("Rotate Angle");
        telemetry.update();

        double angleError;
        double runspeed = speed;

        Imu.setReferenceAngle();

        if (target > 0) {
            do {
                angleError = target - Imu.getRelativeAngle();
                if (Math.abs(angleError) < 45.0)
                    runspeed = speed*(1.0*Math.abs(angleError)/45.0 + 0.0);
                Drive.rotateRight(runspeed);
                autoUpdate();
            } while ((angleError > 5.0) && opModeIsActive());
        }
        if (target < 0) {
            Drive.rotateLeft(speed);
            do {
                angleError = target - Imu.getRelativeAngle();
                if (Math.abs(angleError) < 45.0)
                    runspeed = speed*(1.0*Math.abs(angleError)/45.0 + 0.0);
                Drive.rotateLeft(runspeed);
                autoUpdate();
            } while ((angleError < -5.0) && opModeIsActive());
        }
        Drive.moveStop();
    }

    /* Rotate right specified amount of time and then stop */
    void autoRotateRight(double speed, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Rotate Right");
        telemetry.update();
        Drive.rotateRight(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Rotate left specified amount of time and then stop */
    void autoRotateLeft(double speed, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Rotate Left");
        telemetry.update();
        Drive.rotateLeft(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Move forward specified amount of time and then stop */
    void autoMoveForward(double speed, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Move Forward");
        telemetry.update();
        Drive.moveForward(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Move backwards specified amount of time and then stop */
    void autoMoveBackward(double speed, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Move Backward");
        telemetry.update();
        Drive.moveBackward(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Strafe left specified amount of time and then stop */
    void autoMoveLeft(double speed, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Move Left");
        telemetry.update();
        Drive.moveLeft(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Strafe right specified amount of time and then stop */
    void autoMoveRight(double speed, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Move Right");
        telemetry.update();
        Drive.moveRight(speed);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Open grips (release) and delay specified amount of time */
    void autoGlyphRelease(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Glyph Release");
        telemetry.update();
        Grabber.LeftServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
        Grabber.RightServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
        autoDelaySec(time_sec);
        Drive.moveStop();
    }

    /* Close grips (grab) and delay specified amount of time */
    void autoGlyphGrab(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Glyph Grab");
        telemetry.update();
        Grabber.LeftServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
        Grabber.RightServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
        autoDelaySec(time_sec);
    }

    /* Raise upper arm to position and delay specified amount of time */
    void autoArmLift(double position, double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Arm Lift");
        telemetry.update();
//AJB        Arms.UpperArm.moveToPosition(position);
        autoDelaySec(time_sec);
    }

    /* Move arms home and wait for them to reach home (or specified maximum amount of time) */
    void autoArmHome(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Arm Home");
        telemetry.update();
//AJB        Arms.UpperArm.moveHome();

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            autoUpdate();
            timeNow = runtime.seconds() - timeStart;
        }
        while ((timeNow < time_sec) && opModeIsActive());
//AJB        while ((timeNow < time_sec) && opModeIsActive() && (Arms.UpperArm.Limit.getState() == true));
    }

    /* Start side arms extending and delay specified amount of time */
    void autoAmpereExtend(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Ampere Extend");
        telemetry.update();
        Ampere.moveWinches(AMPERE_POWER);
        autoDelaySec(time_sec);
    }

    /* Start side arms retracting and delay specified amount of time */
    void autoAmpereRetract(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Ampere Retract");
        telemetry.update();
        Ampere.moveWinches(-AMPERE_POWER);
        autoDelaySec(time_sec);
    }

    /* Stop side arms and delay specified amount of time */
    void autoAmpereStop(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Ampere Stop");
        telemetry.update();
        Ampere.moveWinches(0.0);
        autoDelaySec(time_sec);
    }

    /* Open flippers (extend) and delay specified amount of time */
    void autoFlippersExtend(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Flipper Extend");
        telemetry.update();
        Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.OPEN);
        Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.OPEN);
        autoDelaySec(time_sec);
    }

    /* Close flippers (retract) and delay specified amount of time */
    void autoFlippersRetract(double time_sec) {
        if (!opModeIsActive()) return;
        telemetry.addLine("Flipper Retract");
        telemetry.update();
        Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
        Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
        autoDelaySec(time_sec);
    }

    /* Enable/disable flipper color sensors */
    void autoFlippersColorEnable(boolean enable) {
        if (!opModeIsActive()) return;
        Ampere.ColorLeft.enableLed(enable);
        Ampere.ColorRight.enableLed(enable);
    }

    /* Records colors seen by flippers */
    void autoFlippersColorRecord() {
        if (!opModeIsActive()) return;
        left_blue = Ampere.ColorLeft.blue();
        right_blue = Ampere.ColorRight.blue();
        left_red = Ampere.ColorLeft.red();
        right_red = Ampere.ColorRight.red();
    }

    /* Flick if specified color seen */
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

        if ((left_blue > 0) && (right_red > 0)) {
            if (red)
                Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
            else
                Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
        } else if ((left_red > 0) && (right_blue > 0)) {
            if (red)
                Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
            else
                Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.PARTIAL);
        }
        autoDelaySec(time_sec);
    }

    /* Delay method */
    void autoDelaySec(double time_sec) {
        if (!opModeIsActive()) return;

        double timeStart = 0;
        double timeNow = 0;
        timeStart = runtime.seconds();
        do {
            autoUpdate();
            timeNow = runtime.seconds() - timeStart;
        } while ((timeNow < time_sec) && opModeIsActive());
    }

    /* Call this update function whenever in a loop waiting */
    void autoUpdate() {
        if (!opModeIsActive()) return;

//AJB        Arms.Update();
        sleep(40);
    }
}
