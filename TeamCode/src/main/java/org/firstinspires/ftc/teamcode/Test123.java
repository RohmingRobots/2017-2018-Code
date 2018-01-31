package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/**
 * Created by Nicholas on 1/24/2018.
 */

@TeleOp(name="IMUTest", group="Test")
public class Test123 extends LinearOpMode {
    RobotConfig robot = new RobotConfig();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;


    BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;

    @Override
    public void runOpMode() throws InterruptedException {

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        telemetry.update();
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        gravity = imu.getGravity();

        //declaring all my variables in one place for my sake
        double front_right;
        double front_left;
        double back_left;
        double back_right;
        double speed = 1;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */

        robot.init(hardwareMap);

        /* Instantiate extended gamepad */
        egamepad1 = new GamepadEdge(gamepad1);
        egamepad2 = new GamepadEdge(gamepad2);

        boolean updpad;
        boolean downdpad;
        boolean leftdpad;
        boolean rightdpad;
        double reverse = 1;

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {
            //and now, the fun stuff

            /* Update extended gamepad */
            egamepad1.UpdateEdge();
            egamepad2.UpdateEdge();

            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            gravity = imu.getGravity();

            boolean abutton = egamepad1.a.released;

            //adds a lil' version thing to the telemetry so you know you're using the right version
            telemetry.addData("Version", "2.2");
            telemetry.addData("BRmotor", robot.BR.getPower());
            telemetry.addData("BLmotor", robot.BL.getPower());
            telemetry.addData("FLmotor", robot.FL.getPower());
            telemetry.addData("FRmotor", robot.FR.getPower());
            telemetry.addData("Speed", speed);
            telemetry.addData("IMUangle", angles);
            telemetry.addData("IMUgravity", gravity);
            telemetry.update();

            //when a button is just released, multiply the speed by -1 so it's reverse
            if (abutton) {
                reverse *= -1;
            }

            //change that speed by those bumpers
            if (gamepad1.right_bumper) {
                speed += 0.25;
            }
            if (gamepad1.left_bumper) {
                speed -= 0.25;
            }
            //if the speed is at the min/max value set it to NOT min/max so boom it cant go over
            if (speed < 0) {
                speed = 0;
            }
            if (speed > 3) {
                speed = 3;
            }

            // using the right joystick's x axis to rotate left and right
            front_right = -gamepad1.right_stick_x * 2;
            front_left = gamepad1.right_stick_x * 2;
            back_left = gamepad1.right_stick_x * 2;
            back_right = -gamepad1.right_stick_x * 2;

            // using the left joystick's y axis to move forward and backwards
            front_right -= gamepad1.left_stick_y;
            front_left -= gamepad1.left_stick_y;
            back_left -= gamepad1.left_stick_y;
            back_right -= gamepad1.left_stick_y;

            // using the left joystick's x axis to strafe left and right
            front_right += -gamepad1.left_stick_x * 2;
            front_left += gamepad1.left_stick_x * 2;
            back_left += -gamepad1.left_stick_x * 2;
            back_right += gamepad1.left_stick_x * 2;

            //takes all those values, divides
            front_right = front_right / 3.414 * speed * reverse;
            front_left = front_left / 3.414 * speed * reverse;
            back_left = back_left / 3.414 * speed * reverse;
            back_right = back_right / 3.414 * speed * reverse;

        /*for later- joysticks have a max input of 1 or -1. divide it by 3,
          which leaves us with a max input of 0.333333. motors have a max input
           of one. i'm not quite sure if this is perfectly true because i havent tested,
           but that should allow us to have a max speed var of 3. if you were to
           have max inputs on everything, you'd have 1 / 3 * 1 * 1, which
           equals 0.33. so the max speed should be set to 3, leaving us with
           1 / 3 * 3 * 1, equaling out to 1, our max value.
        */

            if (gamepad1.dpad_left){
                robot.MoveLeft(speed);
            } else if (gamepad1.dpad_right){
                robot.MoveRight(speed);
            } else if (gamepad1.dpad_up){
                robot.MoveForward(speed);
            } else if (gamepad1.dpad_down){
                robot.MoveBackward(speed);
            } else {
                //takes all those values, divides
                robot.FR.setPower(front_right);
                robot.FL.setPower(front_left);
                robot.BL.setPower(back_left);
                robot.BR.setPower(back_right);
            }

            //let the robot have a little rest, sleep is healthy
            sleep(40);
        }
    }
}
