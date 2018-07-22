package org.firstinspires.ftc.teamcode.SubAssemblyAmpere;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utilities.GamepadWrapper;


//naming the teleop thing
@TeleOp(name = "Ampere Test", group = "Test")
public class AmpereTest extends LinearOpMode {

    /* displays information to user about gamepad usage */
    public void displayHelp() {
        telemetry.addLine("Press START for this help");
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Flippers");
        telemetry.addLine("    next/prev   bumper/trigger");
        telemetry.addLine("    +inc/-dec   dpad [L]up/down [R]left/right ");
        telemetry.addLine("  Arms (both)");
        telemetry.addLine("    extend/retract   y / a");
    }

    @Override
    public void runOpMode() throws InterruptedException {
        double AMPERE_POWER = 0.8;
        double increment = 0.05;

        telemetry.addLine("Ampere Test");

        /* initialize sub assemblies
         */
        AmpereControl Ampere = new AmpereControl(this, false);

        //turns ampere LEDs om
        Ampere.ColorLeft.enableLed(true);
        Ampere.ColorRight.enableLed(true);

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

            /* Test side arms */
            if (egamepad1.y.pressed) {          // extend
                Ampere.moveWinches(AMPERE_POWER);
            } else if (egamepad1.y.released) {  // stop
                Ampere.moveWinches(0.0);
            }
            if (egamepad1.a.pressed) {          // retract
                Ampere.moveWinches(-AMPERE_POWER);
            } else if (egamepad1.a.released) {  // stop
                Ampere.moveWinches(0.0);
            }

            /* Test flippers */
            if (egamepad1.left_bumper.pressed) {
                Ampere.LeftFlipperServo.nextSetpoint(true);
            }
            if (egamepad1.left_trigger.pressed) {
                Ampere.LeftFlipperServo.prevSetpoint(true);
            }
            if (egamepad1.right_bumper.pressed) {
                Ampere.RightFlipperServo.nextSetpoint(true);
            }
            if (egamepad1.right_trigger.pressed) {
                Ampere.RightFlipperServo.prevSetpoint(true);
            }
            if (egamepad1.dpad_up.pressed) {
                Ampere.LeftFlipperServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_down.pressed) {
                Ampere.LeftFlipperServo.incrementPosition(-increment);
            }
            if (egamepad1.dpad_left.pressed) {
                Ampere.RightFlipperServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_right.pressed) {
                Ampere.RightFlipperServo.incrementPosition(-increment);
            }

            /* display information */
            if (egamepad1.start.state) {
                displayHelp();
            } else {
                telemetry.addLine("Flippers (left/right)");
                telemetry.addData("  positions", "%.3f %.3f", Ampere.LeftFlipperServo.getPosition(), Ampere.RightFlipperServo.getPosition());
                telemetry.addData("  setpoints", "%s %s", Ampere.LeftFlipperServo.getSetpoint(), Ampere.RightFlipperServo.getSetpoint());
                telemetry.addLine("Color Sensors (left/right)");
                telemetry.addData("  red ", "%3d %3d", Ampere.ColorLeft.red(), Ampere.ColorRight.red());
                telemetry.addData("  blue", "%3d %3d", Ampere.ColorLeft.blue(), Ampere.ColorRight.blue());
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
