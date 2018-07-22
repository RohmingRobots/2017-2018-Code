package org.firstinspires.ftc.teamcode.SubAssemblyGrabber;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utilities.GamepadWrapper;


//naming the teleop thing
@TeleOp(name = "Grabber Test", group = "Test")
public class GrabberTest extends LinearOpMode {

    /* displays information to user about gamepad usage */
    public void displayHelp() {
        telemetry.addLine("Press START for this help");
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Grips");
        telemetry.addLine("    next/prev   bumper/trigger");
        telemetry.addLine("    +inc/-dec   dpad [L]up/down [R]left/right ");
        telemetry.addLine("  Claw");
        telemetry.addLine("    next/prev   a / y");
        telemetry.addLine("    +inc/-dec   x / b");
    }

    @Override
    public void runOpMode() throws InterruptedException {
        double increment = 0.05;

        telemetry.addLine("Grabber Test");

        /* initialize sub assemblies
         */
        GrabberControl Grabber = new GrabberControl(this, false);

        /* Instantiate extended gamepad */
        GamepadWrapper egamepad1 = new GamepadWrapper(gamepad1);
        GamepadWrapper egamepad2 = new GamepadWrapper(gamepad2);

        displayHelp();
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /* Test claw */
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

            /* Test grips */
            if (egamepad1.left_bumper.pressed) {
                Grabber.LeftServo.nextSetpoint(true);
            }
            if (egamepad1.left_trigger.pressed) {
                Grabber.LeftServo.prevSetpoint(true);
            }
            if (egamepad1.right_bumper.pressed) {
                Grabber.RightServo.nextSetpoint(true);
            }
            if (egamepad1.right_trigger.pressed) {
                Grabber.RightServo.prevSetpoint(true);
            }
            if (egamepad1.dpad_up.pressed) {
                Grabber.LeftServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_down.pressed) {
                Grabber.LeftServo.incrementPosition(-increment);
            }
            if (egamepad1.dpad_left.pressed) {
                Grabber.RightServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_right.pressed) {
                Grabber.RightServo.incrementPosition(-increment);
            }

            /* display information */
            if (egamepad1.start.state) {
                displayHelp();
            } else {
                telemetry.addLine("Grips (left/right)");
                telemetry.addData("  positions", "%.3f %.3f", Grabber.LeftServo.getPosition(), Grabber.RightServo.getPosition());
                telemetry.addData("  setpoints", "%s %s", Grabber.LeftServo.getSetpoint(), Grabber.RightServo.getSetpoint());
                telemetry.addLine("Claw");
                telemetry.addData("  positions", "%.3f", Grabber.ClawServo.getPosition());
                telemetry.addData("  setpoints", "%s", Grabber.ClawServo.getSetpoint());
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
