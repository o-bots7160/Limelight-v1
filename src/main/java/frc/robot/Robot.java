/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
 
package frc.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry thor = table.getEntry("thor");
  NetworkTableEntry tvert = table.getEntry("tvert");
  NetworkTableEntry ts1 = table.getEntry("ts1");
  NetworkTableEntry ts0 = table.getEntry("ts0");
  NetworkTableEntry tv = table.getEntry("tv");
  NetworkTableEntry camTran = table.getEntry("camtran");


  Joystick _joystick = new Joystick(0);
  


  double distance = 0;
  double x = 0;
  double isThereATarget = 0;
  double driveSpeed = 0;
  static double maxDriveSpeed = 0.5;
  static double minDriveSpeed = 0.3;
  double driveTurn = 0;
  static double minTurnSpeed = 0.5;
  double[] helpme = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
  double driveDistance;

  //LiftController _lift = new LiftController(false, _joystick);
  
  ADXRS450_Gyro gyro = new ADXRS450_Gyro();

  DriveTrain drive = new DriveTrain();

  double turnAngle;

  enum driveModes{
    MANUAL, AUTO
  }
  enum autoModes{
    GETVAULES, TURNRIGHTONE, TURNLEFTONE, DRIVE,TURNRIGHTTWO, TURNLEFTTWO
  }


  @Override
  public void robotInit() {
    gyro.calibrate();
  }

  @Override
  public void robotPeriodic() {

    //System.out.println(autoModes);
    //System.out.println(drive.get());
    //read values periodically
    x = tx.getDouble(0.0); //angle from crosshair (-27° to 27°) in the x direction
    double y = ty.getDouble(0.0); //angle from crosshair (-27° to 27°) in the y direction
    double area = ta.getDouble(0.0);
    double width = thor.getDouble(0.0);
    double height = tvert.getDouble(0.0);
    double skew1 = ts1.getDouble(0.0);
    double skew0 = ts0.getDouble(0.0);
    isThereATarget = tv.getDouble(0.0);
    distance = (272.695621739*5.75/height + 264*14/width)/2;
    helpme = camTran.getDoubleArray(helpme);
  


    


    
    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    SmartDashboard.putNumber("LimelightWidth", width);
    SmartDashboard.putNumber("LimelightHeight", height);
    SmartDashboard.putNumber("LimelightDistance",distance);
    SmartDashboard.putNumber("LimelightSkew1", skew1);
    SmartDashboard.putNumber("LimelightSkew0", skew0);
    //SmartDashboard.putNumberArray("DOES THIS WORK", helpme);
    SmartDashboard.putNumber("x distance", helpme[0]);
    //SmartDashboard.putNumber("DOES THIS WORK1", helpme[1]);
    /*
    SmartDashboard.putNumber("y distanc", helpme[2]);
    SmartDashboard.putNumber("DOES THIS WORK3", helpme[3]);
    SmartDashboard.putNumber("DOES THIS WORK4", helpme[4]);
    SmartDashboard.putNumber("DOES THIS WORK5", helpme[5]);*/
    
    SmartDashboard.putNumber("Gyro", gyro.getAngle());
    
    //System.out.println(test);


  }

  @Override
  public void disabledInit() {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
  }


  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  driveModes driveModes;
  autoModes autoModes;

  @Override
  public void teleopInit() {
    drive.reset();
    driveModes = driveModes.MANUAL;
    autoModes = autoModes.GETVAULES;
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
  }

  @Override
  public void teleopPeriodic() {
    switch(driveModes){
      case MANUAL:
        drive.run(_joystick.getY(), _joystick.getZ());
        if(_joystick.getRawButton(5))
          driveModes = driveModes.AUTO;
        break;
      case AUTO:
        switch(autoModes){
          case GETVAULES:
            driveDistance = Math.abs(helpme[1]);
            if(helpme[3] < 0){
              turnAngle = 90 - Math.round(Math.toDegrees(Math.asin(Math.abs(helpme[3])/distance)));
              System.out.println(Math.round(Math.toDegrees(Math.asin(Math.abs(helpme[3])/distance))));
              System.out.println(turnAngle);
              gyro.reset();
              autoModes = autoModes.TURNRIGHTONE;
            }else if(helpme[3] > 0){
              turnAngle = -(90 - Math.round(Math.toDegrees(Math.asin(Math.abs(helpme[3])/distance))));
              System.out.println(Math.round(Math.toDegrees(Math.asin(Math.abs(helpme[3])/distance))));
              System.out.println(turnAngle);
              gyro.reset();
              autoModes = autoModes.TURNLEFTONE;
            }
            break;
          case TURNRIGHTONE:
            //NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
            if(gyro.getAngle() <= turnAngle)
              drive.run(0, 1);
            else{
              System.out.println(gyro.getAngle());
              gyro.reset();
              drive.reset();
              autoModes = autoModes.DRIVE;
            }
            break;
          case TURNLEFTONE:
            if(gyro.getAngle() >= turnAngle)
                drive.run(0, -1);
              else{
                System.out.println(gyro.getAngle());
                gyro.reset();
                drive.reset();
                autoModes = autoModes.DRIVE;
              }
            break;
          
          }
          break;
        }
    
  }
    

}
/*case DRIVE:
              drive.run(-1, 0);
              if(drive.get()>=driveDistance)
                driveModes = driveModes.MANUAL; 
                autoModes = autoModes.GETVAULES;
            break;*/

