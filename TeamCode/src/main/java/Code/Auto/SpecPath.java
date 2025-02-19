package Code.Auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
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

/***This Auto is currently in Progress.
 ***    Goal: Score 2-3 Specimens
 ***    Progress: Can score one specimen, and pick up the last one
 ***    Untested: Going to score human player specimen
 ***
 ***    PLEASE READ:
 ***        This auto should start the robot with the tires lined to the edge
 ***        of the third block from observation zone, it should face toward the
 ***        opposing alliance.
 ***/

@Autonomous(name = "Spec")
public class SpecPath extends LinearOpMode {

    //Built in from Source.
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    //Control paths
    private int pathState;

    //Declare Starting Position
    private final Pose startPose = new Pose(7.625, 64.625, Math.toRadians(0));

    //First Path
    private Path forward1;
    private Path scorePreLoad;

    //Second path
    private Path backup1, goToSub, pickUpHuman, scoreHuman, park;


    private DcMotor LA;
    private DcMotor RA;
    private CRServo C;
    private CRServo CS;


    @Override
    /***Control Code: DO NOT REMOVE***/
    public void runOpMode() {
        /***Declare new Timer(s). I don't know why, but I'm scared to remove them***/
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        /***Allow our constants to function***/
        Constants.setConstants(FConstants.class, LConstants.class);

        /***Initiate our follower***/
        follower = new Follower(hardwareMap);

        /***Set starting position***/
        follower.setStartingPose(startPose);

        /***Map the motors and servos aside from drive train***/
        LA = hardwareMap.dcMotor.get("LA");
        RA = hardwareMap.dcMotor.get("RA");
        C = hardwareMap.crservo.get("CS1");
        CS = hardwareMap.crservo.get("CS2");

        /***Set modes+direction for motors***/
        RA.setDirection(DcMotorSimple.Direction.REVERSE);
        LA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /***Build our paths***/
        buildpath();

        /***Grab block, can be used to know init is done***/
        claw(-1, 50, -0.5);

        waitForStart();

        opmodeTimer.resetTimer();

        /***Set to run first path***/
        setPathState(0);

        /***Loop to constantly update path and position***/
        while(opModeIsActive()){

            //Update location
            follower.update();

            /***Tells which set of paths to follow***/
            if (pathState <= 1) {
                autonomousPathUpdate();
            }
            else if (pathState <= 3){
                autonomousPathUpdate1();
            }
            else  if (pathState <= 7){
                autonomousPathUpdate2();
            }
            else {
                autonmousPathUpdate3();
            }

            // Feedback to Driver Hub
            telemetry.addData("path state", pathState);
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.addData("Armpos: ", LA.getCurrentPosition());
            telemetry.update();

        }



    }


    /***Build all paths used***/
    public void buildpath() {

        /***Align to sub***/
        forward1 = new Path(new BezierLine(new Point(startPose), new Point(24.275, 72.589, Point.CARTESIAN)));
        forward1.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0));

        /***Goes to submersible and score preLoad***/
        scorePreLoad = new Path(new BezierLine(new Point(23.975, 72.589, Point.CARTESIAN), new Point(39.500, 72.589, Point.CARTESIAN)));
        scorePreLoad.setConstantHeadingInterpolation(Math.toRadians(0));

        /***Back away from the sub!***/
        backup1 = new Path(new BezierLine(new Point(39.500, 72.589, Point.CARTESIAN),new Point(24.275, 72.000, Point.CARTESIAN)));
        backup1.setConstantHeadingInterpolation(Math.toRadians(0));

        /***Goes to Observation Zone while turning around***/
        goToSub = new Path(new BezierLine(new Point(24.275, 72.000, Point.CARTESIAN), new Point(24.275, 15.000, Point.CARTESIAN)));
        goToSub.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180));

        /***Drive to wall, to pick up specimen***/
        pickUpHuman = new Path(new BezierLine(new Point(24.275, 15.000, Point.CARTESIAN), new Point(12.005, 15.000, Point.CARTESIAN)));
        pickUpHuman.setConstantHeadingInterpolation(Math.toRadians(180));
        pickUpHuman.setZeroPowerAccelerationMultiplier(2.0);

        scoreHuman = new Path(new BezierLine(new Point(12.005, 15.000, Point.CARTESIAN), new Point(39.5, 70, Point.CARTESIAN)));
        scoreHuman.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0));

        park = new Path(new BezierLine(new Point(39.5, 70, Point.CARTESIAN), new Point(12.145, 15.000, Point.CARTESIAN)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));


    }

    /***Align to sub and raise arm***/
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                //Line to sub
                follower.followPath(forward1, true);
                setPathState(1);
                break;
            case 1:
                //raise arm
                if (!follower.isBusy()) {
                    dArm(-550, 0.9);
                    setPathState(2);
                }

            /*case 2:

                //Approach sub.
                if (!RA.isBusy() && !LA.isBusy()) {
                    follower.followPath(scorePreLoad, true);
                    setPathState(3);
                }
                break;

            case 3:

                if (!follower.isBusy()) {
                    dArm(145, 0.9);
                    setPathState(-1);
                } */
        }
    }

    /***Drive to sub and score preLoad***/
    public void autonomousPathUpdate1() {
        switch (pathState) {
            case 0:

            case 1:

            case 2:

                //Approach sub.
                if (!follower.isBusy()) {
                    follower.followPath(scorePreLoad, false);
                    setPathState(3);
                }
                break;

            case 3:

                if (!follower.isBusy()) {
                    dArm(170, 1);
                    claw(1, 150, 0);
                    setPathState(4);
                }
        }
    }

    /***Goes to pick up second specimen***/
    public void autonomousPathUpdate2() {
        switch (pathState) {

            case 4:

                if (!follower.isBusy()) {
                    follower.followPath(backup1);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    dArm(170, 0.9);
                    follower.followPath(goToSub);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(pickUpHuman);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    sleep(200);
                    claw(-1, 150, -0.5);
                    dArm(-340, 0.9);
                    follower.followPath(scoreHuman, true);
                    setPathState(8);
                }
                break;
        }

    }
    public void autonmousPathUpdate3() {
        switch (pathState) {
            case 0:

            case 8:
                if (!follower.isBusy()) {
                    dArm(190, 0.9);
                    claw(1, 100, 0);
                    follower.followPath(park, true);
                    setPathState(-1);
                }
        }
    }

    /***Change which path your following***/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /***Allows us to move our arm***/
    public void dArm(int tar, double speed) {

        //Reset encoder
        LA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Set the amount we want our arm to move
        LA.setTargetPosition(tar);
        RA.setTargetPosition(tar);

        //Tell the encoder to move to the goal position
        RA.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LA.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //sets the speed of the motors
        LA.setPower(speed);
        RA.setPower(speed);

        //Prevent further progression of path while arm is moving
        while(LA.isBusy() && RA.isBusy() && opModeIsActive()){
            idle();
        }

        //Used as insurance to prevent the arm stalling


    }

    /***Allows the claw to be controlled***/
    private void claw(double speed, int time, double eSpeed) {
        //Controls the two claws
        C.setPower(speed);
        CS.setPower(-speed);

        //How long to keep current power
        sleep(time);

        //Allows us to end at non-zero powers
        C.setPower(eSpeed);
        CS.setPower(-eSpeed);
    }
}