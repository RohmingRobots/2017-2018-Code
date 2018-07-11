package org.firstinspires.ftc.teamcode.SubAssemblyGuides;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubAssemblyGrabber.GrabberControl;
import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;


//naming the teleop thing
@TeleOp(name = "Guide Test", group = "Test")
public class GuideTest extends LinearOpMode {

    /* Sub Assemblies
     */
    GuideControl Guide = new GuideControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    public void displayHelp() {
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Guides");
        telemetry.addLine("    next/prev   bumper/trigger");
        telemetry.addLine("    +inc/-dec   dpad [L]up/down [R]left/right ");
    }

    @Override
    public void runOpMode() throws InterruptedException {
        double increment = 0.05;

        telemetry.addLine("Guide Test");

        /* initialize sub assemblies
         */
        Guide.initialize(this);

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
                Guide.LeftServo.nextSetpoint(true);
            }
            if (egamepad1.left_stick_button.pressed) {
                Guide.LeftServo.prevSetpoint(true);
            }
            if (egamepad1.right_bumper.pressed) {
                Guide.RightServo.nextSetpoint(true);
            }
            if (egamepad1.right_stick_button.pressed) {
                Guide.RightServo.prevSetpoint(true);
            }
            if (egamepad1.dpad_up.pressed) {
                Guide.LeftServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_down.pressed) {
                Guide.LeftServo.incrementPosition(-increment);
            }
            if (egamepad1.dpad_left.pressed) {
                Guide.RightServo.incrementPosition(increment);
            }
            if (egamepad1.dpad_right.pressed) {
                Guide.RightServo.incrementPosition(-increment);
            }

            /* display information */
            if (egamepad1.guide.state) {
                displayHelp();
            } else {
                telemetry.addLine("Guides (left/right)");
                telemetry.addData("  positions", "%.1f %.1f", Guide.LeftServo.getPosition(), Guide.RightServo.getPosition());
                telemetry.addData("  setpoints", "%s %s", Guide.LeftServo.getSetpoint(), Guide.RightServo.getSetpoint());
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }

        /* Clean up sub-assemblies */
        Guide.cleanup();
        telemetry.update();
    }
}
