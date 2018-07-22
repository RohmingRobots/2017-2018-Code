package org.firstinspires.ftc.teamcode;

/**
 * Created by Nicholas on 1/17/2018.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.SubAssemblyAmpere.AmpereControl;
import org.firstinspires.ftc.teamcode.SubAssemblyArms.DualArmControl;
import org.firstinspires.ftc.teamcode.SubAssemblyDrive.DriveControl;
import org.firstinspires.ftc.teamcode.SubAssemblyGrabber.GrabberControl;
import org.firstinspires.ftc.teamcode.Utilities.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Utilities.ImuControl;
import org.firstinspires.ftc.teamcode.Utilities.UserControl;
import org.firstinspires.ftc.teamcode.Utilities.VuforiaControl;


//naming the teleop thing
@Autonomous(name = "AllAuto", group = "Drive")
public class AllAuto extends LinearOpMode {

    /* Sub Assemblies
     */
    DriveControl Drive = null;
    ImuControl Imu = null;
    UserControl User = null;

    private ElapsedTime runtime = new ElapsedTime();

    public boolean inputPosition() {
        //input the position
        boolean left;

        left = User.getLeftRight("Select Position");

        //position selection
        if ((left && redteam) || (!left && !redteam))
            return true;
        return false;
    }


    public void displaySelections() {
        //displays the selected color and position
        telemetry.addData("Version", "Worlds 4-13-2018");
        telemetry.addData("Status", "Initialized");
        if (FI)
            telemetry.addData("Position", "FI");
        else
            telemetry.addData("Position", "BI");
        if (redteam)
            telemetry.addData("Team Color", "RED");
        else
            telemetry.addData("Team Color", "BLUE");
        //says it is ready to start so it adds all the prestart telemetry together
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
    }

    /* turns to the specified angle */
    public void turn2angle(int angle) {
        double angle2turn;

        angle2turn = Imu.getAngleDifference(angle, turnAngle);

        //turns until it gets within a certain distance based on how far it has been turning
        if (angle2turn > 15) {
            Drive.rotateRight(ROTATE_SPEED);
            if (now > 5.0) {
                Drive.rotateRight(ROTATE_SPEED * 1.5);
            }
        } else if (angle2turn < -15) {
            Drive.rotateLeft(ROTATE_SPEED);
            if (now > 5.0) {
                Drive.rotateLeft(ROTATE_SPEED * 1.5);
            } else if (now > 5.5) {
                resetClock();
            }
        }
        //at this point, it is within 10 degrees, so it tries to narrow down further
        else {
            /*
            if (step == 1){
                robot.moveStop();
                step++;
            }

            robot.RotateRight(angle2turn*0.1);
            if (2.5 > Math.abs(angle2turn)){
                mode++;
                resetClock();
                robot.moveStop();
                step = 0;
            }
            */
            mode++;
            resetClock();
            Drive.moveStop();
            step = 0;
        }
    }

    public void resetClock() {
        //resets the clock
        lastReset = runtime.seconds();
    }

    /* Variables: declaring all my variables in one place for my sake */

    //start position variables
    boolean FI;
    boolean redteam;
    boolean do_jewels;

    //time based variables
    double lastReset = 0;
    double now = 0;

    //speed variables
    double MOVE_SPEED = 0.5;
    double STRAFFE_SPEED = 0.75;
    double ROTATE_SPEED = 0.5;
    double AMPERE_POWER = 0.8;

    //turning variables
    double turnAngle;       //actual angle relative to where we started used for turning

    //navigation color sensor variables
    int leftcolorblue = 0;
    int rightcolorblue = 0;
    boolean leftcolor = false;
    boolean rightcolor = false;

    //ampere color sensor variables
    int leftamperered = 0;
    int leftampereblue = 0;
    int rightamperered = 0;
    int rightampereblue = 0;
    boolean leftampere = false;
    boolean rightampere = false;

    /* Mode 'stuff' */
    //modes lists which steps and in what order to accomplish them
    int home_sequence = 0;
    int step = 0;
    int mode = 0;
    int[] modes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 100};
    /* List of what the mode numbers do so you don't have to hunt them down elsewhere
      1: Vumark detection + jewel selection
      2: Back off balancing stone
      3: Turn to -90 (red) or 90 (blue)
      4: Strafe right (red) or left (blue) to align on balancing stone (BI)
      5: Move forward for time (FI) or until color (BI)
      6: Turn to 0 (FI)
      7: Tirangulate 'n strafe (FI) or strafe right (RABI) or left till column (BABI)
      8: Drive into cryptobox
      9: Back up from cryptobox
    100: End
    */
    
    /* Troublshooting and Hotwiring modes
    -2 : Grab glyph
    -1 : Release glyph
     0 : Wait 1 sec
     */
    /* in case I want to modify it more easily for one position */
    //modes is set based on inputted color and position
    //int [] modesRAFI = {};
    //int [] modesBAFI = {};
    //int [] modesRABI = {};
    //int [] modesBABI = {};

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("All Auto");

        /* initialize sub assemblies
         */
        DualArmControl Arms = new DualArmControl(this);
        GrabberControl Grabber = new GrabberControl(this);
        AmpereControl Ampere = new AmpereControl(this);
        VuforiaControl Vuforia = new VuforiaControl(this);
        Drive = new DriveControl(this);
        Imu = new ImuControl(this);
        User = new UserControl(this);

        double voltage = Drive.Battery.getVoltage();
        telemetry.addData("Voltage", voltage);

        /* Initializes the movement speeds which are scaled based on the starting voltage */
        MOVE_SPEED = 0.5 + ((13.2 - voltage) / 14);
        STRAFFE_SPEED = 0.75 + ((13.2 - voltage) / 14);
        ROTATE_SPEED = 0.4 + ((13.2 - voltage) / 14);

        /* Turns off all the color sensor lights */
        Drive.ColorLeft.enableLed(false);
        Drive.ColorRight.enableLed(false);
        Ampere.ColorLeft.enableLed(false);
        Ampere.ColorRight.enableLed(false);

        /* Runs the arrays to receive the position and color from the drivers, set the variables,
           and tell the drivers what it got set to for confirmation */
        redteam = User.getRedBlue("Select Team Color");
        FI = inputPosition();
        do_jewels = User.getYesNo("Do Jewels:");
        displaySelections();

        /* in case I want to modify it more easily for one position */
        //chooseModes();

        //transitions to and initializes teleop once auto is done
        AutoTransitioner.transitionOnStop(this, "TeleOp");

        //waits for that giant PLAY button to be pressed on RC
        waitForStart();

        //resets the clock and sets the start angle once the 30 seconds begins
        resetClock();

        Imu.setReferenceAngle();

        // telling the code to run until you press that giant STOP button on RC
        // include opModeIsActive in all while loops so that STOP button terminates all actions
        while (opModeIsActive() && modes[mode] < 100) {

            /* IMU update code */
            turnAngle = -Imu.getRelativeAngle();

            //keeps now up to date
            now = runtime.seconds() - lastReset;

            //so if the robot starts acting weird, we can check if is because of the color sensors
            telemetry.addData("left_color red", Drive.ColorLeft.red());
            telemetry.addData("left_color blue", Drive.ColorLeft.blue());
            telemetry.addData("right_color red", Drive.ColorRight.red());
            telemetry.addData("right_color blue", Drive.ColorRight.blue());
            telemetry.addData("leftcolor", leftcolor);
            telemetry.addData("rightcolor", rightcolor);
            telemetry.addData("mode", mode);
            telemetry.addData("step", step);
            telemetry.update();

            /* sets the requirements for the left color sensor to be seeing the line it is looking
               for and keeps it up to date */
            if (Drive.ColorLeft.red() > 13 && redteam) {
                leftcolor = true;
            } else if (Drive.ColorLeft.blue() > 2 + leftampereblue && !redteam) {
                leftcolor = true;
            } else {
                leftcolor = false;
            }

            /* sets the requirements for the right color sensor ...(same as above) */
            if (Drive.ColorRight.red() > 12 && redteam) {
                rightcolor = true;
            } else if (Drive.ColorRight.blue() > 2 + rightampereblue && !redteam) {
                rightcolor = true;
            } else {
                rightcolor = false;
            }

            /* the switch containing all the preset modes so it switches between them without a
               giant list of if, else ifs */
            switch (modes[mode]) {

                /* says it is done when it finishes */
                default:
                    telemetry.addLine("All done");
                    Drive.moveStop();
                    break;

                /* vuMark detection + jewel selection */
                case 1:


                    /* vuMark detection */
                    if (step == 0) {
                        //closes grabbers
                        Grabber.LeftServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);
                        Grabber.RightServo.setSetpoint(GrabberControl.GripSetpoints.CLOSE);

                        if (now > 0.2) {
                            //turns ampere LEDs om
                            Ampere.ColorLeft.enableLed(true);
                            Ampere.ColorRight.enableLed(true);

                            //winches out the ampere
                            telemetry.addLine("Extend");
                            Ampere.moveWinches(AMPERE_POWER);
                        }

                        /* VuForia update code */
                        Vuforia.findVuMark();

                        if (Vuforia.getVuMark() == RelicRecoveryVuMark.UNKNOWN) {
                            telemetry.addData("VuMark", "not visible");
                        }
                        if (Vuforia.getVuMark() == RelicRecoveryVuMark.LEFT) {
                            telemetry.addData("VuMark", "left");
                        }
                        if (Vuforia.getVuMark() == RelicRecoveryVuMark.RIGHT) {
                            telemetry.addData("VuMark", "right");
                        }
                        if (Vuforia.getVuMark() == RelicRecoveryVuMark.CENTER) {
                            telemetry.addData("VuMark", "center");
                        }

                        switch (home_sequence) {
                            case 0:
                                /* lift arm up, through gate, after glyph grabbed */
                                if (now > 0.5) {
                                    Arms.UpperArm.moveToPosition(0.2);
                                    home_sequence++;
                                }
                                break;
                            case 1:
                                /* wait until arm has gone through gate, then send arm home */
                                if (Arms.UpperArm.getCurrentPosition() > 0.1) {
                                    Arms.UpperArm.moveToPosition(0.1);
                                    Arms.UpperArm.moveHome(2.0);
                                    if (do_jewels) {
                                        step++;
                                    } else {
                                        mode++;
                                    }
                                    resetClock();
                                    Drive.moveStop();
                                }
                                break;
                        }
                    }
                    /* jewel selection step 1 */
                    else if (step == 1) {
                        //turns ampere LEDs om
                        Ampere.ColorLeft.enableLed(true);
                        Ampere.ColorRight.enableLed(true);

                        //winches out the ampere
                        telemetry.addLine("Extend");
                        Ampere.moveWinches(AMPERE_POWER);

                        //waits 4 seconds
                        if (now > 3.0) {
                            //extends flickers
                            Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.OPEN);
                            Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.OPEN);

                            //if it hasn't calibrated yet. this makes sure it only runs this bit once
                            if (leftamperered == 0) {
                                //calibrates to light of open air
                                leftamperered = Ampere.ColorLeft.red();
                                leftampereblue = Ampere.ColorLeft.blue();
                                rightamperered = Ampere.ColorRight.red();
                                rightampereblue = Ampere.ColorRight.blue();
                            }
                        }

                        //waits till fully extended
                        if (now > 7.7) {
                            //ends the mode
                            step++;
                            resetClock();
                            Drive.moveStop();
                        }
                    }
                    /* jewel selection step 2 */
                    else if (step == 2) {
                        //stops the winches
                        telemetry.addLine("Stop");
                        Ampere.moveWinches(0.0);

                        /* checks if both color sensors detect a difference in the change of values and
                           returns true if the side is red and the side is blue */
                        leftampere = false;
                        rightampere = false;
                        if (((Ampere.ColorLeft.red() - leftamperered) > 10 + (Ampere.ColorRight.red() - rightamperered)) &&
                                ((Ampere.ColorRight.blue() - rightampereblue) > 10 + (Ampere.ColorLeft.blue() - leftampereblue))) {
                            leftampere = true;
                        }
                        if (((Ampere.ColorRight.red() - rightamperered) > 10 + (Ampere.ColorLeft.red() - leftamperered)) &&
                                ((Ampere.ColorLeft.blue() - leftampereblue) > 10 + (Ampere.ColorRight.blue() - rightampereblue))) {
                            rightampere = true;
                        }

                        //gives it a bit to check
                        if (now > 0.1) {
                        /* if both color sensors agree, the one that is true will be red. it hits the
                           correct one for our color. if both didn't agree, they will both be false,
                           so it will move on without scoring the wrong jewel */
                            if (leftampere) {
                                if (redteam) {
                                    Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
                                } else {
                                    Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
                                }
                            } else if (rightampere) {
                                if (redteam) {
                                    Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
                                } else {
                                    Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
                                }
                            }

                            //reels back in the amperes
                            telemetry.addLine("Retract");
                            Ampere.moveWinches(-AMPERE_POWER);

                            //turns LEDs of
                            Ampere.ColorLeft.enableLed(false);
                            Ampere.ColorRight.enableLed(false);

                            //moves on without knocking one of if it isn't certain it saw it properly
                            step++;
                            resetClock();
                            Drive.moveStop();
                        }
                    }
                    /* jewel selection step 3 */
                    else if (step == 3) {
                        //reels back in the amperes
                        telemetry.addLine("Retract");
                        Ampere.moveWinches(-AMPERE_POWER);

                        //gives time to get past the jewels
                        if (now > 2.2) {
                            //folds in the servos
                            telemetry.addLine("Fold in");
                            Ampere.LeftFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
                            Ampere.RightFlipperServo.setSetpoint(AmpereControl.Setpoints.CLOSE);
                        }

                        //gives time for jewel servos to fold in
                        if (now > 4.0) {
                            mode++;
                            resetClock();
                            step = 0;
                        }
                    }
                    break;

                /* backup 24 inches */
                case 2:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    //backs up for a set time
                    Drive.moveBackward(MOVE_SPEED);
                    if (now > 0.58) {
                        mode++;
                        resetClock();
                        Drive.moveStop();
                        step = 0;
                    }
                    break;

                /* turn to -90 (red) or 90 (blue) */
                case 3:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    if (now > 0.5) {
                        //turns to angle
                        if (redteam) {
                            turn2angle(-90);
                        }
                        if (!redteam) {
                            turn2angle(90);
                        }
                    }
                    break;

                /* strafe right (red) or left (blue) to align on balancing stone (BI) */
                case 4:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    //strafes in the direction of the stone, which depends on team color
                    if (now < 0.46 && redteam) {
                        Drive.moveForward(MOVE_SPEED * 0.75);
                    } else if (now > 0.5) {
                        if (redteam) {
                            Drive.moveRight(STRAFFE_SPEED);
/*                            robot.FR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle + 90)/200);
                            robot.FL.setPower(STRAFFE_SPEED * 1.05 - (turnAngle + 90)/200);
                            robot.BL.setPower(-STRAFFE_SPEED * 0.95 - (turnAngle + 90)/200);
                            robot.BR.setPower(STRAFFE_SPEED * 0.95 + (turnAngle + 90)/200);
                            */
                        } else {
                            Drive.moveLeft(STRAFFE_SPEED);
/*                            robot.FR.setPower(STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                            robot.FL.setPower(-STRAFFE_SPEED * 1.15 - (turnAngle - 90)/200);
                            robot.BL.setPower(STRAFFE_SPEED * 0.95 - (turnAngle - 90)/200);
                            robot.BR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                            */
                        }
                    } else {
                        Drive.moveStop();
                    }

                    //strafes for a set time
                    if (now > 1.8) {
                        mode++;
                        resetClock();
                        Drive.moveStop();
                        step = 0;
                    }
                    break;

                /* move forward 36 inches (FI) or till sees a line (BI) */
                case 5:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    //moves forward for a set time (FI) or till it sees a line (BI)
                    if (FI) {
                        Drive.moveForward(MOVE_SPEED);
                    } else /*if (Math.abs(Math.abs(turnAngle) - 90) < 5)*/ {
                        if (step == -1) {
                            Drive.moveBackward(MOVE_SPEED);
                            if (now > 0.25) {
                                step++;
                                resetClock();
                            }
                        }

                        /* triangulate */
                        else if (step == 0) {
                            Drive.moveForward(MOVE_SPEED * 0.7);
                            if (now > 5) {
                                step--;
                                resetClock();
                            }
                        }
                    }

                    if ((FI && now > 1.05) || (!FI && (leftcolor || rightcolor))) {
                        mode++;
                        resetClock();
                        Drive.moveStop();
                        step = 0;
                        leftcolorblue = Drive.ColorLeft.blue();
                        rightcolorblue = Drive.ColorRight.blue();
                    }
                    break;

                /* turn to 0 (FI) */
                case 6:
                    //continues reeling in the amperes
                    telemetry.addLine("Retract");
                    //robot.AWL.setPower(-AMPERE_POWER);
                    //robot.AWR.setPower(-AMPERE_POWER);

                    if (FI) {
                        //turns to angle
                        turn2angle(0);
                    } else {
                        mode++;
                        resetClock();
                        Drive.moveStop();
                        step = 0;
                    }
                    break;

                /* tirangulate 'n strafe (FI) or strafe to column (BI) */
                case 7:
                    //turns the LEDs on
                    Drive.ColorLeft.enableLed(true);
                    Drive.ColorRight.enableLed(true);

                    /* triangulate 'n strafe (FI) */
                    if (FI) {
                        if (step == -1) {
                            Drive.moveBackward(MOVE_SPEED);
                            if (now > 2.5) {
                                step++;
                                resetClock();
                            }
                        }

                        /* triangulate */
                        else if (step == 0) {
                            if (now > 5) {
                                step--;
                                resetClock();
                            }

                            //stops if both see the line
                            if (leftcolor && rightcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }

                            //strafes right at an angle if the left side sees the line
                            else if (leftcolor && !rightcolor) {
                                Drive.moveLeft(STRAFFE_SPEED);
/*                                robot.FL.setPower(-MOVE_SPEED);
                                robot.BR.setPower(-MOVE_SPEED);
                                robot.BL.setPower(MOVE_SPEED * 1.4);
                                robot.FR.setPower(MOVE_SPEED * 1.4);
                                */
                            }

                            //strafes left at an angle if the right side sees the line
                            else if (!leftcolor && rightcolor) {
                                Drive.moveRight(STRAFFE_SPEED);
/*                                robot.FL.setPower(MOVE_SPEED * 1.4);
                                robot.BR.setPower(MOVE_SPEED * 1.4);
                                robot.BL.setPower(-MOVE_SPEED);
                                robot.FR.setPower(-MOVE_SPEED);
                                */
                            }

                            //drives forward if it doesn't see anything
                            else {
                                Drive.moveForward(MOVE_SPEED * 0.7);
                            }
                        } else {
                            /* 'n strafe */
                            if (Vuforia.getVuMark() == RelicRecoveryVuMark.LEFT) {
                                //strafe left until the right sensor gets to the line on the other side
                                Drive.moveLeft(STRAFFE_SPEED);
/*                                robot.FR.setPower(STRAFFE_SPEED * 1.05 + turnAngle/200);
                                robot.FL.setPower(-STRAFFE_SPEED * 1.15 - turnAngle/200);
                                robot.BL.setPower(STRAFFE_SPEED * 0.95 - turnAngle/200);
                                robot.BR.setPower(-STRAFFE_SPEED * 1.05 + turnAngle/200);
                                */
                                if (step == 1 && !rightcolor) {
                                    step++;
                                    resetClock();
                                }
                                if (step == 2 && rightcolor) {
                                    //lined up, so moves on
                                    mode++;
                                    resetClock();
                                    Drive.moveStop();
                                    step = 0;
                                }
                            } else if (Vuforia.getVuMark() == RelicRecoveryVuMark.RIGHT) {
                                //strafe right until the left sensor gets to the line on the other side
                                Drive.moveRight(STRAFFE_SPEED);
/*                                robot.FR.setPower(-STRAFFE_SPEED * 1.05 + turnAngle/200);
                                robot.FL.setPower(STRAFFE_SPEED * 1.05 - turnAngle/200);
                                robot.BL.setPower(-STRAFFE_SPEED * 0.95 - turnAngle/200);
                                robot.BR.setPower(STRAFFE_SPEED * 0.95 + turnAngle/200);
                                */
                                if (step == 1 && !leftcolor) {
                                    step++;
                                    resetClock();
                                }
                                if (step == 2 && leftcolor) {
                                    //lined up, so moves on
                                    mode++;
                                    resetClock();
                                    Drive.moveStop();
                                    step = 0;
                                }
                            } else {
                                //already lined up, so moves on
                                mode++;
                                resetClock();
                                Drive.moveStop();
                                step = 0;
                            }
                        }
                    }

                    /* strafe right to column (RABI) */
                    else if (redteam) {
                        Drive.moveRight(STRAFFE_SPEED);
/*                        robot.FR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle + 90)/200);
                        robot.FL.setPower(STRAFFE_SPEED * 1.05 - (turnAngle + 90)/200);
                        robot.BL.setPower(-STRAFFE_SPEED * 0.95 - (turnAngle + 90)/200);
                        robot.BR.setPower(STRAFFE_SPEED * 0.95 + (turnAngle + 90)/200);
                        */
                        if (step == 0 && (Vuforia.getVuMark() == RelicRecoveryVuMark.CENTER || Vuforia.getVuMark() == RelicRecoveryVuMark.RIGHT)) {
                            //strafe right until the left sensor gets to the line on the other side
                            if (leftcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }
                        } else if (step == 1 && Vuforia.getVuMark() == RelicRecoveryVuMark.RIGHT) {
                            if (!leftcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }
                        } else if (step == 2) {
                            if (leftcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }
                        } else {
                            //lined up, so moves on
                            mode++;
                            resetClock();
                            Drive.moveStop();
                            step = 0;
                        }
                    }

                    /* strafe left till column (BABI) */
                    else {
                        Drive.moveLeft(STRAFFE_SPEED);
/*                        robot.FR.setPower(STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
                        robot.FL.setPower(-STRAFFE_SPEED * 1.15 - (turnAngle - 90)/200);
                        robot.BL.setPower(STRAFFE_SPEED * 0.95 - (turnAngle - 90)/200);
                        robot.BR.setPower(-STRAFFE_SPEED * 1.05 + (turnAngle - 90)/200);
*/
                        if (step == 0 && (Vuforia.getVuMark() == RelicRecoveryVuMark.CENTER || Vuforia.getVuMark() == RelicRecoveryVuMark.LEFT)) {
                            //strafe left until the right sensor gets to the line on the other side
                            if (rightcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }
                        } else if (step == 1 && Vuforia.getVuMark() == RelicRecoveryVuMark.LEFT) {
                            if (!rightcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }
                        } else if (step == 2) {
                            if (rightcolor) {
                                step++;
                                resetClock();
                                Drive.moveStop();
                            }
                        } else {
                            //lined up, so moves on
                            mode++;
                            resetClock();
                            Drive.moveStop();
                            step = 0;
                        }
                    }
                    break;

                /* release glyph and move forward 10 inches into cryptobox */
                case 8:
                    //turns LEDs off
                    Drive.ColorLeft.enableLed(false);
                    Drive.ColorRight.enableLed(false);

                    //opens grabbers
                    Grabber.LeftServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);
                    Grabber.RightServo.setSetpoint(GrabberControl.GripSetpoints.OPEN);

                    //moves forward for a set time
                    Drive.moveForward(MOVE_SPEED * 0.8);
                    if (now > 0.4) {
                        mode++;
                        resetClock();
                        Drive.moveStop();
                        step = 0;
                    }
                    break;

                /* move backward 5 inches */
                case 9:
                    //backs up for a set time
                    Drive.moveBackward(MOVE_SPEED * 0.8);
                    if (now > 0.15) {
                        Drive.moveStop();
                        mode++;
                        resetClock();
                        step = 0;
                    }
                    break;

                //Unused modes
