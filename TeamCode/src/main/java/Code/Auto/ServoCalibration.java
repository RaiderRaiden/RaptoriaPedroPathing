package Code.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name="ServoCal")
public class ServoCalibration extends OpMode {

    private Servo Wrist;
    public void init(){
        Wrist = hardwareMap.servo.get("W");

    }
    public void loop(){
        Wrist.setPosition(0.5);
    }

}
