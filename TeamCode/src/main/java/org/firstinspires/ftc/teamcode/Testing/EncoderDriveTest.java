package org.firstinspires.ftc.teamcode.Testing;

/**
 * Created by Nicholas on 2/12/2018.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;


//naming the teleop thing
@Autonomous(name="EncoderDriveTest", group ="Test")
public class EncoderDriveTest extends LinearOpMode {

    /* Sub Assemblies
     */
    DriveControl Drive = new DriveControl();

    private ElapsedTime runtime = new ElapsedTime();

    //mode 'stuff'
    //modes lists which steps and in what order to accomplish them
    int mode = 0;
    int[] modes = {0, 1, 2, 3, 2, 100};

    //time based variables
    double lastReset = 0;
    double now = 0;
    double currentDistance = 0;

    //clock reseter
    public void resetClock() {
        lastReset = runtime.seconds();
    }

    public void resetEncoders() {
/*        robot.FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        */
    }

    @Override
    public void runOpMode() throws InterruptedException {

        /* Initialize sub assemblies
         */
        Drive.Initialize(this);

        double voltage = Drive.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        //declaring all my variables in one place for my sake
        final double MOVE_SPEED = 0.5 + ((13.2-voltage)/12);
        final double STRAFFE_SPEED = 0.75 + ((13.2-voltage)/12);
        final double ROTATE_SPEED = 0.4 + ((13.2-voltage)/12);

/*        robot.FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
*/

        telemetry.addData("Move Speed", MOVE_SPEED);
        telemetry.addData("Straffe Speed", STRAFFE_SPEED);
        telemetry.addData("Rotate Speed", ROTATE_SPEED);
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

/*            currentDistance = -(robot.FL.getCurrentPosition() + robot.BL.getCurrentPosition() +
                               robot.FR.getCurrentPosition() + robot.BR.getCurrentPosition())/160;
*/

            telemetry.addData("currentDistance", currentDistance);
            telemetry.update();

            switch (modes[mode]) {

                default:
                    telemetry.addLine("All done");
                    Drive.MoveStop();
                    break;

                /* wait one second */
                case 0:
                    if (now > 1.0) {
                        mode++;
                        resetClock();
                        resetEncoders();
                        Drive.MoveStop();
                    }
                    break;

                /* move forward 12 inches */
                case 1:
                    Drive.MoveForward(MOVE_SPEED);
                    if (currentDistance > 12-6) {
                        mode++;
                        resetClock();
                        //resetEncoders();
                        Drive.MoveStop();
                    }
                    break;

                case 2:
                    if (now > 5.0) {
                        mode++;
                        resetClock();
                        resetEncoders();
                        Drive.MoveStop();
                    }
                    break;

                case 3:
                    Drive.MoveForward(MOVE_SPEED);
                    if (currentDistance > 30-6) {
                        mode++;
                        resetClock();
                        //resetEncoders();
                        Drive.MoveStop();
                    }
                    break;

            }  // end of switch
        }
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}