//                /* grab glyph */
//                case -2:
//                    //closes grabbers
//                    robot.GGL.setPosition(robot.GRABBER_LEFT[1]);
//                    robot.GGR.setPosition(robot.GRABBER_RIGHT[1]);
//                    if (now > 0.5) {
//                        mode++;
//                        resetClock();
//                        step = 0;
//                    }
//                    break;
//
//                /* release glyph */
//                case -1:
//                    //opens grabbers
//                    robot.GGL.setPosition(robot.GRABBER_LEFT[0]);
//                    robot.GGR.setPosition(robot.GRABBER_RIGHT[0]);
//                    if (now > 0.5) {
//                        mode++;
//                        resetClock();
//                        step = 0;
//                    }
//                    break;
//
//                /* wait one second *
//                case 0:
//                    //turns all LEDs off
//                    robot.left_ampere.enableLed(false);
//                    robot.right_ampere.enableLed(false);
//                    robot.left_color.enableLed(false);
//                    robot.right_color.enableLed(false);
//
//                    //waits 1 second
//                    if (now > 1.0) {
//                        mode++;
//                        resetClock();
//                        robot.moveStop();
//                        step = 0;
//                    }
//                    break;

            }  // end of switch

            //updates the arms
            Arms.Update();

//            telemetry.update();
            sleep(40);
        }
    }

    //important thing that makes Vuforia do its job
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}
