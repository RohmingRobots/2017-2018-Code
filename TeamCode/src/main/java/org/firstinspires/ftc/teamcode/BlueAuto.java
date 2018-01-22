package org.firstinspires.ftc.teamcode;
/* version history 2.0
     -11/06/17 created autonomous practice
               add methods for basic movement (forward/backward, left/right, rotate left/right)
 */


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;


//naming the teleop thing
@Autonomous(name="Blue Auto", group ="Drive")
public class BlueAuto extends LinearOpMode {

    RobotConfig robot = new RobotConfig();
    private ElapsedTime runtime = new ElapsedTime();


    //clock reseter
    public void resetClock() {
        lastReset = runtime.seconds();
    }

    //mode 'stuff'
    //modes lists which steps and in what order to accomplish them
    int mode = 0;
    int [] modes = {2, 3, 7, 8, 9, 11, 12, 13, 100};

    //time based variables
    double lastReset = 0;
    double now = 0;

    //servo initialization
    //servo1
    //servo2

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        final double     MOVE_SPEED = 0.5;
        final double     ROTATE_SPEED = 0.3;
        final double     TEST_TIME = 2.0;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Wait for start");    //
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        resetClock();

        // telling the code to run until you press that giant STOP button on RC
        // include opModeIsActive in all while loops so that STOP button terminates all actions
        while (opModeIsActive() && modes[mode] < 100) {


            //keeps now up to date
            now = runtime.seconds() - lastReset;

            //MODE 1: Check Vumark
            /*if (modes[mode] == 1) {
                if (now > 1) {
                    mode++;
                    resetClock();
                }

                //Vuforia check
            }*/

            //MODE 2: Lock arm in resting pos
            switch (modes[mode]) {

                case 2:
                    robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
                    robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);
                    if (now > 0.5) {
                        mode++;
                        resetClock();
                    }
                    break;

                case 3:
                    if (now > 0.9) {
                        robot.UL.setPower(0);
                        robot.UR.setPower(0);
                        mode++;
                        resetClock();
                    } else {
                        robot.UL.setPower(0.2);
                        robot.UR.setPower(0.2);
                    }
                    break;

                //MODE 3-7 to be added if advance past state to score jewel

                //MODE 8: Grab preloaded glyph

                case 7:
                    if (now > 1) {
                        mode++;
                        resetClock();
                    }
                    robot.MoveForward(MOVE_SPEED);
                    break;

                case 8:
                    if (now > 1) {
                        mode++;
                        resetClock();
                    }
                    robot.RotateLeft(MOVE_SPEED);
                    break;

                case 9:
                    if (now > 0.3) {
                        mode++;
                        resetClock();
                    }
                    robot.MoveForward(MOVE_SPEED);
                    break;

                //MODE 11: Release glyph
                case 11:
                    robot.GGL.setPosition(robot.GRABBER_LEFT[0]);
                    robot.GGR.setPosition(robot.GRABBER_RIGHT[0]);
                    if (now > 0.5) {
                        mode++;
                        resetClock();
                    }
                    break;

                //MODE 12: Back up a bit
                case 12:
                    if (now > 0.3) {
                        mode++;
                        resetClock();
                    }
                    robot.MoveBackward(MOVE_SPEED);
                    break;

                //MODE 13: Do a 180
                case 13:
                    if (now > 2.4) {
                        mode++;
                        resetClock();
                    }
                    robot.RotateRight(ROTATE_SPEED);
                    break;

                //MODE 14: Bonus glyphs! (if time)


            }  // end of switch

            /*
            //and now, the fun stuff

            // Test forwards movement
            robot.MoveForward(MOVE_SPEED);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < TEST_TIME)) {
                telemetry.addData("Move Forward", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            robot.MoveStop();

            // Test backwards movement
            robot.MoveBackward(MOVE_SPEED);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < TEST_TIME)) {
                telemetry.addData("Move Backward", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            robot.MoveStop();

            // Test left movement
            robot.MoveLeft(MOVE_SPEED);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < TEST_TIME)) {
                telemetry.addData("Move Left", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            robot.MoveStop();

            // Test right movement
            robot.MoveRight(MOVE_SPEED);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < TEST_TIME)) {
                telemetry.addData("Move Right", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            robot.MoveStop();

            // Test rotate left
            robot.RotateLeft(ROTATE_SPEED);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < TEST_TIME)) {
                telemetry.addData("Rotate Left", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            robot.MoveStop();

            // Test rotate right
            robot.RotateRight(ROTATE_SPEED);
            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < TEST_TIME)) {
                telemetry.addData("Rotate Right", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            robot.MoveStop();

            // Send telemetry message to signify robot waiting;
            telemetry.addData("Status", "Wait for end of autonomous");    //
            telemetry.update();
            while (opModeIsActive()) {
            }
*/





        }
    }
}