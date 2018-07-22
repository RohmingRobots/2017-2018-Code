package org.firstinspires.ftc.teamcode.SubAssemblyArms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utilities.GamepadWrapper;

//naming the teleop thing
@TeleOp(name = "Arms Test", group = "Test")
public class ArmsTest extends LinearOpMode {

    public void displayHelp() {
        telemetry.addLine("Press START for this help");
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  Arms");
        telemetry.addLine("    next/prev   dpad up/down");
        telemetry.addLine("    row 1       dpad left");
    }

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("Arms Test");

        /* initialize sub assemblies
         */
        DualArmControl Arms = new DualArmControl(this);

        /* Instantiate extended gamepad */
        GamepadWrapper egamepad1 = new GamepadWrapper(gamepad1);
        GamepadWrapper egamepad2 = new GamepadWrapper(gamepad2);

        displayHelp();
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            /* Update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /********** Arm code **********/
            if (egamepad1.dpad_up.pressed) {
                Arms.nextSetpoint();
            }
            if (egamepad1.dpad_down.pressed) {
                Arms.prevSetpoint();
            }
            if (egamepad1.dpad_left.pressed) {
                Arms.setSetpoint(DualArmControl.Setpoints.ROW1);
            }

            Arms.Update();

            if (egamepad1.start.state) {
                displayHelp();
            } else {
                telemetry.addData("Lower ", "%.2f %.2f", Arms.LowerArm.getCurrentPosition(), Arms.LowerArm.getPower());
                telemetry.addData("Upper ", "%.2f %.2f", Arms.UpperArm.getCurrentPosition(), Arms.UpperArm.getPower());
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
