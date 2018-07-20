package org.firstinspires.ftc.teamcode.Utilities;

/**
 * Created by ablauch on 2/7/2018.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;


public class ImuWrapper {
    /* Declare private class objects
     */
    private Telemetry telemetry;            /* local copy of telemetry object from opmode class */
    private HardwareMap hardwareMap;        /* local copy of HardwareMap object from opmode class */
    private String name = "IMU Wrapper";

    private BNO055IMU imu;                  /* IMU Object */

    private double refAngle;                /* reference angle */


    /* Getter methods
     */
    public String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    private String formatDegrees(double degrees) {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }

    public Orientation getAngularOrientation(AxesReference reference, AxesOrder order, AngleUnit unit) {
        return imu.getAngularOrientation(reference, order, unit);
    }


    /* Constructor */
    public ImuWrapper() {
    }

    /* Initialization method - to be called before any other methods are used */
    public void initialize(LinearOpMode opMode) {
        /* Set local copies from opmode class */
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        telemetry.addLine(name + " initialize");

        BNO055IMU.Parameters imuParameters = new BNO055IMU.Parameters();
        imuParameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuParameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(imuParameters);
    }

    /* cleanup method - to be called when done with subassembly to 'turn off' everything */
    public void cleanup() {
        telemetry.addLine(name + " cleanup");
    }

    public double getAngleDifference(double angle, double angleref) {
        double anglediff;
        anglediff = angle - angleref;
        while (anglediff > 180.0) anglediff = anglediff - 360.0;
        while (anglediff < -180.0) anglediff = anglediff + 360.0;

        return anglediff;
    }

    public void setReferenceAngle() {
        Orientation angles;

        angles = getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        refAngle = angles.firstAngle;
    }

    public void setReferenceAngle(double angleref) {
        refAngle = angleref;
    }

    public double getReferenceAngle() {
        return refAngle;
    }

    public double getRelativeAngle() {
        Orientation angles;
        double angle;

        angles = getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        angle = angles.firstAngle - refAngle;
        if (angle > 180.0) angle = angle - 360.0;
        if (angle < -180.0) angle = angle + 360.0;

        return angle;
    }
}
