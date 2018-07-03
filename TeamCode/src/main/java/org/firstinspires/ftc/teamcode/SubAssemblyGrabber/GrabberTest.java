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
@TeleOp(name="Grabber Test", group="Test")
public class GrabberTest extends LinearOpMode {

    /* Sub Assemblies
     */
    GrabberControl Grabber = new GrabberControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("Grabber Test");
        telemetry.addLine("gamepad 2");
        telemetry.addLine("left bumper - increment left grabber");
        telemetry.addLine("right bumper - increment right grabber");
        telemetry.addLine("b - grabbers partial open");
        telemetry.addLine("x - grabbers closed");
        telemetry.addLine("a - increment claw");
        telemetry.addLine("dpad right - increment guides");

        /* Initialize sub assemblies
         */
        Grabber.Initialize(this);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /* display information */
            telemetry.update();


            /********** Grabber code **********/
            if (egamepad2.left_bumper.pressed) {
                Grabber.IncrementLeft();
            }
            if (egamepad2.right_bumper.pressed) {
                Grabber.IncrementRight();
            }
            if (egamepad2.b.pressed) {
                Grabber.SetPosition(2);
            }
            if (egamepad2.x.pressed) {
                Grabber.SetPosition(1);
            }

            //*******Guides**********/
            if (egamepad2.dpad_right.pressed) {
                Grabber.ToggleGuides();
            }

            if (egamepad2.a.released) {
                Grabber.ToggleClaw();
            }

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
