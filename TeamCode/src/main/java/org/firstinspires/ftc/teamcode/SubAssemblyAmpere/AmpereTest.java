package org.firstinspires.ftc.teamcode.SubAssemblyAmpere;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Utilities.GamepadEdge;


//naming the teleop thing
@TeleOp(name="Ampere Test", group="Test")
public class AmpereTest extends LinearOpMode {

    /* Sub Assemblies
     */
    AmpereControl Ampere = new AmpereControl();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    //ampere color sensor variables
    int leftamperered = 0;
    int leftampereblue = 0;
    int rightamperered = 0;
    int rightampereblue = 0;
    boolean leftampere = false;
    boolean rightampere = false;

    @Override
    public void runOpMode() throws InterruptedException {
        ElapsedTime OurTime = new ElapsedTime();
        double AMPERE_POWER = 0.8;
        double increment;

        telemetry.addLine("Ampere Test");
        telemetry.addLine("gamepad 1");
        telemetry.addLine("d-up   - extend side arms");
        telemetry.addLine("d-down - retract side arms");
        telemetry.addLine("x      - cycle through left flipper");
        telemetry.addLine("b      - cycle through right flipper");
        telemetry.addLine("stick button  - increment flipper");
        telemetry.addLine("bumper button - decrement flipper");
        telemetry.addLine("y      - autonomous test");

        /* Initialize sub assemblies
         */
        Ampere.Initialize(this);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        increment = 0.05;

        //turns ampere LEDs om
        Ampere.ColorLeft.enableLed(true);
        Ampere.ColorRight.enableLed(true);

        //waits for that giant PLAY button to be pressed on RC
        telemetry.update();

        waitForStart();

        OurTime.reset();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            /* update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            // side arms
            if (egamepad1.dpad_down.pressed) {
                // retract
                Ampere.moveWinches(-AMPERE_POWER);
            } else if (egamepad1.dpad_down.released){
                // stop
                Ampere.moveWinches(0.0);
            }
            if (egamepad1.dpad_up.pressed) {
                // extend
                Ampere.moveWinches(AMPERE_POWER);
            } else if (egamepad1.dpad_up.released){
                // stop
                Ampere.moveWinches(0.0);
            }

            // flippers
            if (egamepad1.x.released) {
                Ampere.LeftFlipperServo.nextSetpoint();
            }
            if (egamepad1.left_stick_button.released) {
                Ampere.LeftFlipperServo.incrementPosition(increment);
            }
            if (egamepad1.left_bumper.released) {
                Ampere.LeftFlipperServo.incrementPosition(-increment);
            }
            if (egamepad1.b.released) {
                Ampere.RightFlipperServo.nextSetpoint();
            }
            if (egamepad1.right_stick_button.released) {
                Ampere.RightFlipperServo.incrementPosition(increment);
            }
            if (egamepad1.right_bumper.released) {
                Ampere.RightFlipperServo.incrementPosition(-increment);
            }

            telemetry.addLine("Flippers (left/right)");
            telemetry.addData("  positions","%.1f %.1f",Ampere.LeftFlipperServo.getPosition(),Ampere.RightFlipperServo.getPosition());
            telemetry.addData("  setpoints","%s %s",Ampere.LeftFlipperServo.getSetpoint(),Ampere.RightFlipperServo.getSetpoint());
            telemetry.addLine("Color Sensors (red/blue)");
            telemetry.addData("  ampere left ","%3d %3d", Ampere.ColorLeft.red(),Ampere.ColorLeft.blue());
            telemetry.addData("  ampere right","%3d %3d", Ampere.ColorRight.red(),Ampere.ColorRight.blue());
            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
