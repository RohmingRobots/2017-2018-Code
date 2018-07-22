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
import org.firstinspires.ftc.teamcode.SubAssemblyGuides.GuideControl;
import org.firstinspires.ftc.teamcode.Utilities.GamepadWrapper;

//naming the teleop thing
@TeleOp(name = "TeleOp", group = "Drive")
public class teleop extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        double speed_forward_back, speed_left_right, speed_rotate_left_right;
        double speed = 2.5;
        double reverse = 1.0;

        boolean close_guides = false;

        //navigation color sensor variables
        boolean leftcolor = false;
        boolean rightcolor = false;

        /* initialize sub assemblies
         */
        DualArmControl Arms = new DualArmControl(this);
        GrabberControl Grabber = new GrabberControl(this);
        GuideControl Guide = new GuideControl(this);
        DriveControl Drive = new DriveControl(this);

        /* Instantiate extended gamepad */
        GamepadWrapper egamepad1 = new GamepadWrapper(gamepad1);
        GamepadWrapper egamepad2 = new GamepadWrapper(gamepad2);

        telemetry.addData("Version", "Worlds 4-13-2018");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            //and now, the fun stuff

            /* Update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /******Telemetry*****/
            //adds a lil' version thing to the telemetry so you know you're using the right version
            telemetry.addData("leftcolor", leftcolor);
            telemetry.addData("rightcolor", rightcolor);
            telemetry.addData("Speed", speed);
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
            speed_rotate_left_right = -gamepad1.right_stick_x * 2;

            // using the left joystick's y axis to move forward and backwards
            // using the left joystick's x axis to strafe left and right
            speed_forward_back = -gamepad1.left_stick_y;
            speed_left_right = -gamepad1.left_stick_x * 2;

            //takes all those values, divides
            speed_left_right = speed_left_right / 3.414 * speed * reverse;
            speed_forward_back = speed_forward_back / 3.414 * speed * reverse;
            speed_rotate_left_right = speed_rotate_left_right / 3.414 * speed * reverse;

            /******Dpad Drive*****/
            if (gamepad1.dpad_left) {
                Drive.moveLeft(speed);
            } else if (gamepad1.dpad_right) {
                Drive.moveRight(speed);
            } else if (gamepad1.dpad_up) {
                Drive.moveForward(speed);
            } else if (gamepad1.dpad_down) {
                Drive.moveBackward(speed);
            } else {
                Drive.moveCombination(speed_forward_back, speed_left_right, speed_rotate_left_right);
            }

            /**------------------------------------------------------------------------**/
            /********** GAMEPAD2 CONTROLS **********/
            /**------------------------------------------------------------------------**/

            /********** Grabber code **********/
            if (egamepad2.left_bumper.pressed) {
                Grabber.LeftServo.nextSetpoint();
            }
            if (egamepad2.right_bumper.pressed) {
                Grabber.RightServo.nextSetpoint();
            }
            if (egamepad2.b.pressed) {
                Grabber.LeftServo.setSetpoint(GrabberControl.GripSetpoints.PARTIAL);
                Grabber.RightServo.setSetpoint(GrabberControl.GripSetpoints.PARTIAL);
            }
            if (egamepad2.x.pressed) {
                Grabber.LeftServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
                Grabber.RightServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
            }
            if (egamepad2.a.released) {
                Grabber.ClawServo.nextSetpoint();
            }

            //*******Guides**********/
            if (egamepad2.dpad_right.pressed) {
                Guide.LeftServo.nextSetpoint();
                Guide.RightServo.nextSetpoint();
            }


            /********** Arm code **********/
            if (egamepad2.dpad_up.pressed) {
                Arms.nextSetpoint();
                close_guides = true;
            }
            //closing guides when arm is up
            if ((close_guides) && (Arms.UpperArm.getCurrentPosition() > .1)) {
                close_guides = false;
                Guide.LeftServo.setSetpoint(GuideControl.GuideSetpoints.RETRACT);
                Guide.RightServo.setSetpoint(GuideControl.GuideSetpoints.RETRACT);
            }
            if (egamepad2.dpad_down.pressed) {
                Arms.prevSetpoint();
            }
            if (egamepad2.dpad_left.pressed) {
                Arms.setSetpoint(DualArmControl.Setpoints.ROW1);
            }

            /* update sub assemblies */
            Arms.Update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
