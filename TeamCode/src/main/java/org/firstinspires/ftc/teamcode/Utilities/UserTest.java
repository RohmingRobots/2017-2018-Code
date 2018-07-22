package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Test opmode for GamepadWrapper and UserControl.
 */

@TeleOp(name="User Test", group="Test")
public class UserTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("User Test");

        /* initialize sub assemblies
         */
        UserControl User = new UserControl(this);

        /* Instantiate extended gamepad */
        GamepadWrapper egamepad1 = new GamepadWrapper(gamepad1);
        GamepadWrapper egamepad2 = new GamepadWrapper(gamepad2);

        boolean isred = User.getRedBlue("Test Red/Blue:");
        boolean isyes = User.getYesNo("Test Yes/No:");
        boolean isleft = User.getLeftRight("Test Left/Right:");
        UserControl.DPAD dpad = User.getDPad("Test DPad");

        telemetry.addData("Answer = ", (isred) ? "Red" : "Blue");
        telemetry.addData("Answer = ", (isyes) ? "Yes" : "No");
        telemetry.addData("Answer = ", (isleft) ? "Left" : "Right");
        telemetry.addData("Answer = ", dpad);

        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            /* Update extended gamepad */
            egamepad1.updateEdge();
            egamepad2.updateEdge();

            /* Display color sensors */
            telemetry.addLine("Gamepad1");
            telemetry.addData(" button A","%b %b %b", egamepad1.a.state,egamepad1.a.pressed,egamepad1.a.released);
            telemetry.addData(" button B","%b %b %b", egamepad1.b.state,egamepad1.b.pressed,egamepad1.b.released);
            telemetry.addData(" button X","%b %b %b", egamepad1.x.state,egamepad1.x.pressed,egamepad1.x.released);
            telemetry.addData(" button Y","%b %b %b", egamepad1.y.state,egamepad1.y.pressed,egamepad1.y.released);
            telemetry.addData(" dpad up   ","%b %b %b", egamepad1.dpad_up.state,egamepad1.dpad_up.pressed,egamepad1.dpad_up.released);
            telemetry.addData(" dpad down ","%b %b %b", egamepad1.dpad_down.state,egamepad1.dpad_down.pressed,egamepad1.dpad_down.released);
            telemetry.addData(" dpad left ","%b %b %b", egamepad1.dpad_left.state,egamepad1.dpad_left.pressed,egamepad1.dpad_left.released);
            telemetry.addData(" dpad right","%b %b %b", egamepad1.dpad_right.state,egamepad1.dpad_right.pressed,egamepad1.dpad_right.released);
            telemetry.addData(" bumper left ","%b %b %b", egamepad1.left_bumper.state,egamepad1.left_bumper.pressed,egamepad1.left_bumper.released);
            telemetry.addData(" bumper right","%b %b %b", egamepad1.right_bumper.state,egamepad1.right_bumper.pressed,egamepad1.right_bumper.released);
            telemetry.addData(" stick left  ","%b %b %b", egamepad1.left_stick_button.state,egamepad1.left_stick_button.pressed,egamepad1.left_stick_button.released);
            telemetry.addData(" stick right ","%b %b %b", egamepad1.right_stick_button.state,egamepad1.right_stick_button.pressed,egamepad1.right_stick_button.released);
            telemetry.addData(" trigger left ","%b %b %b", egamepad1.left_trigger.state,egamepad1.left_trigger.pressed,egamepad1.left_trigger.released);
            telemetry.addData(" trigger right","%b %b %b", egamepad1.right_trigger.state,egamepad1.right_trigger.pressed,egamepad1.right_trigger.released);
            telemetry.addData(" guide","%b %b %b", egamepad1.guide.state,egamepad1.guide.pressed,egamepad1.guide.released);
            telemetry.addData(" start","%b %b %b", egamepad1.start.state,egamepad1.start.pressed,egamepad1.start.released);
            telemetry.addData(" back ","%b %b %b", egamepad1.back.state,egamepad1.back.pressed,egamepad1.back.released);

            telemetry.addData(" joystick left ","%.2f %.2f", gamepad1.left_stick_x,gamepad1.left_stick_y);
            telemetry.addData(" joystick right","%.2f %.2f", gamepad1.right_stick_x,gamepad1.right_stick_y);
            telemetry.addData(" triggers      ","%.2f %.2f", gamepad1.left_trigger,gamepad1.right_trigger);

            telemetry.update();

            //let the robot have a little rest, sleep is healthy
            sleep(500);
        }
    }
}
