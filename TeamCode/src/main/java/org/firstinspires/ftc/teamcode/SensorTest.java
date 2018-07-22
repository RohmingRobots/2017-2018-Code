package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubAssemblyAmpere.AmpereControl;
import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;
import org.firstinspires.ftc.teamcode.Utilities.GamepadWrapper;

/**
 * Created by ablauch on 2/21/2018.
 */

@TeleOp(name = "Sensor Test", group = "Test")
public class SensorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("Sensor Test");

        /* initialize sub assemblies
         */
        DualArmControl Arms = new DualArmControl(this);
        AmpereControl Ampere = new AmpereControl(this, false);
        DriveControl Drive = new DriveControl(this);

        /* turn on color sensor LEDs */
        Drive.ColorLeft.enableLed(true);
        Drive.ColorRight.enableLed(true);
        Ampere.ColorLeft.enableLed(true);
        Ampere.ColorRight.enableLed(true);

        /* Instantiate extended gamepad */
        GamepadWrapper egamepad1 = new GamepadWrapper(gamepad1);
        GamepadWrapper egamepad2 = new GamepadWrapper(gamepad2);

        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* Update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /* Display battery sensors */
            telemetry.addData("Battery: ", "%.2f", Drive.Battery.getVoltage());

            /* Display arm sensors */
            telemetry.addLine("Arm (lower, upper)");
            telemetry.addData("Limits: ", "%b %b", Arms.LowerArm.Limit.getState(), Arms.UpperArm.Limit.getState());
            telemetry.addData("Pots: ", "%.3f %.3f", Arms.LowerArm.getCurrentPosition(), Arms.UpperArm.getCurrentPosition());

            /* Display color sensors */
            telemetry.addLine("Color Sensors (left/right)");
            telemetry.addData("   track red ", "%3d %3d", Drive.ColorLeft.red(), Drive.ColorRight.red());
            telemetry.addData("   track blue", "%3d %3d", Drive.ColorLeft.blue(), Drive.ColorRight.blue());
            telemetry.addData("  ampere red ", "%3d %3d", Ampere.ColorLeft.red(), Ampere.ColorRight.red());
            telemetry.addData("  ampere blue", "%3d %3d", Ampere.ColorLeft.blue(), Ampere.ColorRight.blue());

            Arms.Update(false);

            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
