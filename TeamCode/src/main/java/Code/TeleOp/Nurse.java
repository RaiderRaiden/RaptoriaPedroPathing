package Code.TeleOp;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
@TeleOp(name = "Nurse")
public class Nurse extends OpMode {


    DcMotor FR;
    DcMotor FL;
    DcMotor BR;
    DcMotor BL;
    DcMotor RL; //Right Ladder, in fact not Right Left
    DcMotor LL; //Left Ladder, in fact not Left left.
    DcMotor RA; //Not resident advisor, it is our right arm (Pivot)
    DcMotor LA;
    @Override
    public void init() {
        FR = hardwareMap.dcMotor.get("FR");
        FL = hardwareMap.dcMotor.get("FL");
        BR = hardwareMap.dcMotor.get("BR");
        BL = hardwareMap.dcMotor.get("BL");
        RL = hardwareMap.dcMotor.get("RL");
        LL = hardwareMap.dcMotor.get("LL");
        RA = hardwareMap.dcMotor.get("RA");
        LA = hardwareMap.dcMotor.get("LA");
    }

    @Override
    public void loop() {
        if (gamepad1.a); //these 4 are the 4 main driving motors IE: FR = Front Right
        FR.setPower(1);
        if (gamepad1.b);
        FL.setPower(1);
        if (gamepad1.y);
        BR.setPower(1);
        if (gamepad1.x);
        BL.setPower(1);
        if (gamepad1.dpad_down); //these two are the ladders IE: RL = Right Ladder
        RL.setPower(1);
        if (gamepad1.dpad_left);
        LL.setPower(1);
        if (gamepad1.dpad_right); //these two are the arms IE: RA = Right Arm
        RA.setPower(1);
        if (gamepad1.dpad_up);
        LA.setPower(1);
    }

}
