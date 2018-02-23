package org.firstinspires.ftc.teamcode;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

//naming the teleop thing
@TeleOp(name="TeleOp", group="Drive")
public class teleop extends LinearOpMode {

    RobotConfig robot = new RobotConfig();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        double front_right;
        double front_left;
        double back_left;
        double back_right;
        double speed = 2.5;
        double reverse = 1.0;
        int grabber_left;
        int grabber_right;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */

        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        grabber_left = 0;
        grabber_right = 0;
        telemetry.addData("Version", "State");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            //and now, the fun stuff

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            //DpadDirection dpadDirection = GetDpadDirection(gamepad1);

            boolean abutton = egamepad1.a.released;

            /******Telemetry*****/
            //adds a lil' version thing to the telemetry so you know you're using the right version
            telemetry.addData("Speed", speed);
            telemetry.update();

            /**------------------------------------------------------------------------**/
            /******GAMEPAD1 CONTROLS*****/
            /**------------------------------------------------------------------------**/

            /******Reverse*****/
            //when a button is just released, multiply the speed by -1 so it's reverse
            if (abutton) {
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
            front_right = -gamepad1.right_stick_x * 2;
            front_left = gamepad1.right_stick_x * 2;
            back_left = gamepad1.right_stick_x * 2;
            back_right = -gamepad1.right_stick_x * 2;

            // using the left joystick's y axis to move forward and backwards
            front_right -= gamepad1.left_stick_y;
            front_left -= gamepad1.left_stick_y;
            back_left -= gamepad1.left_stick_y;
            back_right -= gamepad1.left_stick_y;

            // using the left joystick's x axis to strafe left and right
            front_right += -gamepad1.left_stick_x * 2;
            front_left += gamepad1.left_stick_x * 2;
            back_left += -gamepad1.left_stick_x * 2;
            back_right += gamepad1.left_stick_x * 2;

            front_right = front_right / 3.414 * speed * reverse;
            front_left = front_left / 3.414 * speed * reverse;
            back_left = back_left / 3.414 * speed * reverse;
            back_right = back_right / 3.414 * speed * reverse;

            /******Dpad Drive*****/
            if (gamepad1.dpad_left) {
                robot.MoveLeft(speed);
            } else if (gamepad1.dpad_right) {
                robot.MoveRight(speed);
            } else if (gamepad1.dpad_up) {
                robot.MoveForward(speed);
            } else if (gamepad1.dpad_down) {
                robot.MoveBackward(speed);
            } else {
                //takes all those values, divides
                robot.FR.setPower(front_right);
                robot.FL.setPower(front_left);
                robot.BL.setPower(back_left);
                robot.BR.setPower(back_right);
            }

            /**------------------------------------------------------------------------**/
            /********** GAMEPAD2 CONTROLS **********/
            /**------------------------------------------------------------------------**/

            /********** Grabber code **********/
            if (egamepad2.left_bumper.released) {
                grabber_left = (grabber_left < 2) ? grabber_left + 1 : 0;
            }
            if (egamepad2.right_bumper.released) {
                grabber_right = (grabber_right < 2) ? grabber_right + 1 : 0;
            }
            if (egamepad2.b.released) {
                grabber_left = 0;
                grabber_right = 0;
            }
            if (egamepad2.x.released) {
                grabber_left = 1;
                grabber_right = 1;
            }
            robot.GGL.setPosition(robot.GRABBER_LEFT[grabber_left]);
            robot.GGR.setPosition(robot.GRABBER_RIGHT[grabber_right]);

            if (egamepad2.a.released) {
                if (robot.Claw.getPosition() > 0.5) {
                    robot.Claw.setPosition(robot.CLAW[1]);
                } else {
                    robot.Claw.setPosition(robot.CLAW[0]);
                }
            }

            /********** Arm code **********/
            if (egamepad2.y.pressed) {
                robot.LowerArm.MoveToPosition(0.0);
                robot.UpperArm.MoveToPosition(0.1);
            }
            if (egamepad2.dpad_down.pressed) {
                robot.LowerArm.MoveHome();
                robot.UpperArm.MoveHome();
            }
            if (egamepad2.dpad_left.pressed) {
                robot.LowerArm.MoveToPosition(0.0);
                robot.UpperArm.MoveToPosition(0.2);
            }
            if (egamepad2.dpad_right.pressed) {
                robot.LowerArm.MoveToPosition(0.2);
                robot.UpperArm.MoveToPosition(0.4);
            }
            if (egamepad2.dpad_up.pressed) {
                robot.LowerArm.MoveToPosition(0.3);
                robot.UpperArm.MoveToPosition(0.7);
            }

            robot.LowerArm.Update(this);
            robot.UpperArm.Update(this);

            //let the robot have a little rest, sleep is healthy
            sleep(40);

        }
    }
}

