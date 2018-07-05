package org.firstinspires.ftc.teamcode.SubAssemblyGrabber;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;


//naming the teleop thing
@TeleOp(name = "Grabber Test", group = "Test")
public class GrabberTest extends LinearOpMode {

    /* Sub Assemblies
     */
    GrabberControl Grabber = new GrabberControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    public void displayHelp() {
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Guides");
        telemetry.addLine("    next/prev   bumper/trigger");
        telemetry.addLine("    +inc/-dec   dpad [L]up/down [R]left/right ");
        telemetry.addLine("  Claw");
        telemetry.addLine("    next/prev   a / y");
        telemetry.addLine("    +inc/-dec   x / b");
        telemetry.addLine("Gamepad 2");
        telemetry.addLine("  Grips");
        telemetry.addLine("    next/prev   bumper/trigger");
        telemetry.addLine("    +inc/-dec   dpad [L]up/down [R]left/right ");
        telemetry.addLine("    setpoints   a, b, x");
    }

    @Override
    public void runOpMode() throws InterruptedException {
        double increment = 0.05;

        telemetry.addLine("Grabber Test");

        /* initialize sub assemblies
         */
        Grabber.initialize(this);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        displayHelp();
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /* Test guides - gamepad1 dpad, bumpers, stick_buttons */
            if (egamepad1.left_bumper.pressed) {
                Grabber.LeftGuideServo.nextSetpoint(true);
            }
            if (egamepad1.left_stick_button.pressed) {
                Grabber.LeftGuideServo.prevSetpoint(true);
            }
            if (egamepad1.right_bumper.pressed) {
                Grabber.RightGuideServo.nextSetpoint(true);
            }
            if (egamepad1.right_stick_button.pressed) {
                Grabber.RightGuideServo.prevSetpoint(true);
            }
            if (egamepad1.dpad_up.pressed) {
                Grabber.LeftGuideServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_down.pressed) {
                Grabber.LeftGuideServo.incrementPosition(-increment);
            }
            if (egamepad1.dpad_left.pressed) {
                Grabber.RightGuideServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_right.pressed) {
                Grabber.RightGuideServo.incrementPosition(-increment);
            }

            /* Test claw - gamepad1 a,b,x,y */
            if (egamepad1.y.pressed) {
                Grabber.ClawServo.prevSetpoint(true);
            }
            if (egamepad1.a.pressed) {
                Grabber.ClawServo.nextSetpoint(true);
            }
            if (egamepad1.x.pressed) {
                Grabber.ClawServo.incrementPosition(increment);
            }
            if (egamepad1.b.pressed) {
                Grabber.ClawServo.incrementPosition(-increment);
            }

            /* Test grips - gamepad2 dpad, bumpers, stick_buttons, a, b, x */
            if (egamepad2.left_bumper.pressed) {
                Grabber.LeftGripServo.nextSetpoint(true);
            }
            if (egamepad2.left_stick_button.pressed) {
                Grabber.LeftGripServo.prevSetpoint(true);
            }
            if (egamepad2.right_bumper.pressed) {
                Grabber.RightGripServo.nextSetpoint(true);
            }
            if (egamepad2.right_stick_button.pressed) {
                Grabber.RightGripServo.prevSetpoint(true);
            }
            if (egamepad2.a.pressed) {
                Grabber.LeftGripServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
                Grabber.RightGripServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
            }
            if (egamepad2.b.pressed) {
                Grabber.LeftGripServo.setSetpoint(GrabberControl.GripSetpoints.PARTIAL);
                Grabber.RightGripServo.setSetpoint(GrabberControl.GripSetpoints.PARTIAL);
            }
            if (egamepad2.x.pressed) {
                Grabber.LeftGripServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
                Grabber.RightGripServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
            }
            if (egamepad2.dpad_up.pressed) {
                Grabber.LeftGripServo.incrementPosition(increment);
            }
            if (egamepad2.dpad_down.pressed) {
                Grabber.LeftGripServo.incrementPosition(-increment);
            }
            if (egamepad2.dpad_left.pressed) {
                Grabber.RightGripServo.incrementPosition(increment);
            }
            if (egamepad2.dpad_right.pressed) {
                Grabber.RightGripServo.incrementPosition(-increment);
            }

            /* display information */
            if (egamepad1.guide.state) {
                displayHelp();
            } else {
                telemetry.addLine("Grips (left/right)");
                telemetry.addData("  positions", "%.1f %.1f", Grabber.LeftGripServo.getPosition(), Grabber.RightGripServo.getPosition());
                telemetry.addData("  setpoints", "%s %s", Grabber.LeftGripServo.getSetpoint(), Grabber.RightGripServo.getSetpoint());
                telemetry.addLine("Claw");
                telemetry.addData("  positions", "%.1f", Grabber.ClawServo.getPosition());
                telemetry.addData("  setpoints", "%s", Grabber.ClawServo.getSetpoint());
                telemetry.addLine("Guides (left/right)");
                telemetry.addData("  positions", "%.1f %.1f", Grabber.LeftGuideServo.getPosition(), Grabber.RightGuideServo.getPosition());
                telemetry.addData("  setpoints", "%s %s", Grabber.LeftGuideServo.getSetpoint(), Grabber.RightGuideServo.getSetpoint());
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }

        /* Clean up sub-assemblies */
        Grabber.cleanup();
        telemetry.update();
    }
}
