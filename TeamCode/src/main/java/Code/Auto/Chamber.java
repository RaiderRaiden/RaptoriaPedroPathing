package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous (name = "ChamberREV")
public class Chamber extends LinearOpMode {
    private DcMotor BL;
    private DcMotor FL;
    private DcMotor BR;
    private DcMotor FR;
    private DcMotor EM;
    private DcMotor RA;
    private DcMotor LA;
    private CRServo C;
    private CRServo CS;

    private int lPos;
    private int rPos;
    private int ArmPos;
    private int ExtPos;

    @Override
    public void runOpMode() {

        //Sets the Motor to a given port
        FR = hardwareMap.dcMotor.get("FR");
        FL = hardwareMap.dcMotor.get("FL");
        BR = hardwareMap.dcMotor.get("BR");
        BL = hardwareMap.dcMotor.get("BL");
        EM = hardwareMap.dcMotor.get("EM");
        LA = hardwareMap.dcMotor.get("LA");
        RA = hardwareMap.dcMotor.get("RA");
        C = hardwareMap.crservo.get("CS1");
        CS = hardwareMap.crservo.get("CS2");

        //Reverses the right tires
        FL.setDirection(DcMotorSimple.Direction.REVERSE);
        BL.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set the encoder value to zero for all motors

        /* When determining the encoder ticks needed:
                (Distance to trave/Wheelcircumference) * Output CPR
         */

        //Planetary motors use 28 counts/revolution
        FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //4 counts/revolution (at motor), 288 counts/revolution (at output)
        EM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //sets variables to zero
        rPos = 0;
        lPos = 0;
        ArmPos = 0;
        ExtPos = 0;
        claw(-1, 50, -1);


        waitForStart();

        drive(105, 105, 0.15, true); //move forward a little
        dArm(550, 0.9); // lift arm appropriately
        drive(385, 399, 0.15, true); //drive towards chamber
        dArm(-145, 1); //lower arm to hook specimen
        sleep(1000); //stay for a second to let specimen settle
        claw(0.5, 150, 0); //release claw
        drive(-200, -250, 0.15, true); //back up to line with observation
        drive(240, -240, 0.15, true); //turn towards the observation zone
        dArm(-228, 0.9); //lower the arm to avoid penalty
        drive(415, 430, 0.15, true); // drive to observation zone
        drive(20, 20, 0.3, false);



    }

    //Controls the drive train
    private void drive(int lTar, int rTar, double speed, boolean ace) {
        //Change the target pos of the motors encoders
        rPos += rTar;
        lPos += lTar;

        //Sets the target positions of each encoder
        FR.setTargetPosition(rPos);
        BR.setTargetPosition(rPos);
        FL.setTargetPosition(lPos);
        BL.setTargetPosition(lPos);

        //Tells the motors to move to the target position
        FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Tells the motors at what speed they should be running.
        if (ace) {
            for (double i = 0.025; i <= speed; i += 0.025) {
                double accel = i;
                FL.setPower(accel);
                FR.setPower(accel);
                BL.setPower(accel);
                BR.setPower(accel);

            }
        }

        //Makes the code idle until the movement is done to prevent confusing operations.
        while(opModeIsActive() && FL.isBusy() && FR.isBusy() && BL.isBusy() && BR.isBusy()) {
            idle();
        }

    }



    //Controls the arm
    private void dArm(int Tar, double speed) {
        //Modifies the goal position
        ArmPos += Tar;

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

    }

    private void extend(int Tar) {
        ExtPos += Tar;
        EM.setTargetPosition(ExtPos);
        EM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        EM.setPower(1);
        while (opModeIsActive() && EM.isBusy()) {
            idle();
        }
    }
    //controls the claw
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
