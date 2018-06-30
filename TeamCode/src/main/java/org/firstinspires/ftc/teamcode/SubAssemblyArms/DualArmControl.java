package org.firstinspires.ftc.teamcode.SubAssemblyArms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/* Sub Assembly Class
 */
public class DualArmControl {
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "Dual Arm Control";

    /************************* 0-3 glyph positions, 4-6 relic positions */
    private int DualIndex = 0;
    private double[] LOWERARM = {0.0, 0.0, 0.09, 0.3, 1.0, 1.5, 2.0};
    private double[] UPPERARM = {0.0, 0.2, 0.5, 0.73, 1.0, 1.5, 2.0};
    private double[] MOVETIME = {0.5, 0.5, 0.5, 0.5, 2.0, 2.0, 2.0};

    /* Declare public class objects */
    public SingleArmControl LowerArm = new SingleArmControl();
    public SingleArmControl UpperArm = new SingleArmControl();

    /* getter methods
     * */
    public int getDualIndex() {
        return DualIndex;
    }

    /* Subassembly constructor */
    public DualArmControl() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void Initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Map hardware devices */
        LowerArm.Initialize(opMode, false);
        UpperArm.Initialize(opMode, true);
    }

    /* Cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void Cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public void IncrementPositionExtended() {
        DualIndex = (DualIndex < LOWERARM.length - 1) ? DualIndex + 1 : LOWERARM.length - 1;
        LowerArm.MoveToPosition(LOWERARM[DualIndex], MOVETIME[DualIndex]);
        UpperArm.MoveToPosition(UPPERARM[DualIndex], MOVETIME[DualIndex]);
    }

    public void IncrementPosition() {
        DualIndex = (DualIndex < 3) ? DualIndex + 1 : 3;
        LowerArm.MoveToPosition(LOWERARM[DualIndex], MOVETIME[DualIndex]);
        UpperArm.MoveToPosition(UPPERARM[DualIndex], MOVETIME[DualIndex]);
    }

    public void DecrementPosition() {
        DualIndex = (DualIndex > 0) ? DualIndex - 1 : 0;
        LowerArm.MoveToPosition(LOWERARM[DualIndex], MOVETIME[DualIndex]);
        UpperArm.MoveToPosition(UPPERARM[DualIndex], MOVETIME[DualIndex]);
    }

    public void SetPosition(int index) {
        if (index<0) index = 0;
        if (index>3) index = 3;

        DualIndex = index;
        LowerArm.MoveToPosition(LOWERARM[DualIndex], MOVETIME[DualIndex]);
        UpperArm.MoveToPosition(UPPERARM[DualIndex], MOVETIME[DualIndex]);
    }

    /* Call this method when you want to update the arm control (must be done on a periodic basis */
    public void Update(boolean active) {
        LowerArm.Update(0.0, active);
        UpperArm.Update(-LowerArm.getAngle(), active);
    }
}
