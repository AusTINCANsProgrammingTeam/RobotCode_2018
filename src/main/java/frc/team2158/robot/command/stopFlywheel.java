package frc.team2158.robot.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2158.robot.Robot;

import java.util.logging.Logger;

public class stopFlywheel extends Command {
    private static final Logger LOGGER = Logger.getLogger(stopFlywheel.class.getName());

    public stopFlywheel() {
        requires(Robot.getFlywheelSubsystem());
    }

        @Override
        protected void initialize() {
            Robot.getFlywheelSubsystem().stopFlywheel();
        }

        @Override
        protected boolean isFinished() {
            return true;
        }
    }
