package frc.team2158.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2158.robot.command.drive.DriveMode;
import frc.team2158.robot.command.drive.OperatorControl;
import frc.team2158.robot.command.drive.ToggleGearMode;
import frc.team2158.robot.command.intake.*;
import frc.team2158.robot.command.lift.MoveLift;
import frc.team2158.robot.subsystem.GripPipeline;
import frc.team2158.robot.subsystem.drive.DriveSubsystem;
import frc.team2158.robot.subsystem.drive.GearMode;
import frc.team2158.robot.subsystem.drive.TalonSRXGroup;
import frc.team2158.robot.subsystem.intake.IntakeSubsystem;
import frc.team2158.robot.subsystem.lift.LiftSubsystem;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;

import java.util.logging.Logger;
//TODO Rename some classes <- Billy's job.
//TODO Lua macros

/**
 * @author William Blount
 * @version 0.0.1
 * The main class of our code.
 * Initializes the teleOperated code.
 */
public class Robot extends TimedRobot {
    Preferences prefs;
    CameraServer server;
    public static int g_exp;
    private SendableChooser<Double> autoChooser;
    private static final Logger LOGGER = Logger.getLogger(Robot.class.getName());
    private static final LoggingSystem LOGGING_SYSTEM = LoggingSystem.getInstance();

    private static DriveSubsystem driveSubsystem;
    private static LiftSubsystem liftSubsystem;
    private static IntakeSubsystem intakeSubsystem;

    private static OperatorInterface operatorInterface;
    private static final int IMG_WIDTH = 320;
    private static final int IMG_HEIGHT = 240;

    private VisionThread visionThread;
    private double centerX = 0.0;

    private final Object imgLock = new Object();
    private Spark blinkin = new Spark(6);

