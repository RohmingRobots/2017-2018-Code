package org.firstinspires.ftc.teamcode.SubAssemblySample;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/* Sub Assembly Test OpMode
 * This TeleOp OpMode is used to test the functionality of the specific sub assembly
 */
// Assign OpMode type (TeleOp or Autonomous), name, and grouping
@TeleOp(name="SubAssembly Test", group="Test")
public class SubAssemblyTest extends LinearOpMode {

    SubAssemblyTemplate SubAssembly = new SubAssemblyTemplate();

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("SubAssembly Test OpMode");

        /* initialize sub-assemblies */
        SubAssembly.initialize(this);
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            SubAssembly.test();
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }

        /* Clean up sub-assemblies */
        SubAssembly.cleanup();
        telemetry.update();
    }
}
