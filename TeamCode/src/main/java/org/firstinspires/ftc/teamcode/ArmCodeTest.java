package org.firstinspires.ftc.teamcode;
/* version history
     -1/13/18 created file for testing arm control
     -1/23/18 added potentiometer feedback for upper arm
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

//naming the teleop thing
@TeleOp(name="ArmCode Test", group="Test")
public class ArmCodeTest extends LinearOpMode {

    RobotConfig robot = new RobotConfig();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {
        int index_arm = 0;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        //adds a lil' version thing to the telemetry so you know you're using the right version
        telemetry.addData("Version", "Dual 2.0");
        telemetry.addLine("gamepad 2");
        telemetry.addLine("dpad left - home");
        telemetry.addLine("dpad up   - next higher position");
        telemetry.addLine("dpad down - next lower position");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /* TeleOp code */
            /* Only call MoveToPosition method once per move */
            if (index_arm != -1) {
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
            }

            if (egamepad2.a.pressed) {
                index_arm = 0;
                robot.LowerArm.MoveToPosition(robot.LOWERARM[index_arm], 2.0);
                robot.UpperArm.MoveToPosition(robot.UPPERARM[index_arm], 2.0);
            }
            if (egamepad2.b.pressed) {
                index_arm = -1;
                robot.LowerArm.MoveToPosition(1.0, 2.0);
                robot.UpperArm.MoveToPosition(1.0, 2.0);
            }
            if (egamepad2.x.pressed) {
                index_arm = -1;
                robot.LowerArm.MoveToPosition(1.5, 2.0);
                robot.UpperArm.MoveToPosition(1.5, 2.0);
            }
            if (egamepad2.y.pressed) {
                index_arm = -1;
                robot.LowerArm.MoveToPosition(2.0, 2.0);
                robot.UpperArm.MoveToPosition(2.0, 2.0);
            }

            robot.ArmUpdate(this, true);

            telemetry.addData("Lower ", "%.2f %.2f", robot.LowerArm.CurrentPosition,robot.LowerArm.Power);
            telemetry.addData("Upper ", "%.2f %.2f", robot.UpperArm.CurrentPosition,robot.UpperArm.Power);
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
