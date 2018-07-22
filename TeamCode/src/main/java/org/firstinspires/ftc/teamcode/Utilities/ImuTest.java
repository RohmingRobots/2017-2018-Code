package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Test opmode for ImuControl
 */

@TeleOp(name = "ImuTest", group = "Test")
public class ImuTest extends LinearOpMode {

    /* displays information to user about gamepad usage */
    public void displayHelp() {
        telemetry.addLine("Press START for this help");
        telemetry.addLine("Gamepad 1");
        telemetry.addLine("  set reference angle   A");
    }

    @Override
    public void runOpMode() {
        double refangle, angle;

        telemetry.addLine("Imu Test");

        /* initialize sub assemblies
         */
        ImuControl Imu = new ImuControl(this, true);

        /* Instantiate extended gamepad */
        GamepadWrapper egamepad1 = new GamepadWrapper(gamepad1);
        GamepadWrapper egamepad2 = new GamepadWrapper(gamepad2);

        displayHelp();
        telemetry.update();

        // Wait until we're told to go
        waitForStart();

        // Loop and update the dashboard
        while (opModeIsActive()) {

            /* Update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            if (egamepad1.a.pressed)
                Imu.setReferenceAngle();

            refangle = Imu.getReferenceAngle();
            angle = Imu.getRelativeAngle();

            /* display information */
            if (egamepad1.start.state) {
                displayHelp();
            } else {
                telemetry.addData("reference angle", "%.1f", refangle);
                telemetry.addData("relative angle", "%.1f", angle);
            }
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
