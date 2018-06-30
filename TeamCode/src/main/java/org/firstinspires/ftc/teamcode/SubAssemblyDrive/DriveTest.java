package org.firstinspires.ftc.teamcode.SubAssemblyDrive;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.GamepadEdge;

//naming the teleop thing
@TeleOp(name = "Mecanum Test", group = "Test")
public class DriveTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DriveControl Drive = new DriveControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        double speed_forward_back, speed_left_right, speed_rotate_left_right;
        double speed = 1.0;
        double reverse = 1.0;

        telemetry.addLine("Drive Test");

        /* Initialize sub assemblies
         */
        Drive.Initialize(this);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            //and now, the fun stuff

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            //adds a lil' version thing to the telemetry so you know you're using the right version
            telemetry.addData("Version", "2.2");
            telemetry.addData("Speed", speed);
            telemetry.update();

            //when a button is just released, multiply the speed by -1 so it's reverse
            if (egamepad1.a.released) {
                reverse *= -1;
            }

            //change that speed by those bumpers
            if (gamepad1.right_bumper) {
                speed += 0.25;
            }
            if (gamepad1.left_bumper) {
                speed -= 0.25;
            }
            //if the speed is at the min/max value set it to NOT min/max so boom it cant go over
            if (speed < 0) {
                speed = 0;
            }
            if (speed > 3) {
                speed = 3;
            }

            // using the right joystick's x axis to rotate left and right
            speed_left_right = -gamepad1.right_stick_x * 2;

            // using the left joystick's y axis to move forward and backwards
            speed_forward_back = -gamepad1.left_stick_y;

            // using the left joystick's x axis to strafe left and right
            speed_rotate_left_right = -gamepad1.left_stick_x * 2;

            //takes all those values, divides
            speed_left_right = speed_left_right / 3.414 * speed * reverse;
            speed_forward_back = speed_forward_back / 3.414 * speed * reverse;
            speed_rotate_left_right = speed_rotate_left_right / 3.414 * speed * reverse;


        /*for later- joysticks have a max input of 1 or -1. divide it by 3,
          which leaves us with a max input of 0.333333. motors have a max input
           of one. i'm not quite sure if this is perfectly true because i havent tested,
           but that should allow us to have a max speed var of 3. if you were to
           have max inputs on everything, you'd have 1 / 3 * 1 * 1, which
           equals 0.33. so the max speed should be set to 3, leaving us with
           1 / 3 * 3 * 1, equaling out to 1, our max value.
        */

            if (gamepad1.dpad_left) {
                Drive.MoveLeft(speed);
            } else if (gamepad1.dpad_right) {
                Drive.MoveRight(speed);
            } else if (gamepad1.dpad_up) {
                Drive.MoveForward(speed);
            } else if (gamepad1.dpad_down) {
                Drive.MoveBackward(speed);
            } else if (gamepad1.x) {
                Drive.RotateLeft(speed);
            } else if (gamepad1.b) {
                Drive.RotateRight(speed);
            } else {
                Drive.MoveCombination(speed_forward_back, speed_left_right, speed_rotate_left_right);
            }

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }

}




