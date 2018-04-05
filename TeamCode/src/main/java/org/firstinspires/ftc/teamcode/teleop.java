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
        int index_grabber_left;
        int index_grabber_right;
        int index_claw;
        int index_arm;

        //navigation color sensor variables
        boolean leftcolor = false;
        boolean rightcolor = false;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */

        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        index_grabber_left = 0;
        index_grabber_right = 0;
        index_claw = 0;
        index_arm = 0;
        telemetry.addData("Version", "World");
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
            telemetry.addData("leftcolor", leftcolor);
            telemetry.addData("rightcolor", rightcolor);
            telemetry.addData("Lower", robot.LowerArm.CurrentPosition);
            telemetry.addData("Upper", robot.UpperArm.CurrentPosition);
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
            }
            robot.Claw.setPosition(robot.CLAW[index_claw]);

            /********** Arm code **********/
            /* Only call MoveToPosition method once per move */
            if (egamepad2.dpad_up.pressed) {
                index_arm = (index_arm < 3) ? index_arm + 1 : 3;
                robot.LowerArm.MoveToPosition(robot.LOWERARM[index_arm], 0.5);
                robot.UpperArm.MoveToPosition(robot.UPPERARM[index_arm], 0.5);
            }
            if (egamepad2.dpad_down.pressed) {
                index_arm = (index_arm > 0) ? index_arm - 1 : 0;
                robot.LowerArm.MoveToPosition(robot.LOWERARM[index_arm], 0.5);
                robot.UpperArm.MoveToPosition(robot.UPPERARM[index_arm], 0.5);
            }
            if (egamepad2.dpad_left.pressed) {
                index_arm = 0;
                robot.LowerArm.MoveToPosition(robot.LOWERARM[index_arm], 0.5);
                robot.UpperArm.MoveToPosition(robot.UPPERARM[index_arm], 0.5);
            }
            if (egamepad2.dpad_right.pressed) {

            }

            robot.LowerArm.Update(this);
            robot.UpperArm.Update(this);

            //let the robot have a little rest, sleep is healthy
            sleep(40);

        }
    }
}
