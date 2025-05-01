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

/***This Auto is currently in Progress.
 ***    Goal: Score 2-3 Specimens
 ***    Progress: Can score one specimen, and pick up the last one
 ***    Untested: Going to score human player specimen
 ***
 ***    PLEASE READ:
 ***        This auto should start the robot with the tires lined to the edge
 ***        of the third block from observation zone, it should face toward the
 ***        opposing alliance.
 ***
 ***
 ***/

@Autonomous(name = "SpecPath3")
public class SpecPath3 extends LinearOpMode {

    //Built in from Source.
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    //Control paths
    private int pathState;

    //Declare Starting Position
    private final Pose startPose = new Pose(7.625, 64.625, Math.toRadians(0));

    //First Path
    private Path scorePreLoad, secureSpike, stabHuman, scoreHuman1, goToSub, grabHuman2, scoreHuman2, park;



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
        dArm(-65, 1, true);

        waitForStart();

        opmodeTimer.resetTimer();

        /***Set to run first path***/
        setPathState(0);

        /***Loop to constantly update path and position***/
        while(opModeIsActive()){

            //Update location
            follower.update();
            autonomousPathUpdate();

            // Feedback to Driver Hub
            telemetry.addData("path state", pathState);
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.addData("ArmPos: ", LA.getCurrentPosition());
            telemetry.update();

        }



    }


    /***Build all paths used***/
    public void buildpath() {

        /***Score the Preload ***/
        scorePreLoad = new Path(new BezierLine(new Point(startPose), new Point(40.000, 77.000, Point.CARTESIAN)));
        scorePreLoad.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0));
        scorePreLoad.setZeroPowerAccelerationMultiplier(0.24);


        /***We need a weapon!***/
        secureSpike = new Path(new BezierCurve(new Point(40.000, 77.00, Point.CARTESIAN),new Point(0.943, 41.008 , Point.CARTESIAN), new Point(65.000, 32.759, Point.CARTESIAN)));
        secureSpike.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180));
        secureSpike.setReversed(true);


        /***Time to kill***/
        stabHuman = new Path(new BezierCurve(new Point(65.000, 32.759, Point.CARTESIAN), new Point(66.226, 18.854, Point.CARTESIAN), new Point(10.95, 24.000, Point.CARTESIAN)));
        stabHuman.setConstantHeadingInterpolation(Math.toRadians(180));
        stabHuman.setZeroPowerAccelerationMultiplier(2.5);

        /***Take him to the dungeon***/
        scoreHuman1 = new Path(new BezierLine(new Point(10.950, 24.000, Point.CARTESIAN), new Point(40.000, 72.000, Point.CARTESIAN)));
        scoreHuman1.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0));
        scoreHuman1.setZeroPowerAccelerationMultiplier(3.5);

        /***There's another!***/
        goToSub = new Path(new BezierLine(new Point(40.000, 72.000, Point.CARTESIAN), new Point(23, 15.000, Point.CARTESIAN)));
        goToSub.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180));

        /***Get'em***/
        grabHuman2 = new Path(new BezierLine(new Point(23.000, 15.000, Point.CARTESIAN), new Point(11.345, 15.000, Point.CARTESIAN)));
        grabHuman2.setConstantHeadingInterpolation(Math.toRadians(180));

        /***To the chopping block, my patience is gone***/
        scoreHuman2 = new Path(new BezierLine(new Point(11.345, 15.000, Point.CARTESIAN), new Point(40.00, 68.000, Point.CARTESIAN)));
        scoreHuman2.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0));

        /***Time to take a break***/
        park = new Path(new BezierLine(new Point(40.000, 68.000, Point.CARTESIAN), new Point(13.000, 15.000, Point.CARTESIAN)));
        park.setConstantHeadingInterpolation(Math.toRadians(0));
        park.setZeroPowerAccelerationMultiplier(5);




    }

    /***Align to sub and raise arm***/
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                //Go score preLoad
                dArm(-475, 1, false);
                follower.followPath(scorePreLoad, true);
                setPathState(1);
                break;
            case 1:
                //Clip spec, and go get spike
                if (!follower.isBusy()) {
                    dArm(170, 1, true);
                    claw(1, 100, 0);
                    follower.followPath(secureSpike);
                    setPathState(2);
                }

            case 2:

                //Weaponize the Jank. (bring spike to observation zone)
                if (!follower.isBusy()) {
                    dArm(165, 1, false);
                    follower.followPath(stabHuman, true);
                    setPathState(3);
                }
                break;

            case 3:

                //Get first human spec and go score it
                if (!follower.isBusy()) {
                    claw(-1, 150, -0.5);
                    sleep(50);
                    dArm(-75, 1, true);
                    dArm(-240, 1, false);
                    sleep(100);
                    follower.followPath(scoreHuman1);
                    setPathState(4);
                }
            case 4:
                //Score the spec and open claw
                if (!follower.isBusy()) {
                    dArm(170, 1, true);
                    claw(1, 150, 0);
                    dArm(-10, 1, true);
                    setPathState(5);
                }
                break;
            case 5:
                //Go to the sub to pick the next spec up, while lowering arm
                if (!follower.isBusy()) {
                    follower.followPath(goToSub, true);
                    setPathState(6);
                }
                break;
            case 6:
                //Approach the player and get last spec
                if (!follower.isBusy()) {
                    dArm(167, 1, true);
                    follower.followPath(grabHuman2);
                    setPathState(7);
                }
                break;
            case 7:
                //Get the spec and go to chamber
                if (!follower.isBusy()) {
                    claw(-1, 150, -0.55);
                    sleep(100);
                    dArm(-50, 1, true);
                    dArm(-290, 1, false);
                    follower.followPath(scoreHuman2, true);
                    setPathState(8);
                }
                break;
            case 8:
                //Go park
                if (!follower.isBusy()) {
                    dArm(185, 1, true);
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
    public void dArm(int tar, double speed, boolean wait) {

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
        while(LA.isBusy() && RA.isBusy() && opModeIsActive() && wait){
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