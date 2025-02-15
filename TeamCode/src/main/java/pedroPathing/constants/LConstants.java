package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class LConstants {
    static {
        TwoWheelConstants.forwardTicksToInches = 0.002988467787; //Still need to do the
        TwoWheelConstants.strafeTicksToInches = 0.0029970542101870087;;
        TwoWheelConstants.forwardY = 4.84252;
        TwoWheelConstants.strafeX = 3.14961;
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




