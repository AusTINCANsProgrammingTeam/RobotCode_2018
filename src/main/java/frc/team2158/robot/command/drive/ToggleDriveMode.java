package frc.team2158.robot.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2158.robot.Robot;

import java.util.logging.Logger;

public class ToggleDriveMode extends Command {
    private static final Logger LOGGER = Logger.getLogger(ToggleDriveMode.class.getName());

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void initialize() {
        Robot.getOperatorControl().toggleDriveMode();
        LOGGER.info("Toggled GearMode to " + Robot.getOperatorControl().getDriveMode().toString());
    }
}
