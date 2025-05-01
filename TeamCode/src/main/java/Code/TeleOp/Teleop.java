package Code.TeleOp;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "uMakeTheName")
public class Teleop extends OpMode {

    //Initialize the variables
    DcMotor FR;
    DcMotor FL;
    DcMotor BR;
    DcMotor BL;
    DcMotor EM;
    DcMotor LA;
    DcMotor RA;
    CRServo CS1;
    CRServo CS2;
    Servo Wrist;
    boolean fine = false;
    boolean isFreed = false;


    @Override
    public void init() {
        //Set variable to motor
        FR = hardwareMap.dcMotor.get("FR");
        FL = hardwareMap.dcMotor.get("FL");
        BR = hardwareMap.dcMotor.get("BR");
        BL = hardwareMap.dcMotor.get("BL");
        EM = hardwareMap.dcMotor.get("EM");
        LA = hardwareMap.dcMotor.get("LA");
        RA = hardwareMap.dcMotor.get("RA");
        CS1 = hardwareMap.crservo.get("LCR");
        CS2 = hardwareMap.crservo.get("RCR");
        Wrist = hardwareMap.servo.get("W");


        //set the Power to brake
        FR.setZeroPowerBehavior(BRAKE);
        FL.setZeroPowerBehavior(BRAKE);
        BR.setZeroPowerBehavior(BRAKE);
        BL.setZeroPowerBehavior(BRAKE);
        LA.setZeroPowerBehavior(BRAKE);
        RA.setZeroPowerBehavior(BRAKE);

        //Set the mode. This is to make sure we try to account for all other variables
        FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LA.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RA.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        //Reverse the direction of the Right side motors to move in the same way as the left.
        FR.setDirection(DcMotorSimple.Direction.REVERSE);
        BR.setDirection(DcMotorSimple.Direction.REVERSE);
        LA.setDirection(DcMotorSimple.Direction.REVERSE);


    }
    @Override
    public void loop() {

        //Controls the wheels.
        //A limiter was placed on it to make the robot more controlable.
        BL.setPower((gamepad1.left_stick_y - (gamepad1.right_stick_x/1.2) + (gamepad1.left_stick_x))/2.75);
        FL.setPower((gamepad1.left_stick_y - (gamepad1.right_stick_x/1.2) - (gamepad1.left_stick_x))/2.75);
        FR.setPower((gamepad1.left_stick_y + (gamepad1.right_stick_x/1.2) + (gamepad1.left_stick_x))/2.75);
        BR.setPower((gamepad1.left_stick_y + (gamepad1.right_stick_x/1.2) - (gamepad1.left_stick_x))/2.75);



        //Allows the player to lift the arm by pressing either the 'x' button or the left bumper
        if (gamepad1.left_bumper) {
            RA.setPower(1);
            LA.setPower(1);
        }

        //Allows the player to lower the arm by pressing the 'y' button or right bumper.
        else if (gamepad1.right_bumper || gamepad1.y) {
            if (gamepad1.y && !gamepad1.right_bumper) {
                RA.setPower(-0.90);
                LA.setPower(-0.90);
            }
            else {

                RA.setPower(-0.45);
                LA.setPower(-0.45);
            }
        }

        //Reset power to zero when no button is pressed for the arm
        else {
            RA.setPower(0);
            LA.setPower(0);
        }

        //Extend the arm when 'b' is pressed and retracts when 'a' is pressed
        if (gamepad1.a) {
            EM.setPower(1);
        }
        else if (gamepad1.b) {
            EM.setPower(-1);
        }
        else {EM.setPower(0);}

        //Sets the claws power with a limiter so it doesn't auto push itself
        CS1.setPower(((gamepad1.left_trigger/1.2)-gamepad1.right_trigger));
        CS2.setPower((gamepad1.right_trigger-gamepad1.left_trigger));


        if (gamepad1.dpad_down) {
            Wrist.setPosition(0.25);
        }
        if (gamepad1.dpad_left) {
            Wrist.setPosition(0.5);
        }
        if(gamepad1.dpad_up) {
            Wrist.setPosition(1);
        }




        telemetry.addData("fine: ", fine);
        telemetry.addData("x: ", gamepad1.x);
        telemetry.addData("dpad_down: ", gamepad1.dpad_down);
    }

}
