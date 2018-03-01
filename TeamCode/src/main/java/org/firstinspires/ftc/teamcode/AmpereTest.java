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

    @Override
    public void runOpMode() throws InterruptedException {

        ElapsedTime OurTime = new ElapsedTime();
        double position, step = 0.1;
        double AMPERE_POWER = 0.8;
        int mode = 100;
        double increment;
        int index_left_flipper, index_right_flipper;
        int blue_left_right, red_left_right;

        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        increment = 0.05;
        index_left_flipper = 0;
        index_right_flipper = 0;

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

        /* close grabbers so side arms can extend */
        robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
        robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);

        OurTime.reset();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /* display information */
            telemetry.addData("AW   ","%.2f %.2f", robot.AWL.getPower(), robot.AWR.getPower());
            telemetry.addData("AF   ","%.2f %.2f", robot.AFL.getPosition(), robot.AFR.getPosition());
            telemetry.addData("Blue ","%3d %3d", robot.left_ampere.blue(),robot.right_ampere.blue());
            telemetry.addData("Red  ","%3d %3d", robot.left_ampere.red(),robot.right_ampere.red());
            if (robot.left_ampere.blue() > robot.right_ampere.blue()+5) {
                // left is BLUE
                telemetry.addLine("BLUE left");
                blue_left_right = -1;
            } else if (robot.left_ampere.blue()+5 < robot.right_ampere.blue()) {
                // right is BLUE
                telemetry.addLine("BLUE right");
                blue_left_right = 1;
            } else {
                // no clear blue
                telemetry.addLine("BLUE ???");
                blue_left_right = 0;
            }
            if (robot.left_ampere.red() > robot.right_ampere.red()+5) {
                // left is RED
                telemetry.addLine("RED left");
                red_left_right = -1;
            } else if (robot.left_ampere.red()+5 < robot.right_ampere.red()+5) {
                // right is RED
                telemetry.addLine("RED right");
                red_left_right = 1;
            } else {
                // no clear red
                telemetry.addLine("RED ???");
                red_left_right = 0;
            }
            telemetry.update();

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

            if (egamepad1.y.released) {
                mode = 0;
                OurTime.reset();
            }

            // no gamepad - mimic autonomous
            switch (mode) {
                case 0:
                    robot.LowerArm.MoveToPosition(0.0);
                    robot.UpperArm.MoveToPosition(0.4);
                    if (OurTime.seconds()<4.0)
                        break;
                    mode++;
                    OurTime.reset();
                    break;

                case 10:
                    // extend part way
                    robot.AWL.setPower(AMPERE_POWER);
                    robot.AWR.setPower(AMPERE_POWER);
                    if (OurTime.seconds()<4.0)
                        break;
                    // stop
                    robot.AWL.setPower(0.0);
                    robot.AWR.setPower(0.0);
                    mode++;
                    OurTime.reset();
                    break;
                case 11:
                    // extend flippers (continue extending)
                    robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[2]);
                    robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[2]);
                    if (OurTime.seconds()<1.0)
                        break;
                    mode++;
                    OurTime.reset();
                    break;
                case 12:
                    // extend rest of the way
                    robot.AWL.setPower(AMPERE_POWER);
                    robot.AWR.setPower(AMPERE_POWER);
                    if (OurTime.seconds()<4.0)
                        break;
                    // stop
                    robot.AWL.setPower(0.0);
                    robot.AWR.setPower(0.0);
                    mode++;
                    OurTime.reset();
                    break;

                case 20:
                    // flick jewel
                    if ( blue_left_right*red_left_right < 0 ) {
                        if (blue_left_right > 0) {
                            robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
                        } else {
                            robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
                        }
                    }
                    if (OurTime.seconds()<1.0)
                        break;
                    mode++;
                    OurTime.reset();
                    break;

                case 31:
                    // retract part of the way
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);
                    if (OurTime.seconds()<4.0)
                        break;
                    // stop
                    robot.AWL.setPower(0.0);
                    robot.AWR.setPower(0.0);
                    mode++;
                    OurTime.reset();
                    break;
                case 32:
                    // retract flippers
                    robot.AFL.setPosition(robot.AMPERE_FLICKER_LEFT[0]);
                    robot.AFR.setPosition(robot.AMPERE_FLICKER_RIGHT[0]);
                    // retract rest of the way
                    robot.AWL.setPower(-AMPERE_POWER);
                    robot.AWR.setPower(-AMPERE_POWER);
                    if (OurTime.seconds()<4.0)
                        break;
                    // stop
                    robot.AWL.setPower(0.0);
                    robot.AWR.setPower(0.0);
                    mode++;
                    OurTime.reset();
                    break;

                case 40:
                    robot.LowerArm.MoveHome();
                    robot.UpperArm.MoveHome();
                    if (OurTime.seconds()<1.0)
                        break;
                    mode++;
                    OurTime.reset();
                    break;

                case 100:
                    break;

                default:
                    mode++;
                    break;
            }

            robot.LowerArm.Update(this);
            robot.UpperArm.Update(this);

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
