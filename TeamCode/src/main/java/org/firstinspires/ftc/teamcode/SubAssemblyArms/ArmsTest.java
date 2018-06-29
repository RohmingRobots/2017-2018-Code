package org.firstinspires.ftc.teamcode.SubAssemblyArms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.GamepadEdge;

//naming the teleop thing
@TeleOp(name="Arms Test", group="Test")
public class ArmsTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DualArmControl Arms = new DualArmControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {

        /* Initialize sub assemblies
         */
        Arms.Initialize(this);

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

            /********** Arm code **********/
            /* Only call MoveToPosition method once per move */
            if (egamepad2.dpad_up.pressed) {
                if (gamepad2.a) {
                    /* allow full extension */
                    Arms.IncrementPositionExtended();
                } else {
                    Arms.IncrementPosition();
                }
            }
            if (egamepad2.dpad_down.pressed) {
                Arms.DecrementPosition();
            }
            if (egamepad2.dpad_left.pressed) {
                if (Arms.getDualIndex()>3) {
                    Arms.SetPosition(3);
                } else {
                    Arms.SetPosition(0);
                }
            }

            Arms.Update(true);

            telemetry.addData("Lower ", "%.2f %.2f", Arms.LowerArm.getCurrentPosition(),Arms.LowerArm.getPower());
            telemetry.addData("Upper ", "%.2f %.2f", Arms.UpperArm.getCurrentPosition(),Arms.UpperArm.getPower());
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
