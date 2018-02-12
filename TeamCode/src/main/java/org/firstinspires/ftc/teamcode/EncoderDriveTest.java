package org.firstinspires.ftc.teamcode;

/**
 * Created by Nicholas on 2/12/2018.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


//naming the teleop thing
@Autonomous(name="EncoderDriveTest", group ="Test")
public class EncoderDriveTest extends LinearOpMode {
    RobotConfig robot = new RobotConfig();
    private ElapsedTime runtime = new ElapsedTime();

    //mode 'stuff'
    //modes lists which steps and in what order to accomplish them
    int mode = 0;
    int[] modes = {0, 1, 0, 0, 0, 100};

    //time based variables
    double lastReset = 0;
    double now = 0;
    double currentDistance = 0;

    //clock reseter
    public void resetClock() {
        lastReset = runtime.seconds();
    }

    public void resetEncoders() {
        robot.FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        //declaring all my variables in one place for my sake
        final double MOVE_SPEED = 0.5;
        final double STRAFFE_SPEED = 0.75;
        final double ROTATE_SPEED = 0.4;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        telemetry.addData(">", "Press Play to start");
        telemetry.update();

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        resetClock();
        resetEncoders();

        // telling the code to run until you press that giant STOP button on RC
        // include opModeIsActive in all while loops so that STOP button terminates all actions
        while (opModeIsActive() && modes[mode] < 100) {

            //keeps now up to date
            now = runtime.seconds() - lastReset;

            currentDistance = -(robot.FL.getCurrentPosition() + robot.BL.getCurrentPosition() +
                               robot.FR.getCurrentPosition() + robot.BR.getCurrentPosition())/180;

            telemetry.addData("currentDistance", currentDistance);
            telemetry.update();

            switch (modes[mode]) {

                default:
                    telemetry.addLine("All done");
                    robot.MoveStop();
                    break;

                /* wait one second */
                case 0:
                    if (now > 1.0) {
                        mode++;
                        resetClock();
                        resetEncoders();
                        robot.MoveStop();
                    }
                    break;

                /* move forward 12 inches */
                case 1:
                    robot.MoveForward(MOVE_SPEED);
                    if (currentDistance > 12) {
                        mode++;
                        resetClock();
                        resetEncoders();
                        robot.MoveStop();
                    }
                    break;

            }  // end of switch
        }
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}
