package org.firstinspires.ftc.teamcode.SubAssemblyDrive;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;

//naming the teleop thing
@TeleOp(name = "Mecanum Test", group = "Test")
public class DriveTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DriveControl Drive = new DriveControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    public void displayHelp() {
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Reverse (combo only)  a");
        telemetry.addLine("  Change speed (+/-)    right/left bumper");
        telemetry.addLine("  Combination motion");
        telemetry.addLine("    strafe   right joystick x");
        telemetry.addLine("    move     left joystick y ");
        telemetry.addLine("    rotate   left joystick x");
        telemetry.addLine("  Direct motion");
        telemetry.addLine("    move & strafe  dpad");
        telemetry.addLine("    rotate         x, b");
    }

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        double speed_forward_back, speed_left_right, speed_rotate_left_right;
        double speed = 1.0;
        double reverse = 1.0;

        telemetry.addLine("Drive Test");

        /* initialize sub assemblies
         */
        Drive.initialize(this);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        displayHelp();
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            //and now, the fun stuff

            /* Update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            //when a button is just released, multiply the speed by -1 so it's reverse
            if (egamepad1.a.released) {
                reverse *= -1;
            }

            //change that speed by those bumpers
            if (gamepad1.right_bumper) {
                speed += 0.25;
                if (speed > 3) speed = 3;
            }
            if (gamepad1.left_bumper) {
                speed -= 0.25;
                if (speed < 0) speed = 0;
            }

            // using the right joystick's x axis to rotate left and right
            speed_left_right = -gamepad1.right_stick_x;

            // using the left joystick's y axis to move forward and backwards
            speed_forward_back = -gamepad1.left_stick_y;

            // using the left joystick's x axis to strafe left and right
            speed_rotate_left_right = -gamepad1.left_stick_x;

            //takes all those values, divides
            speed_left_right = speed_left_right * speed * reverse;
            speed_forward_back = speed_forward_back * speed * reverse;
            speed_rotate_left_right = speed_rotate_left_right * speed * reverse;

            if (gamepad1.dpad_left) {
                Drive.moveLeft(speed);
            } else if (gamepad1.dpad_right) {
                Drive.moveRight(speed);
            } else if (gamepad1.dpad_up) {
                Drive.moveForward(speed);
            } else if (gamepad1.dpad_down) {
                Drive.moveBackward(speed);
            } else if (gamepad1.x) {
                Drive.rotateLeft(speed);
            } else if (gamepad1.b) {
                Drive.rotateRight(speed);
            } else {
                Drive.moveCombination(speed_forward_back, speed_left_right, speed_rotate_left_right);
            }

            /* display information */
            if (egamepad1.guide.state) {
                displayHelp();
            } else {
                telemetry.addData("Speed: ", speed);
                telemetry.addData("Direction: ", speed);
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }

        /* Clean up sub-assemblies */
        Drive.cleanup();
        telemetry.update();
    }
}