    /**
     *Initializes the TalonSRX Groups, the Solenoid, Operator Interface, Lift Motors, and Intake Motors.
     */
    @Override
    public void robotInit()  {
        //prefs = Preferences.getInstance();
        //g_exp = prefs.getInt("exp", 1);

        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
        camera.setBrightness(1);
        camera.setExposureManual(20);
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
        // Initialize the auto chooser system
        autoChooser = new SendableChooser<>();
        autoChooser.addObject("0.0", 0.0);
        autoChooser.addObject("0.25", 0.25);
        autoChooser.addObject("0.50", 0.5);
        autoChooser.addObject("0.75", 0.75);
        autoChooser.addDefault("1.0", 1.0);
        autoChooser.addObject("1.25", 1.25);
        autoChooser.addObject("1.50", 1.5);
        autoChooser.addObject("1.75", 1.75);
        autoChooser.addObject("2.0", 2.0);
        autoChooser.addObject("2.25", 2.25);
        autoChooser.addObject("2.50", 2.50);
        autoChooser.addObject("2.75", 2.75);
        autoChooser.addObject("3.0", 3.5);
        /*
            ----TODO THIS IS WHAT YOU COPY/PASTE TO ADD MORE DATA----
            autoChooser.addObject("#.#", #.#);
         */
        SmartDashboard.putData("Time to run forward in auto!", autoChooser);

        // Initialize the drive subsystem.
        driveSubsystem = new DriveSubsystem(
                new TalonSRXGroup(
                        new WPI_TalonSRX(RobotMap.LEFT_MOTOR_1), // This motor is the master for the left side.
                        new WPI_TalonSRX(RobotMap.LEFT_MOTOR_2),
                        new WPI_TalonSRX(RobotMap.LEFT_MOTOR_3)
                ),
                new TalonSRXGroup(
                        new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_1), // This motor is the master for the right side.
                        new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_2),
                        new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_3)
                ),
                new DoubleSolenoid(RobotMap.PCM_ADDRESS, RobotMap.GEARBOX_FORWARD_CHANNEL,
                        RobotMap.GEARBOX_REVERSE_CHANNEL)
        );
        LOGGER.info("Drive Subsystem Initialized properly!");

        // Initialize the lift subsystem.
        liftSubsystem = new LiftSubsystem(
                new SpeedControllerGroup(
                        new Spark(RobotMap.LIFT_MOTOR_1),
                        new Spark(RobotMap.LIFT_MOTOR_2),
                        new Spark(RobotMap.LIFT_MOTOR_3)
                ),
                true
        );
        LOGGER.info("Lift Subsystem Initialized properly!");
        // Initialize the intake subsystem.
        intakeSubsystem = new IntakeSubsystem(
                new Spark(RobotMap.LEFT_INTAKE_MOTOR),
                new Spark(RobotMap.RIGHT_INTAKE_MOTOR),
                new Spark(RobotMap.PIVOT_INTAKE_MOTOR),
                new DoubleSolenoid(RobotMap.PCM_ADDRESS, RobotMap.INTAKE_SOLENOID_1, RobotMap.INTAKE_SOLENOID_2)
        );
        LOGGER.info("Intake Subsystem Initialized properly!");
        // Initialize the operator interface.
        operatorInterface = new OperatorInterface();

        visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
            if (!pipeline.filterContoursOutput().isEmpty()) {
                LOGGER.warning("e2");
                Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    centerX = r.x + (r.width / 2);
                }
            }
            else{
                centerX = 0.0;
            }
        });
        visionThread.start();

        LOGGER.info("Robot initialization completed.");
    }

    /**
     * Returns the instance of the drive subsystem.
     * @return the instance of the drive subsystem.
     */
    @Override
    public void autonomousInit() {
        timer.reset();
        timer.start();
    }

    @Override
    public void autonomousPeriodic() {

        double centerX;
        synchronized (imgLock) {
            centerX = this.centerX;
        }
        double turn = centerX - (IMG_WIDTH / 2);
        LOGGER.info(Double.toString(centerX));
        if((centerX == 0) || (centerX < 50 && centerX > 30)){
            getDriveSubsystem().arcadeDrive(0,0);
        }
        else if(centerX >= 50){
            LOGGER.warning("o5");
            getDriveSubsystem().arcadeDrive(-.3, turn * 0.005);}
        else if(centerX <= 30)
        {
            LOGGER.warning("06");
            getDriveSubsystem().arcadeDrive(-.3, turn * -0.004);
        }
    }

    public static DriveSubsystem getDriveSubsystem() {
        if(driveSubsystem != null) {
            return driveSubsystem;
        }
        throw new RuntimeException("Drive subsystem has not yet been initialized!");
    }

    /**
     * Returns the instance of the lift subsystem.
     * @return the instance of the lift subsystem.
     */
    public static LiftSubsystem getLiftSubsystem() {
        if(liftSubsystem != null) {
            return liftSubsystem;
        }
        throw new RuntimeException("Lift subsystem has not yet been initialized!");
    }

    /**
     * Returns the instance of the intake subsystem.
     * @return the instance of the intake subsystem.
     */
    public static IntakeSubsystem getIntakeSubsystem() {
        if(intakeSubsystem != null) {
            return intakeSubsystem;
        }
        throw new RuntimeException("Intake subsystem has not yet been initialized!");
    }

    /**
     * Returns the instance of the Operator Interface.
     * @return the instance of the Operator Interface.
     */
    public static OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    /**
     * Initializes the TeleOp Robot State and binds the buttons that will be used in the controller.
     */
    @Override
    public void teleopInit() {

        LOGGER.info("Teleop Init!");
        operatorInterface.bindButton("buttonLB", OperatorInterface.ButtonMode.WHILE_HELD, new Intake());
        operatorInterface.bindButton("buttonLT", OperatorInterface.ButtonMode.WHILE_HELD, new Outtake());
        //operatorInterface.bindButton("buttonLT", OperatorInterface.ButtonMode.WHILE_HELD, new TestIntake(false));
        operatorInterface.bindButton("buttonY", OperatorInterface.ButtonMode.WHEN_PRESSED, new ToggleIntakeSolenoid());
        operatorInterface.bindButton("buttonRB", OperatorInterface.ButtonMode.WHILE_HELD, new MoveLift(LiftSubsystem.Direction.UP, LiftSubsystem.DEFAULT_LIFT_UP_SPEED));
        //operatorInterface.bindButton("buttonRT", OperatorInterface.ButtonMode.WHILE_HELD, new MoveLift(LiftSubsystem.Direction.DOWN, LiftSubsystem.DEFAULT_LIFT_DOWN_SPEED));
        //operatorInterface.bindButton("buttonRT", OperatorInterface.ButtonMode.WHILE_HELD, new TestIntake(true));
        operatorInterface.bindButton("buttonX", OperatorInterface.ButtonMode.WHILE_HELD, new CounterClockwise());
        operatorInterface.bindButton("buttonB", OperatorInterface.ButtonMode.WHILE_HELD, new Clockwise());
        operatorInterface.bindButton("buttonA", OperatorInterface.ButtonMode.WHEN_PRESSED, new ToggleGearMode());
        operatorInterface.bindButton("buttonBack", OperatorInterface.ButtonMode.WHILE_HELD, new PivotDown());
        operatorInterface.bindButton("buttonStart", OperatorInterface.ButtonMode.WHILE_HELD, new PivotUp());
        Scheduler.getInstance().add(new OperatorControl(DriveMode.ARCADE));
        // Stretch Goal: Make the button bindings come from an xml/json config.
        //how would we implement such a system?
    }
    /**
     * Runs the TeleOp Periodic code.
     */
    private static double solid_red = 0.61;
    private static double solid_blue = 0.87;
    private static double light_chase_red = -0.31;
    private static double light_chase_blue = -0.29;
    private static double shot_red = -0.85;
    private static double shot_blue = -0.83;
    private static double fast_rainbow = -0.57;
    private static double solid_yellow = 0.69;
    private static double solid_orange = 0.63;
    private static double color1_strobe = 0.15;
    private static double color2_strobe = 0.35;
    private static double strobe_red = -0.11;
    private static double strobe_blue = -0.9;
    private static double breath_red = -0.17;
    private static double breath_blue = -0.15;

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        GearMode gearMode = getDriveSubsystem().getGearMode();

        switch(getIntakeSubsystem().getSolenoidState()) {
            case kForward: //intake is closed - dont strobe
                if(gearMode.equals(GearMode.HIGH)){
                    blinkin.set(solid_red);
                } else if(gearMode.equals(GearMode.LOW)) {
                    blinkin.set(solid_blue);
                } else {
                    blinkin.set(0.9);
                }
                break;
            case kReverse: //intake is open - strobe
                if(gearMode.equals(GearMode.HIGH)){
                    blinkin.set(breath_red);
                } else if(gearMode.equals(GearMode.LOW)) {
                    blinkin.set(breath_blue);
                } else {
                    blinkin.set(0.9);
                }
                break;
            case kOff:

                break;
        }


    }
    private static Timer timer = new Timer();

}
