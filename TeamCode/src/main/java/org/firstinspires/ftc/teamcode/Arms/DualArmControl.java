package org.firstinspires.ftc.teamcode.Arms;

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
        DualIndex = (DualIndex < 6) ? DualIndex + 1 : 6;
        if (DualIndex > 3) {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 2.0);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 2.0);
        } else {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 0.5);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 0.5);
        }
    }

    public void IncrementPosition() {
        DualIndex = (DualIndex < 3) ? DualIndex + 1 : 3;
        if (DualIndex > 3) {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 2.0);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 2.0);
        } else {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 0.5);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 0.5);
        }
    }

    public void DecrementPosition() {
        DualIndex = (DualIndex > 0) ? DualIndex - 1 : 0;
        if (DualIndex > 3) {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 2.0);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 2.0);
        } else {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 0.5);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 0.5);
        }
    }

    public void SetPosition(int index) {
        if (index<0) index = 0;
        if (index>3) index = 3;

        DualIndex = index;
        if (DualIndex>3) {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 2.0);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 2.0);
        } else {
            LowerArm.MoveToPosition(LOWERARM[DualIndex], 0.5);
            UpperArm.MoveToPosition(UPPERARM[DualIndex], 0.5);
        }
    }

    /* Call this method when you want to update the arm control (must be done on a periodic basis */
    public void Update(boolean active) {
        LowerArm.Update(0.0, active);
        UpperArm.Update(-LowerArm.getAngle(), active);
    }

}
