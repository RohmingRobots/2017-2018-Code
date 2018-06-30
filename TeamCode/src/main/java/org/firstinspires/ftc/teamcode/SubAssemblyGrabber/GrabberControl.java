package org.firstinspires.ftc.teamcode.SubAssemblyGrabber;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/* Sub Assembly Class
 */
public class GrabberControl {
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Grabber Control";

    /* GGR - gripper grabber right servo motor
     * GGL - gripper grabber right servo motor
     * Claw - top grabber
     * RG - right glyph guide
     * LG - left glyph guide
     */
    private Servo GGR = null;
    private Servo GGL = null;
    private Servo Claw = null;
    private Servo RG = null;
    private Servo LG = null;

    /* open full, closed full, partial open */
    private double[] GRABBER_LEFT = {0.745, .255, .375};
    private double[] GRABBER_RIGHT = {0.44, .89, .765};
    private double[] CLAW = {0.7, 0.0};
    private double[] GUIDESLEFT = {1, 0.2};
    private double[] GUIDESRIGHT = {0.94, 0.14};

    private int GuideIndex = 0;
    private int ClawIndex = 0;
    private int LeftIndex = 0;
    private int RightIndex = 0;

    /* Declare public class objects */

    /* getter methods
     * */

    /* Subassembly constructor */
    public GrabberControl() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void Initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Map hardware devices */
        GGR = hardwareMap.servo.get("GGR");
        GGL = hardwareMap.servo.get("GGL");
        Claw = hardwareMap.servo.get("Claw");
        LG = hardwareMap.servo.get("LG");
        RG = hardwareMap.servo.get("RG");
        // reverse those motors
        LG.setDirection(Servo.Direction.REVERSE);
        // set initial positions
        GGL.setPosition(GRABBER_LEFT[LeftIndex]);
        GGR.setPosition(GRABBER_RIGHT[RightIndex]);
        // don't initialize position, let it set on arm        Claw.setPosition(CLAW[0]);
        LG.setPosition(GUIDESLEFT[GuideIndex]);
        RG.setPosition(GUIDESRIGHT[GuideIndex]);
    }

    /* Cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void Cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public void ToggleGuides() {
        GuideIndex = (GuideIndex < GUIDESLEFT.length - 1) ? GuideIndex + 1 : 0;
        LG.setPosition(GUIDESLEFT[GuideIndex]);
        RG.setPosition(GUIDESRIGHT[GuideIndex]);
    }

    public void CloseGuides() {
        GuideIndex = 0;
        LG.setPosition(GUIDESLEFT[GuideIndex]);
        RG.setPosition(GUIDESRIGHT[GuideIndex]);
    }

    public void OpenGuides() {
        GuideIndex = 1;
        LG.setPosition(GUIDESLEFT[GuideIndex]);
        RG.setPosition(GUIDESRIGHT[GuideIndex]);
    }


    public void IncrementLeft() {
        LeftIndex = (LeftIndex < GRABBER_LEFT.length - 1) ? LeftIndex + 1 : 0;
        GGL.setPosition(GRABBER_LEFT[LeftIndex]);
    }

    public void IncrementRight() {
        RightIndex = (RightIndex < GRABBER_RIGHT.length - 1) ? RightIndex + 1 : 0;
        GGR.setPosition(GRABBER_RIGHT[RightIndex]);
    }

    public void DecrementLeft() {
        LeftIndex = (LeftIndex > 0) ? LeftIndex - 1 : 0;
        GGL.setPosition(GRABBER_LEFT[LeftIndex]);
    }

    public void DecrementRight() {
        RightIndex = (RightIndex > 0) ? RightIndex - 1 : 0;
        GGR.setPosition(GRABBER_RIGHT[RightIndex]);
    }

    public void SetPosition(int index) {
        if (index < 0) index = 0;
        if (index > GRABBER_RIGHT.length - 1) index = GRABBER_RIGHT.length - 1;

        LeftIndex = index;
        RightIndex = index;
        GGL.setPosition(GRABBER_LEFT[LeftIndex]);
        GGR.setPosition(GRABBER_RIGHT[RightIndex]);
    }

    public void ToggleClaw() {
        ClawIndex = (ClawIndex < CLAW.length - 1) ? ClawIndex + 1 : 0;
        Claw.setPosition(CLAW[ClawIndex]);
    }
}
