package org.firstinspires.ftc.teamcode;
/* version history
     -1/13/18 created file for testing arm control
     -1/23/18 added potentiometer feedback for upper arm
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubAssemblyAmpere.AmpereControl;
import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;
import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;

/**
 * Created by ablauch on 2/21/2018.
 */

@TeleOp(name="Sensor Test", group="Test")
public class SensorTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DualArmControl Arms = new DualArmControl();
    AmpereControl Ampere = new AmpereControl();
    DriveControl Drive = new DriveControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("Sensor Test");

        /* initialize sub assemblies
         */
        Arms.initialize(this);
        Ampere.initialize(this, false);
        Drive.initialize(this);

        /* turn on color sensor LEDs */
        Drive.ColorLeft.enableLed(true);
        Drive.ColorRight.enableLed(true);
        Ampere.ColorLeft.enableLed(true);
        Ampere.ColorRight.enableLed(true);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

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
            telemetry.addData("Limits: ","%b %b",
                    Arms.LowerArm.Limit.getState(), Arms.UpperArm.Limit.getState());
            telemetry.addData("Pots: ","%.2f %.2f",
                    Arms.LowerArm.getCurrentPosition(), Arms.UpperArm.getCurrentPosition());

            /* Display color sensors */
            telemetry.addLine("Color Sensors (red/blue)");
            telemetry.addData("   track left ","%3d %3d", Drive.ColorLeft.red(),Drive.ColorLeft.blue());
            telemetry.addData("   track right","%3d %3d", Drive.ColorRight.red(),Drive.ColorRight.blue());
            telemetry.addData("  ampere left ","%3d %3d", Ampere.ColorLeft.red(),Ampere.ColorLeft.blue());
            telemetry.addData("  ampere right","%3d %3d", Ampere.ColorRight.red(),Ampere.ColorRight.blue());

            Arms.Update(false);

            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }

        /* Clean up sub-assemblies */
        Arms.cleanup();
        Ampere.cleanup();
        Drive.cleanup();
        telemetry.update();
    }
}
