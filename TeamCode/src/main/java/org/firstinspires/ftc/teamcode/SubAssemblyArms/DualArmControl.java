package org.firstinspires.ftc.teamcode.SubAssemblyArms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Utilities.EnumWrapper;

import java.util.EnumMap;


/* Sub Assembly Class
 */
public class DualArmControl {
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Dual Arm Control";

    private EnumMap<Setpoints, Double> MapLowerArm = new EnumMap<Setpoints, Double>(Setpoints.class);
    private EnumMap<Setpoints, Double> MapUpperArm = new EnumMap<Setpoints, Double>(Setpoints.class);
    private EnumMap<Setpoints, Double> MapMoveTime = new EnumMap<Setpoints, Double>(Setpoints.class);

    private Setpoints Setpoint = Setpoints.ROW1;

    /* Declare public class objects */
    public SingleArmControl LowerArm = new SingleArmControl();
    public SingleArmControl UpperArm = new SingleArmControl();

    public enum Setpoints implements EnumWrapper<Setpoints> {ROW1, ROW2, ROW3, ROW4;}


    /* getter methods
     * */


    /* Subassembly constructor */
    public DualArmControl() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Map hardware devices */
        LowerArm.initialize(opMode, false);
        UpperArm.initialize(opMode, true);

        /* Assign setpoint values
         */
        MapLowerArm.put(Setpoints.ROW1, 0.0);
        MapLowerArm.put(Setpoints.ROW2, 0.0);
        MapLowerArm.put(Setpoints.ROW3, 0.09);
        MapLowerArm.put(Setpoints.ROW4, 0.3);

        MapUpperArm.put(Setpoints.ROW1, 0.0);
        MapUpperArm.put(Setpoints.ROW2, 0.2);
        MapUpperArm.put(Setpoints.ROW3, 0.5);
        MapUpperArm.put(Setpoints.ROW4, 0.73);

        MapMoveTime.put(Setpoints.ROW1, 0.5);
        MapMoveTime.put(Setpoints.ROW2, 0.5);
        MapMoveTime.put(Setpoints.ROW3, 0.5);
        MapMoveTime.put(Setpoints.ROW4, 0.5);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");

        /* Clean up sub-assemblies */
        LowerArm.cleanup();
        UpperArm.cleanup();
        telemetry.update();
    }

    public void nextSetpoint() {
        Setpoint = Setpoint.getNext();
        LowerArm.moveToPosition(MapLowerArm.get(Setpoint), MapMoveTime.get(Setpoint));
        UpperArm.moveToPosition(MapUpperArm.get(Setpoint), MapMoveTime.get(Setpoint));
    }

    public void prevSetpoint() {
        Setpoint = Setpoint.getPrev();
        LowerArm.moveToPosition(MapLowerArm.get(Setpoint), MapMoveTime.get(Setpoint));
        UpperArm.moveToPosition(MapUpperArm.get(Setpoint), MapMoveTime.get(Setpoint));
    }

    public void setSetpoint(Setpoints setpt) {
        Setpoint = setpt;
        LowerArm.moveToPosition(MapLowerArm.get(Setpoint), MapMoveTime.get(Setpoint));
        UpperArm.moveToPosition(MapUpperArm.get(Setpoint), MapMoveTime.get(Setpoint));
    }

    /* Call this method when you want to update the arm control (must be done on a periodic basis */
    public void Update(boolean active) {
        LowerArm.Update(0.0, active);
        UpperArm.Update(-LowerArm.getAngle(), active);
    }
}
