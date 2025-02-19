package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class LConstants {
    static {
        TwoWheelConstants.forwardTicksToInches = 0.002946369864;
        TwoWheelConstants.strafeTicksToInches = 0.002950440456;;
        TwoWheelConstants.forwardY = -1283.8877953; //3.8877953
        TwoWheelConstants.strafeX = 1160.549213; //3.799213
        TwoWheelConstants.forwardEncoder_HardwareMapName = "ODO";
        TwoWheelConstants.strafeEncoder_HardwareMapName = "EM";
        TwoWheelConstants.forwardEncoderDirection = Encoder.REVERSE;
        TwoWheelConstants.strafeEncoderDirection = Encoder.REVERSE;
        TwoWheelConstants.IMU_HardwareMapName = "imu";
        TwoWheelConstants.IMU_Orientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD);

       /* DriveEncoderConstants.forwardTicksToInches = 0.002975796154;
        DriveEncoderConstants.strafeTicksToInches = 0.002994935496;
        DriveEncoderConstants.turnTicksToInches = 1.003541217;

        DriveEncoderConstants.robot_Width = 12.87402;
        DriveEncoderConstants.robot_Length = 6.59375;

        DriveEncoderConstants.leftFrontEncoderDirection = Encoder.REVERSE;
        DriveEncoderConstants.rightFrontEncoderDirection = Encoder.FORWARD;
        DriveEncoderConstants.leftRearEncoderDirection = Encoder.REVERSE;
        DriveEncoderConstants.rightRearEncoderDirection = Encoder.FORWARD;*/
    }
}




