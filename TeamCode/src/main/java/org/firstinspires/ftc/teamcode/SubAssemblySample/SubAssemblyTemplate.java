package org.firstinspires.ftc.teamcode.SubAssemblySample;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/* Sub Assembly Class
 */
public class SubAssemblyTemplate {
    /* Declare private class object */
    private Telemetry telemetry = null;         /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap = null;     /* local copy of HardwareMap object from opmode class */
    private String name = "SubAssemblyName";

    /* Declare public class object */
    public VoltageSensor BatteryLevel = null;

    /* Subassembly constructor */
    public SubAssemblyTemplate() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        /* Map hardware devices */
        BatteryLevel = hardwareMap.voltageSensor.get("Lower hub 3");
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    /* Subassembly methods */
    public void test() {
        telemetry.addLine(name + " test");
        telemetry.addData("Battery level = %.1f V", BatteryLevel);
    }

}
