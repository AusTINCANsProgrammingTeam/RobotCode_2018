package frc.team2158.robot.command.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2158.robot.OperatorInterface;
import frc.team2158.robot.Robot;
import frc.team2158.robot.RobotMap;
import frc.team2158.robot.subsystem.drive.DriveSubsystem;

import java.util.logging.Logger;
//gets called every time teleop init, calls .execute every cycle

/**
 * @author William Blount
 * @version 0.0.1
 * Runs the robot, executing code every cycle.
 */
public class OperatorControl extends Command {
    //motion code here

    private DriveMode driveMode;
    private DriveSubsystem driveSubsystem;
    private Joystick joystick;

    /**
     * Instantiates the operator control
     * @param driveMode either ARCADE or TANK
     */
    public OperatorControl(DriveMode driveMode) {
        this.driveMode = driveMode;
        this.driveSubsystem = Robot.getDriveSubsystem();
        this.joystick = Robot.getOperatorInterface().getJoystick();
    }

    public DriveMode getDriveMode() {
        return driveMode;
    }

    public void toggleDriveMode(){
        switch (driveMode){

            case ARCADE:
                driveMode = DriveMode.TANK;
                break;
            case TANK:
                driveMode = DriveMode.ARCADE;
                break;
        }
    }

    @Override
    public void execute() {
        switch(driveMode) {
            case TANK:
                driveSubsystem.tankDrive(joystick.getRawAxis(0), joystick.getRawAxis(1));
                break;
            case ARCADE:
                driveSubsystem.arcadeDrive(-joystick.getRawAxis(1), -joystick.getRawAxis(2));
                break;
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
