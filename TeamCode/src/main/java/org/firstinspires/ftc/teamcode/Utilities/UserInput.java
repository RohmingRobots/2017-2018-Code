package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class UserInput {
    /* Declare private class objects
     */
    private Telemetry telemetry;            /* local copy of telemetry object from opmode class */
    private String name = "User Input";

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    /* Constructor */
    public UserInput() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;

        telemetry.addLine(name + " initialize");

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(opMode.gamepad1);
        egamepad2 = new GamepadEdge(opMode.gamepad2);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public boolean getRedBlue(String prompt) {
        boolean isRed = false;

        telemetry.addLine(prompt);
        telemetry.addLine("[X = blue, B = red]");
        telemetry.update();
        do {
            egamepad1.updateEdge();
        } while (!egamepad1.x.pressed && !egamepad1.b.pressed);
        if (egamepad1.b.pressed)
            isRed = true;
        egamepad1.updateEdge();
        return isRed;
    }

    public boolean getYesNo(String prompt) {
        boolean isYes = false;

        telemetry.addLine(prompt);
        telemetry.addLine("[A = yes, B = no]");
        telemetry.update();
        do {
            egamepad1.updateEdge();
        } while (!egamepad1.a.pressed && !egamepad1.b.pressed);
        if (egamepad1.a.pressed)
            isYes = true;
        egamepad1.updateEdge();
        return isYes;
    }

    public boolean getLeftRight(String prompt) {
        boolean isLeft = false;

        telemetry.addLine(prompt);
        telemetry.addLine("[X = left, B = right]");
        telemetry.update();
        do {
            egamepad1.updateEdge();
        } while (!egamepad1.x.pressed && !egamepad1.b.pressed);
        if (egamepad1.x.pressed)
            isLeft = true;
        egamepad1.updateEdge();
        return isLeft;
    }
}
