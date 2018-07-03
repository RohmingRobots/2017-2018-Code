package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by ablauch on 11/3/2017.
 *
 * this class detects the button press and release
 *
 * call the update method when you want to check for new press/release
 */

public class GamepadEdge {

    public class ButtonEdge {
        public boolean state = false;
        public boolean pressed = false;
        public boolean released = false;

        /* Call this method when you want to update the detection of the button edges pressed/released */
        public void UpdateEdge(boolean new_state) {
            /* check if button has just been pressed */
            if (new_state && !state)        pressed = true;
            else                            pressed = false;

            /* check if button has just been released */
            if (!new_state && state)        released = true;
            else                            released = false;

            state = new_state;
        }
    }

    private Gamepad mygamepad;

    public ButtonEdge dpad_up = new ButtonEdge();
    public ButtonEdge dpad_down = new ButtonEdge();
    public ButtonEdge dpad_left = new ButtonEdge();
    public ButtonEdge dpad_right = new ButtonEdge();
    public ButtonEdge a = new ButtonEdge();
    public ButtonEdge b = new ButtonEdge();
    public ButtonEdge x = new ButtonEdge();
    public ButtonEdge y = new ButtonEdge();
    public ButtonEdge guide = new ButtonEdge();
    public ButtonEdge start = new ButtonEdge();
    public ButtonEdge back = new ButtonEdge();
    public ButtonEdge left_bumper = new ButtonEdge();
    public ButtonEdge right_bumper = new ButtonEdge();
    public ButtonEdge left_stick_button = new ButtonEdge();
    public ButtonEdge right_stick_button = new ButtonEdge();

    /* Constructor */
    public GamepadEdge(Gamepad gp) {
        mygamepad = gp;
    }

    /* Call this method when you want to update the detection of the button edges pressed/released */
    public void UpdateEdge() {

        dpad_up.UpdateEdge(mygamepad.dpad_up);
        dpad_down.UpdateEdge(mygamepad.dpad_down);
        dpad_left.UpdateEdge(mygamepad.dpad_left);
        dpad_right.UpdateEdge(mygamepad.dpad_right);
        a.UpdateEdge(mygamepad.a);
        b.UpdateEdge(mygamepad.b);
        x.UpdateEdge(mygamepad.x);
        y.UpdateEdge(mygamepad.y);
        guide.UpdateEdge(mygamepad.guide);
        start.UpdateEdge(mygamepad.start);
        back.UpdateEdge(mygamepad.back);
        left_bumper.UpdateEdge(mygamepad.left_bumper);
        right_bumper.UpdateEdge(mygamepad.right_bumper);
        left_stick_button.UpdateEdge(mygamepad.left_stick_button);
        right_stick_button.UpdateEdge(mygamepad.right_stick_button);
    }
}
