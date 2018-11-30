package frc.team2158.robot.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2158.robot.Robot;

import java.util.logging.Logger;

public class TogglePIDMode extends Command {
    private static final Logger LOGGER = Logger.getLogger(TogglePIDMode.class.getName());
    @Override
    protected void initialize() {
        Robot.getDriveSubsystem().togglePIDMode();
        LOGGER.info(String.format("Toggling PID to %s", Robot.getDriveSubsystem().getPidMode()));
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
