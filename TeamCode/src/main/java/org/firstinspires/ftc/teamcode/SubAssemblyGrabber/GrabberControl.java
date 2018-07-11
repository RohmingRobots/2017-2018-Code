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
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Grabber Control";

    /* GGR - gripper grabber right servo motor
     * GGL - gripper grabber right servo motor
     * Claw - top grabber
     */
    private Servo GGR = null;
    private Servo GGL = null;
    private Servo Claw = null;

    private EnumMap<GripSetpoints, Double> MapLeftGrip = new EnumMap<GripSetpoints, Double>(GripSetpoints.class);
    private EnumMap<GripSetpoints, Double> MapRightGrip = new EnumMap<GripSetpoints, Double>(GripSetpoints.class);
    public ServoControl<GripSetpoints, EnumMap<GripSetpoints, Double>> LeftServo = new ServoControl(GGL,MapLeftGrip);
    public ServoControl<GripSetpoints, EnumMap<GripSetpoints, Double>> RightServo = new ServoControl(GGR,MapRightGrip);

    private EnumMap<ClawSetpoints, Double> MapClaw = new EnumMap<ClawSetpoints, Double>(ClawSetpoints.class);
    public ServoControl<ClawSetpoints, EnumMap<ClawSetpoints, Double>> ClawServo = new ServoControl(Claw,MapClaw);

    /* Declare public class objects */
    public enum GripSetpoints implements EnumWrapper<GripSetpoints> {OPEN, CLOSE, PARTIAL;}
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

        /* Assign setpoint values
         */
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
        // set initial positions
        if (init_servos) {
            LeftServo.setSetpoint(GripSetpoints.OPEN);
            RightServo.setSetpoint(GripSetpoints.OPEN);
            // don't initialize position, let it set on arm
            //ClawServo.setSetpoint(ClawSetpoints.OPEN);
        }
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

}
