package org.firstinspires.ftc.teamcode;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;
import org.firstinspires.ftc.teamcode.SubAssemblyGrabber.GrabberControl;
import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;

//naming the teleop thing
@TeleOp(name = "TeleOp", group = "Drive")
public class teleop extends LinearOpMode {

    /* Sub Assemblies
     */
    DualArmControl Arms = new DualArmControl();
    GrabberControl Grabber = new GrabberControl();
    DriveControl Drive = new DriveControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        double speed_forward_back, speed_left_right, speed_rotate_left_right;
        double speed = 2.5;
        double reverse = 1.0;

        int close_guides;

        //navigation color sensor variables
        boolean leftcolor = false;
        boolean rightcolor = false;

        /* Initialize sub assemblies
         */
        Arms.Initialize(this);
        Grabber.Initialize(this);
        Drive.Initialize(this);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        close_guides = 0;

        telemetry.addData("Version", "Worlds 4-13-2018");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            //and now, the fun stuff

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /******Telemetry*****/
            //adds a lil' version thing to the telemetry so you know you're using the right version
            telemetry.addData("leftcolor", leftcolor);
            telemetry.addData("rightcolor", rightcolor);
            telemetry.addData("Speed", speed);
//            telemetry.addData("HOOF", robot.ANTIREDNEK.getPosition());
            telemetry.update();

            /**------------------------------------------------------------------------**/
            /******GAMEPAD1 CONTROLS*****/
            /**------------------------------------------------------------------------**/

            /******Reverse*****/
            //when a button is just released, multiply the speed by -1 so it's reverse
            if (egamepad1.a.released) {
                reverse *= -1;
            }

            /******Speed Changing*****/
            //change that speed by those bumpers
            if (egamepad1.right_bumper.released) {
                speed += 0.25;
            }
            if (egamepad1.left_bumper.released) {
                speed -= 0.25;
            }
            //if the speed is at the min/max value set it to NOT min/max so boom it cant go over
            if (speed < 0) {
                speed = 0;
            }
            if (speed > 3) {
                speed = 3;
            }

            /******Joystick Drive*****/
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

            /******Dpad Drive*****/
            if (gamepad1.dpad_left) {
                Drive.MoveLeft(speed);
            } else if (gamepad1.dpad_right) {
                Drive.MoveRight(speed);
            } else if (gamepad1.dpad_up) {
                Drive.MoveForward(speed);
            } else if (gamepad1.dpad_down) {
                Drive.MoveBackward(speed);
            } else {
                Drive.MoveCombination(speed_forward_back, speed_left_right, speed_rotate_left_right);
            }

            /**------------------------------------------------------------------------**/
            /********** GAMEPAD2 CONTROLS **********/
            /**------------------------------------------------------------------------**/

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
            if (egamepad2.a.pressed) {
                Grabber.ToggleClaw();
            }


            //*******Guides**********/
            if (egamepad2.dpad_right.pressed) {
                Grabber.ToggleGuides();
            }


            /********** Arm code **********/
            if (egamepad2.dpad_up.pressed) {
                Arms.IncrementPosition();
                close_guides = 1;
            }
            //closing guides when arm is up/
            if ((close_guides == 1) && (Arms.UpperArm.getCurrentPosition() > .1)) {
                close_guides = 0;
                Grabber.CloseGuides();
            }
            if (egamepad2.dpad_down.pressed) {
                Arms.DecrementPosition();
            }
            if (egamepad2.dpad_left.pressed) {
                Arms.SetPosition(0);
            }
/*            if (gamepad1.y) {
                robot.ANTIREDNEK.setPosition(1);
            }
            if (gamepad1.b) {
                robot.ANTIREDNEK.setPosition(0.3);
            }
*/

            /* update sub assemblies */
            Arms.Update(true);


            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
