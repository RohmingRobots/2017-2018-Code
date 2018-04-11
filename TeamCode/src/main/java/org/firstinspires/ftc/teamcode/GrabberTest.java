package org.firstinspires.ftc.teamcode;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.concurrent.TimeUnit;


//naming the teleop thing
@TeleOp(name="Grabber Test", group="Test")
public class GrabberTest extends LinearOpMode {

    RobotConfig robot = new RobotConfig();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {

        int index_grabber_left;
        int index_grabber_right;
        int index_claw;
        int index_guide;
        boolean pressed = false;

        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        index_grabber_left = 0;
        index_grabber_right = 0;
        index_claw = 0;

        index_guide = 0;


        telemetry.addLine("Grabber Test");
        telemetry.addLine("gamepad 2");
        telemetry.addLine("left bumper - increment left grabber");
        telemetry.addLine("right bumper - increment right grabber");
        telemetry.addLine("b - grabbers partial open");
        telemetry.addLine("x - grabbers closed");
        telemetry.addLine("a - increment claw");
        telemetry.addLine("dpad right - increment guides");
        telemetry.update();

        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /* display information */
            telemetry.addData("Grabbers ", "%.2f %.2f", robot.GGL.getPosition(),robot.GGR.getPosition());
            telemetry.addData("Claw     ", "%.2f", robot.Claw.getPosition());
            telemetry.addData("IsPressed" , "%b", pressed);
            telemetry.update();


            // grabbers -------------------------------------------------------------
            /*if (egamepad1.dpad_down.pressed) {
                robot.GGL.setPosition(robot.GGL.getPosition() - increment);
            }
            if (egamepad1.dpad_up.pressed) {
                robot.GGL.setPosition(robot.GGL.getPosition() + increment);
            }

            if (egamepad1.dpad_left.pressed) {
                robot.GGR.setPosition(robot.GGR.getPosition() - increment);
            }
            if (egamepad1.dpad_right.pressed) {
                robot.GGR.setPosition(robot.GGR.getPosition() + increment);
            }

            if (egamepad1.y.pressed) {
                robot.Claw.setPosition(robot.Claw.getPosition() + increment);
            }
            if (egamepad1.a.pressed) {
                robot.Claw.setPosition(robot.Claw.getPosition() - increment);
            }
*/
             /********** Grabber code **********/
            /*if (egamepad2.left_bumper.released) {
>>>>>>> WORLDS!-AUTO
                index_grabber_left = (index_grabber_left < 2) ? index_grabber_left + 1 : 0;
            }
            if (egamepad2.right_bumper.released) {
                index_grabber_right = (index_grabber_right < 2) ? index_grabber_right + 1 : 0;
            }
            if (egamepad2.b.released) {
                index_grabber_left = 2;
                index_grabber_right = 2;
            }
            if (egamepad2.x.released) {
                index_grabber_left = 1;
                index_grabber_right = 1;
            }
            robot.GGL.setPosition(robot.GRABBER_LEFT[index_grabber_left]);
            robot.GGR.setPosition(robot.GRABBER_RIGHT[index_grabber_right]);

            if (egamepad2.a.released) {
                index_claw = (index_claw < 1) ? index_claw + 1 : 0;
                robot.Claw.setPosition(robot.CLAW[index_claw]);
            }

            //*******Guides**********/
            if (egamepad2.dpad_right.pressed) {
                index_guide = (index_guide < 1) ? index_guide + 1 : 0;
                robot.LG.setPosition(robot.GUIDESLEFT[index_guide]);
                robot.RG.setPosition(robot.GUIDESRIGHT[index_guide]);
            }


            /* test code */
            if (gamepad2.left_trigger > 0.5 && !pressed) {
                pressed = true;
            }
            if (gamepad2.left_trigger < 0.5 && pressed){
                pressed = false;
            }

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
