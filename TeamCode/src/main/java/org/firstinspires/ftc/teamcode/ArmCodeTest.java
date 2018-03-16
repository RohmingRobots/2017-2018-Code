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
        telemetry.addLine("dpad - set positions");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /* TeleOp code */
            if (egamepad2.dpad_down.pressed) {
                robot.LowerArm.MoveHome();
                robot.UpperArm.MoveHome();
            }
            if (egamepad2.dpad_left.pressed) {
                robot.LowerArm.MoveToPosition(0.0, 1.0);
                robot.UpperArm.MoveToPosition(0.2, 1.0);
            }
            if (egamepad2.dpad_right.pressed) {
                robot.LowerArm.MoveToPosition(0.3, 1.0);
                robot.UpperArm.MoveToPosition(0.5, 1.0);
            }
            if (egamepad2.dpad_up.pressed) {
                robot.LowerArm.MoveToPosition(0.5, 1.0);
                robot.UpperArm.MoveToPosition(0.8, 1.0);
            }

            if (egamepad2.y.pressed) {
                robot.UpperArm.MoveIncrement(0.1, 1.0);
            }
            if (egamepad2.a.pressed) {
                robot.UpperArm.MoveIncrement(-0.1, 1.0);
            }
            if (egamepad2.b.pressed) {
                robot.LowerArm.MoveIncrement(0.05, 1.0);
            }
            if (egamepad2.x.pressed) {
                robot.LowerArm.MoveIncrement(-0.05, 1.0);
            }

            robot.LowerArm.Update(this, 0.0);
            robot.UpperArm.Update(this, -robot.LowerArm.Angle);

            telemetry.addData("Angles","%5.0f %5.0f",robot.LowerArm.Angle,robot.UpperArm.Angle);
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
