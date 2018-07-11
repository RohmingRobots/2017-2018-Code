package org.firstinspires.ftc.teamcode.SubAssemblyGuides;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Utilities.EnumWrapper;
import org.firstinspires.ftc.teamcode.Utilities.ServoControl;

import java.util.EnumMap;


/* Sub Assembly Class
 */
public class GuideControl {
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Guide Control";

    /* RG - right glyph guide
     * LG - left glyph guide
     */
    private Servo RG = null;
    private Servo LG = null;

    private EnumMap<GuideSetpoints, Double> MapLeftGuide = new EnumMap<GuideSetpoints, Double>(GuideSetpoints.class);
    private EnumMap<GuideSetpoints, Double> MapRightGuide = new EnumMap<GuideSetpoints, Double>(GuideSetpoints.class);
    public ServoControl<GuideSetpoints, EnumMap<GuideSetpoints, Double>> LeftServo = new ServoControl(LG,MapLeftGuide);
    public ServoControl<GuideSetpoints, EnumMap<GuideSetpoints, Double>> RightServo = new ServoControl(RG,MapRightGuide);

    /* Declare public class objects */
    public enum GuideSetpoints implements EnumWrapper<GuideSetpoints> {RETRACT, EXTEND;}


    /* getter methods
     * */


    /* Subassembly constructor */
    public GuideControl() {
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
        MapLeftGuide.put(GuideSetpoints.RETRACT, 1.0);
        MapLeftGuide.put(GuideSetpoints.EXTEND, 0.2);

        MapRightGuide.put(GuideSetpoints.RETRACT, 0.94);
        MapRightGuide.put(GuideSetpoints.EXTEND, 0.14);

        /* Map hardware devices */
        LG = hardwareMap.servo.get("LG");
        RG = hardwareMap.servo.get("RG");
        // reverse those motors
        LG.setDirection(Servo.Direction.REVERSE);
        // set initial positions
        if (init_servos) {
            LeftServo.setSetpoint(GuideSetpoints.RETRACT);
            RightServo.setSetpoint(GuideSetpoints.RETRACT);
        }
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

}
