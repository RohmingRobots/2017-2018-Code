package org.firstinspires.ftc.teamcode;
/* version history
     -1/13/18 created file for testing arm control
     -1/23/18 added potentiometer feedback for upper arm
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.SubAssemblyAmpere.AmpereControl;
import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;

/**
 * Created by ablauch on 2/21/2018.
 */

@TeleOp(name="Sensor Test", group="Test")
public class SensorTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DualArmControl Arms = new DualArmControl();
    AmpereControl Ampere = new AmpereControl();

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

        /* Initialize sub assemblies
         */
        Arms.Initialize(this);
        Ampere.Initialize(this);

        /* turn on color sensor LEDs &/
        robot.left_color.enableLed(true);
        robot.right_color.enableLed(true);
        robot.left_ampere.enableLed(true);
        robot.right_ampere.enableLed(true);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        //adds a lil' version thing to the telemetry so you know you're using the right version
        telemetry.addLine("Sensor Test");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            /* Display battery sensors */
            telemetry.addData("Battery: ", "%.2f", robot.Battery.getVoltage());

            /* Display arm sensors */
            telemetry.addLine("Arm (lower, upper)");
            telemetry.addData("Limits: ","%b %b",
                    Arms.LowerArm.Limit.getState(), Arms.UpperArm.Limit.getState());
            telemetry.addData("Pots: ","%.2f %.2f",
                    Arms.LowerArm.getCurrentPosition(), Arms.UpperArm.getCurrentPosition());

            /* Display color sensors */
            telemetry.addLine("Color Sensors (red/blue)");
            telemetry.addData("   track left ","%3d %3d", robot.left_color.red(),robot.left_color.blue());
            telemetry.addData("   track right","%3d %3d", robot.right_color.red(),robot.right_color.blue());
            telemetry.addData("  ampere left ","%3d %3d", Ampere.ColorLeft.red(),Ampere.ColorLeft.blue());
            telemetry.addData("  ampere right","%3d %3d", Ampere.ColorRight.red(),Ampere.ColorRight.blue());

            Arms.Update(false);

            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
