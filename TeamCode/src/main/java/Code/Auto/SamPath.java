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
 ***/

@Autonomous(name = "Sam")
public class SamPath extends LinearOpMode {

    //Built in from Source.
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    //Control paths
    private int pathState;

    //Declare Starting Position
    private final Pose startPose = new Pose(8, 100, Math.toRadians(0));

    //First Path
    private Path start1, scoreSAM, prepare1, prepare2, prepare3, prepare4,push1,round2,endPush;
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

        /***GET READY TO RUMBLE!!***/
        start1 = new Path(new BezierLine(
                new Point(8.285, 108.539, Point.CARTESIAN),
                new Point(19.222, 124.944, Point.CARTESIAN)
        )
        );
        start1.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(135));


        /***SCORE THAT SAMPLE IN THAT LOW BUCKET LETS GOO LETS FUCKING GO!!***/
        scoreSAM = new Path(new BezierLine(
                new Point(19.222, 124.944, Point.CARTESIAN),
                new Point(16.239, 127.761, Point.CARTESIAN)
        )
        );
        scoreSAM.setTangentHeadingInterpolation();

        /***OKAY OKAY NOW GET OUT OF THERE AND START GETTING READY TO PUSH!!***/
        prepare1 = new Path(new BezierCurve(
                new Point(16.239, 127.761, Point.CARTESIAN),
                new Point(27.507, 84.677, Point.CARTESIAN),
                new Point(68.272, 120.635, Point.CARTESIAN)
        )
    );
    prepare1.setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180));


        /***AND PUSH!!!***/
        push1 = new Path(new BezierCurve(
                new Point(68.272, 120.635, Point.CARTESIAN),
                new Point(34.467, 118.812, Point.CARTESIAN),
                new Point(11.600, 126.269, Point.CARTESIAN)
        )
        );
                push1.setConstantHeadingInterpolation(Math.toRadians(180));

        /***GET READY TO PUSH THAT 2nd BRICK!***/
        round2 = new Path(new BezierCurve(
                new Point(11.600, 126.269, Point.CARTESIAN),
                new Point(56.341, 109.699, Point.CARTESIAN),
                new Point(68.603, 130.909, Point.CARTESIAN)
        )
        );
                round2.setConstantHeadingInterpolation(Math.toRadians(180));


        /***AND PUSH!!***/
        endPush = new Path(new BezierLine(
                new Point(68.603, 130.909, Point.CARTESIAN),
                new Point(15.908, 132.732, Point.CARTESIAN)
        )
    );
    endPush.setConstantHeadingInterpolation(Math.toRadians(180));
    }



    /***Go time***/
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                //Line to sub
                follower.followPath(start1, true);
                setPathState(1);
                break;
            case 1:
                follower.followPath(scoreSAM,true);
                setPathState(2);
                break;
            case 2:
                follower.followPath(prepare1,true);
                setPathState(3);
                break;
            case 3:
                follower.followPath(push1,true);
                setPathState(4);
                break;
            case 4:
                follower.followPath(round2,true);
                setPathState(5);
                break;
            case 5:
                follower.followPath(endPush,true);
                setPathState(6);
                break;
                }
        }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }


    /***Change which path your following***/


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