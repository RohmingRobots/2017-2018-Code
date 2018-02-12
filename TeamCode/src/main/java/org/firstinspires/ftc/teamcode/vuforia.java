package org.firstinspires.ftc.teamcode;
/* version history 2.0
     -10/21/17 (1.0) working and good
     -10/23/17 (1.3) adding speed changing by lbumper/ltrigger
     -10/30/17 (1.5) dpad control
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/********** vv copy this 1/6 vv **********/
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/********** ^^ copy this ^^ **********/

//naming the teleop thing
@TeleOp(name="Vuforia Test", group="Test")
public class vuforia extends LinearOpMode {

    /********** vv copy this 2/6 vv **********/
    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;
    /********** ^^ copy this ^^ **********/

    RobotConfig robot = new RobotConfig();

    /* Declare extended gamepad */
    GamepadEdge egamepad1;
    GamepadEdge egamepad2;

    @Override
    public void runOpMode() throws InterruptedException {

        /********** vv copy this 3/6 vv **********/

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AQepDXf/////AAAAGcvzfI2nd0MHnzIGZ7JtquJk3Yx64l7jwu6XImRkNmBkhjVdVcI47QZ7xQq0PvugAb3+ppJxL4n+pNcnt1+PYpQHVETBEPk5WkofitFuYL8zzXEbs7uLY0dMUepnOiJcLSiVISKWWDyc8BJkKcK3a/KmB2sHaE1Lp2LJ+skW43+pYeqtgJE8o8xStowxPJB0OaSFXXw5dGUurK+ykmPam5oE+t+6hi9o/pO1EOHZFoqKl6tj/wsdu9+3I4lqGMsRutKH6s1rKLfip8s3MdlxqnlRKFmMDFewprELOwm+zpjmrJ1cqdlzzWQ6i/EMOzhcOzrPmH3JiH4CocA/Kcck12IuqvN4l6iAIjntb8b0G8zL";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK  ;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        /********** ^^ copy this ^^ **********/



        telemetry.addData(">", "Press Play to start");
        telemetry.update();
                waitForStart();



        /********** vv copy this 4/6 vv **********/
        relicTrackables.activate();
        /********** ^^ copy this ^^ **********/


        //telling the code to run until you press that giant STOP button on RC
        while (opModeIsActive()) {

            /********** vv copy this 5/6 vv **********/
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

                /* Found an instance of the template. In the actual game, you will probably
                 * loop until this condition occurs, then move on to act accordingly depending
                 * on which VuMark was visible. */
                telemetry.addData("VuMark", "%s visible", vuMark);

                /* For fun, we also exhibit the navigational pose. In the Relic Recovery game,
                 * it is perhaps unlikely that you will actually need to act on this pose information, but
                 * we illustrate it nevertheless, for completeness. */
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
                telemetry.addData("Pose", format(pose));
                /* We further illustrate how to decompose the pose into useful rotational and
                 * translational components */
                if (pose != null) {
                    VectorF trans = pose.getTranslation();
                    Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                    // Extract the X, Y, and Z components of the offset of the target relative to the robot
                    double tX = trans.get(0);
                    double tY = trans.get(1);
                    double tZ = trans.get(2);

                    // Extract the rotational components of the target relative to the robot
                    double rX = rot.firstAngle;
                    double rY = rot.secondAngle;
                    double rZ = rot.thirdAngle;
                }
            }
            else {
                telemetry.addData("VuMark", "not visible");
            }
            /********** ^^ copy this ^^ **********/



            telemetry.update();


        }



    }

    /********** vv copy this 6/6 vv **********/


    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
    /********** ^^ copy this ^^ **********/
}



    // Movement code
    /*private void Move(DpadDirection buttonDirection, double speed) {
        switch (buttonDirection) {
            case Up:
                robot.FR.setPower(speed);
                robot.FL.setPower(speed);
                robot.BL.setPower(speed);
                robot.BR.setPower(speed);
                break;
            case Down:
                robot.FR.setPower(-speed);
                robot.FL.setPower(-speed);
                robot.BL.setPower(-speed);
                robot.BR.setPower(-speed);
                break;
            case Left:
                robot.FR.setPower(speed);
                robot.FL.setPower(-speed);
                robot.BL.setPower(speed);
                robot.BR.setPower(-speed);
                break;
            case Right:
                robot.FR.setPower(-speed);
                robot.FL.setPower(speed);
                robot.BL.setPower(-speed);
                robot.BR.setPower(speed);
                break;
            case None:
                // do nothing
                break;
        }
    }

    private DpadDirection GetDpadDirection(Gamepad gamepad) {
        if (gamepad.dpad_up) {
            return DpadDirection.Up;
        } else if (gamepad.dpad_down) {
            return DpadDirection.Down;
        } else if (gamepad.dpad_left) {
            return DpadDirection.Left;
        } else if (gamepad.dpad_right) {
            return DpadDirection.Right;
        } else {
            return DpadDirection.None;
        }
    }

    private enum DpadDirection {
        None,
        Up,
        Down,
        Left,
        Right
    }
} */


