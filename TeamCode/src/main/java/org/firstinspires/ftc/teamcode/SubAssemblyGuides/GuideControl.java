package org.firstinspires.ftc.teamcode.SubAssemblyGuides;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Utilities.EnumWrapper;
import org.firstinspires.ftc.teamcode.Utilities.ServoControl;

import java.util.EnumMap;


/* Sub Assembly Class
 */
public class GuideControl {
    /* Declare private class objects
     */
    private Telemetry telemetry;            /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap;        /* local copy of HardwareMap object from opmode class */
    private String name = "Guide Control";

    /* Servos
     * RG - right glyph guide
     * LG - left glyph guide
     */
    private Servo RG;
    private Servo LG;

    /* Setpoint enumeration maps */
    private EnumMap<GuideSetpoints, Double> MapLeftGuide;
    private EnumMap<GuideSetpoints, Double> MapRightGuide;

    /* Servo control for setpoint maps */
    public ServoControl<GuideSetpoints, EnumMap<GuideSetpoints, Double>> LeftServo;
    public ServoControl<GuideSetpoints, EnumMap<GuideSetpoints, Double>> RightServo;

    /* Declare public class objects
     */

    /* servo setpoints */
    public enum GuideSetpoints implements EnumWrapper<GuideSetpoints> {
        RETRACT, EXTEND;
    }


    /* Getter methods
     */


    /* Subassembly constructor */
    public GuideControl() {
    }

    /* default initialize is to set servos to initial setpoint */
    public void initialize(LinearOpMode opMode) {
        initialize(opMode, true);
    }

    /* Initialization method - to be called before any other methods are used
     */
    public void initialize(LinearOpMode opMode, boolean init_servos) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Create setpoint maps */
        MapLeftGuide = new EnumMap<GuideSetpoints, Double>(GuideSetpoints.class);
        MapRightGuide = new EnumMap<GuideSetpoints, Double>(GuideSetpoints.class);

        /* Assign setpoint values */
        MapLeftGuide.put(GuideSetpoints.RETRACT, 1.0);
        MapLeftGuide.put(GuideSetpoints.EXTEND, 0.2);

        MapRightGuide.put(GuideSetpoints.RETRACT, 0.94);
        MapRightGuide.put(GuideSetpoints.EXTEND, 0.14);

        /* Map hardware devices */
        LG = hardwareMap.servo.get("LG");
        RG = hardwareMap.servo.get("RG");
        // reverse those motors
        LG.setDirection(Servo.Direction.REVERSE);

        /* Create servo control objects and initialize positions */
        LeftServo = new ServoControl(LG, MapLeftGuide, GuideSetpoints.RETRACT, init_servos);
        RightServo = new ServoControl(RG, MapRightGuide, GuideSetpoints.RETRACT, init_servos);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }
}
