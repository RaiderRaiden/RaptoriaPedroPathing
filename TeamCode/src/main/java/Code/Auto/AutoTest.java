package Code.Auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "AroundAndAround")
public class AutoTest extends LinearOpMode {

    private Pose startPosition= new Pose(7.5, 72.000, Math.toRadians(0));
    private int pathState, track;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int ArmPos;
    private CRServo C;
    private CRServo CS;
    private DcMotor LA;
    private DcMotor RA;


    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                //Align with middle of chamber
                follower.followPath(SpecPath.line1, true);
                setPathState(1);
                break;
            case 1:
                //Raises Arm before approaching chamber
                if(!follower.isBusy()) {

                    dArm(550, 0.9);
                    setPathState(2);
                }
                break;
            case 2:
                //Approach Chamber
                if(!follower.isBusy()) {

                    follower.followPath(SpecPath.line2);
                    setPathState(3);
                }
                break;
            case 3:
                //Score pre-load
                if (!follower.isBusy()) {

                    dArm(-185, 0.9);
                    //claw(1, 100, 0);
                    setPathState(-1);
                }
                break;


        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
        follower.update();
    }


    /** This method is called once at the init of the OpMode. **/
    @Override
    public void runOpMode() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        C = hardwareMap.crservo.get("CS1");
        CS = hardwareMap.crservo.get("CS2");
        LA = hardwareMap.dcMotor.get("LA");
        RA = hardwareMap.dcMotor.get("RA");
        LA.setDirection(DcMotorSimple.Direction.REVERSE);


        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPosition);
        claw(-1, 0, -1);

        waitForStart();

        opmodeTimer.resetTimer();
        setPathState(0);

        while(opModeIsActive()) {

            // Feedback to Driver Hub
            telemetry.addData("path state", pathState);
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.update();
            follower.update();
            autonomousPathUpdate();
        }





    }

    private void dArm(int Tar, double speed) {
        //Modifies the goal position
        ArmPos  = Tar;

        RA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //updates the new goal position
        LA.setTargetPosition(ArmPos);
        RA.setTargetPosition(ArmPos);

        //Tell the encoder to move to the goal position
        RA.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LA.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //sets the speed of the motors
        LA.setPower(speed);
        RA.setPower(speed);

        //Tells the program to idle as to prevent the confusion of operations.
       while(opModeIsActive() && RA.isBusy() && LA.isBusy()) {
            idle();
       }
        LA.setPower(0);
        RA.setPower(0);

    }
    private void claw(double speed, int time, double eSpeed) {
        if (speed > 0) {
            C.setPower(speed/1.2);
        }
        else {
            C.setPower(speed);
        }
        CS.setPower(-speed);
        sleep(time);
        C.setPower(eSpeed);
        CS.setPower(-eSpeed);
    }




}