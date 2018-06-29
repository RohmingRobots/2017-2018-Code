package org.firstinspires.ftc.teamcode;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;


//naming the teleop thing
@TeleOp(name="Ampere Test", group="Test")
public class AmpereTest extends LinearOpMode {

    RobotConfig robot = new RobotConfig();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    //ampere color sensor variables
    int leftamperered = 0;
    int leftampereblue = 0;
    int rightamperered = 0;
    int rightampereblue = 0;
    boolean leftampere = false;
    boolean rightampere = false;

    @Override
    public void runOpMode() throws InterruptedException {

        ElapsedTime OurTime = new ElapsedTime();
        double AMPERE_POWER = 0.8;
        double increment;
        int index_left_flipper, index_right_flipper;

        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        increment = 0.05;
        index_left_flipper = 0;
        index_right_flipper = 0;

        //turns ampere LEDs om
        robot.left_ampere.enableLed(true);
        robot.right_ampere.enableLed(true);

        telemetry.addLine("Ampere Test");
        telemetry.addLine("gamepad 1");
        telemetry.addLine("d-up   - extend side arms");
        telemetry.addLine("d-down - retract side arms");
        telemetry.addLine("x      - cycle through left flipper");
        telemetry.addLine("b      - cycle through right flipper");
        telemetry.addLine("stick button  - increment flipper");
        telemetry.addLine("bumper button - decrement flipper");
        telemetry.addLine("y      - autonomous test");
        telemetry.update();

        waitForStart();

        OurTime.reset();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            // side arms
            if (egamepad1.dpad_down.pressed) {
                // retract
                robot.AWL.setPower(-AMPERE_POWER);
                robot.AWR.setPower(-AMPERE_POWER);
            } else if (egamepad1.dpad_down.released){
                // stop
                robot.AWL.setPower(0.0);
                robot.AWR.setPower(0.0);
            }
            if (egamepad1.dpad_up.pressed) {
                // extend
                robot.AWL.setPower(AMPERE_POWER);
                robot.AWR.setPower(AMPERE_POWER);
            } else if (egamepad1.dpad_up.released){
                // stop
                robot.AWL.setPower(0.0);
                robot.AWR.setPower(0.0);
            }

            // flippers
            if (egamepad1.x.released) {
                index_left_flipper = index_left_flipper+1;
                if (index_left_flipper > 2) {
                    index_left_flipper = 0;
                }
                robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[index_left_flipper]);
            }
            if (egamepad1.left_stick_button.released) {
                robot.AFL.setPosition(robot.AFL.getPosition()+increment);
            }
            if (egamepad1.left_bumper.released) {
                robot.AFL.setPosition(robot.AFL.getPosition()-increment);
            }
            if (egamepad1.b.released) {
                index_right_flipper = index_right_flipper+1;
                if (index_right_flipper > 2) {
                    index_right_flipper = 0;
                }
                robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[index_right_flipper]);
            }
            if (egamepad1.right_stick_button.released) {
                robot.AFR.setPosition(robot.AFR.getPosition()+increment);
            }
            if (egamepad1.right_bumper.released) {
                robot.AFR.setPosition(robot.AFR.getPosition()-increment);
            }

            if (egamepad1.dpad_left.released) {
                //calibrates to light of open air
                leftamperered = robot.left_ampere.red();
                leftampereblue = robot.left_ampere.blue();
                rightamperered = robot.right_ampere.red();
                rightampereblue = robot.right_ampere.blue();
            }

            /* checks if both color sensors detect a difference in the change of values and
               returns true if the side is red and the side is blue */
            leftampere = false;
            rightampere = false;
            if ( ((robot.left_ampere.red() - leftamperered) > 10 + (robot.right_ampere.red() - rightamperered)) &&
                 ((robot.right_ampere.blue() - rightampereblue) > 10 + (robot.left_ampere.blue() - leftampereblue)) ) {
                leftampere = true;
            }
            if (  ((robot.right_ampere.red() - rightamperered) > 10 + (robot.left_ampere.red() - leftamperered)) &&
                  ((robot.left_ampere.blue() - leftampereblue) > 10 + (robot.right_ampere.blue() - rightampereblue)) ) {
                rightampere = true;
            }

            telemetry.addData("leftamperered", leftamperered);
            telemetry.addData("leftampereblue", leftampereblue);
            telemetry.addData("rightamperered", rightamperered);
            telemetry.addData("rightampereblue", rightampereblue);
            telemetry.addData("left_color red", robot.left_ampere.red());
            telemetry.addData("left_color blue", robot.left_ampere.blue());
            telemetry.addData("right_color red", robot.right_ampere.red());
            telemetry.addData("right_color blue", robot.right_ampere.blue());
            telemetry.addData("leftampere", leftampere);
            telemetry.addData("rightampere", rightampere);
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
