package org.firstinspires.ftc.teamcode.SubAssemblyAmpere;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;


//naming the teleop thing
@TeleOp(name = "Ampere Test", group = "Test")
public class AmpereTest extends LinearOpMode {

    /* Sub Assemblies
     */
    AmpereControl Ampere = new AmpereControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    //ampere color sensor variables
    int leftamperered = 0;
    int leftampereblue = 0;
    int rightamperered = 0;
    int rightampereblue = 0;
    boolean leftampere = false;
    boolean rightampere = false;

    public void displayHelp() {
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Flippers");
        telemetry.addLine("    next/prev   bumper/trigger");
        telemetry.addLine("    +inc/-dec   dpad [L]up/down [R]left/right ");
        telemetry.addLine("  Arms (both)");
        telemetry.addLine("    extend/retract   y / a");
    }

    @Override
    public void runOpMode() throws InterruptedException {
        ElapsedTime OurTime = new ElapsedTime();
        double AMPERE_POWER = 0.8;
        double increment = 0.5;

        telemetry.addLine("Ampere Test");

        /* initialize sub assemblies
         */
        Ampere.initialize(this);

        //turns ampere LEDs om
        Ampere.ColorLeft.enableLed(true);
        Ampere.ColorRight.enableLed(true);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        displayHelp();
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        OurTime.reset();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /* Test side arms */
            if (egamepad1.y.pressed) {
                // extend
                Ampere.moveWinches(AMPERE_POWER);
            } else if (egamepad1.y.released) {
                // stop
                Ampere.moveWinches(0.0);
            }
            if (egamepad1.a.pressed) {
                // retract
                Ampere.moveWinches(-AMPERE_POWER);
            } else if (egamepad1.a.released) {
                // stop
                Ampere.moveWinches(0.0);
            }

            /* Test flippers - gamepad1 dpad, bumpers, stick_buttons */
            if (egamepad1.left_bumper.pressed) {
                Ampere.LeftFlipperServo.nextSetpoint(true);
            }
            if (egamepad1.left_stick_button.pressed) {
                Ampere.LeftFlipperServo.prevSetpoint(true);
            }
            if (egamepad1.right_bumper.pressed) {
                Ampere.RightFlipperServo.nextSetpoint(true);
            }
            if (egamepad1.right_stick_button.pressed) {
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
            if (egamepad1.guide.state) {
                displayHelp();
            } else {
                telemetry.addLine("Flippers (left/right)");
                telemetry.addData("  positions", "%.1f %.1f", Ampere.LeftFlipperServo.getPosition(), Ampere.RightFlipperServo.getPosition());
                telemetry.addData("  setpoints", "%s %s", Ampere.LeftFlipperServo.getSetpoint(), Ampere.RightFlipperServo.getSetpoint());
                telemetry.addLine("Color Sensors (red/blue)");
                telemetry.addData("  ampere left ", "%3d %3d", Ampere.ColorLeft.red(), Ampere.ColorLeft.blue());
                telemetry.addData("  ampere right", "%3d %3d", Ampere.ColorRight.red(), Ampere.ColorRight.blue());
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }

        /* Clean up sub-assemblies */
        Ampere.cleanup();
        telemetry.update();
    }
}
