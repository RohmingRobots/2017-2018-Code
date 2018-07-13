package org.firstinspires.ftc.teamcode.SubAssemblyGrabber;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Utilities.EnumWrapper;
import org.firstinspires.ftc.teamcode.Utilities.ServoControl;

import java.util.EnumMap;


/* Sub Assembly Class
 */
public class GrabberControl {
    /* Declare private class object */
    private Telemetry telemetry;            /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap;        /* local copy of HardwareMap object from opmode class */
    private String name = "Grabber Control";

    /* GGR - gripper grabber right servo motor
     * GGL - gripper grabber right servo motor
     * Claw - top grabber
     */
    private Servo GGR;
    private Servo GGL;
    private Servo Claw;

    private EnumMap<GripSetpoints, Double> MapLeftGrip;
    private EnumMap<GripSetpoints, Double> MapRightGrip;
    public ServoControl<GripSetpoints, EnumMap<GripSetpoints, Double>> LeftServo;
    public ServoControl<GripSetpoints, EnumMap<GripSetpoints, Double>> RightServo;

    private EnumMap<ClawSetpoints, Double> MapClaw;
    public ServoControl<ClawSetpoints, EnumMap<ClawSetpoints, Double>> ClawServo;

    /* Declare public class objects */
    public enum GripSetpoints implements EnumWrapper<GripSetpoints> {
        OPEN, CLOSE, PARTIAL;
    }

    public enum ClawSetpoints implements EnumWrapper<ClawSetpoints> {OPEN, CLOSE;}


    /* getter methods
     * */


    /* Subassembly constructor */
    public GrabberControl() {
    }

    /* default initialize is to set servos to initial setpoint */
    public void initialize(LinearOpMode opMode) {
        initialize(opMode, true);
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode, boolean init_servos) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* create setpoint maps */
        MapLeftGrip = new EnumMap<GripSetpoints, Double>(GripSetpoints.class);
        MapRightGrip = new EnumMap<GripSetpoints, Double>(GripSetpoints.class);
        MapClaw = new EnumMap<ClawSetpoints, Double>(ClawSetpoints.class);

        /* Assign setpoint values */
        MapLeftGrip.put(GripSetpoints.OPEN, 0.745);
        MapLeftGrip.put(GripSetpoints.CLOSE, 0.255);
        MapLeftGrip.put(GripSetpoints.PARTIAL, 0.375);

        MapRightGrip.put(GripSetpoints.OPEN, 0.44);
        MapRightGrip.put(GripSetpoints.CLOSE, 0.89);
        MapRightGrip.put(GripSetpoints.PARTIAL, 0.765);

        MapClaw.put(ClawSetpoints.OPEN, 0.70);
        MapClaw.put(ClawSetpoints.CLOSE, 0.0);

        /* Map hardware devices */
        GGR = hardwareMap.servo.get("GGR");
        GGL = hardwareMap.servo.get("GGL");
        Claw = hardwareMap.servo.get("Claw");

        /* Create servo control objects and initialize positions */
        LeftServo = new ServoControl(GGL, MapLeftGrip, GripSetpoints.OPEN);
        RightServo = new ServoControl(GGR, MapRightGrip, GripSetpoints.OPEN);
        // do not initialize claw position, let it set on arm
        ClawServo = new ServoControl(Claw, MapClaw, ClawSetpoints.OPEN, false);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

}
