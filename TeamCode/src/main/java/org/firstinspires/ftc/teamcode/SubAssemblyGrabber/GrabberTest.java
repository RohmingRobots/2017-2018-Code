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

        /* Clean up sub-assemblies */
        Grabber.cleanup();
        telemetry.update();
    }
}
