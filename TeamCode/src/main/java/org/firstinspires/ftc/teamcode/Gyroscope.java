package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;
import kotlin.DslMarker;

@Autonomous(name = "Gyroscope Testing")
@Disabled
public class Gyroscope extends LinearOpMode
{
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;


    private GyroSensor gyro;

    private int lastRawGyroHeading_ = 0;
    private int gyroHeading_ = 0;


    private int getGyroHeading()
    {
        int rawGyroHeading = gyro.getHeading();

        // Rollover from 0 to 359 detected.
        if (rawGyroHeading - 180 > lastRawGyroHeading_)
        {
            gyroHeading_ = gyroHeading_ + rawGyroHeading - 360 - lastRawGyroHeading_;
        }
        // Roll over from 359 to 0 detected.
        else if (rawGyroHeading + 180 < lastRawGyroHeading_)
        {
            gyroHeading_ = gyroHeading_ + rawGyroHeading + 360 - lastRawGyroHeading_;
        }
        // No rollover detected, add the change in angle to the know value.
        else
        {
            gyroHeading_ =
                    gyroHeading_ + rawGyroHeading - lastRawGyroHeading_;
        }

        lastRawGyroHeading_ = rawGyroHeading;
        return gyroHeading_;
    }


    private void resetGyroHeading()
    {
        lastRawGyroHeading_ = 0;
        gyroHeading_ = 0;
        gyro.resetZAxisIntegrator();
    }

    private void turn(double power, int angle)
    {
        resetGyroHeading();

        if (opModeIsActive())
        {
            while (Math.abs(angle) < getGyroHeading() && opModeIsActive())
            {

                frontRight.setPower(power * (angle / Math.abs(angle)));
                backRight.setPower((power * (angle / Math.abs(angle))));
                frontLeft.setPower(-power * (angle / Math.abs(angle)));
                backLeft.setPower(-power * (angle / Math.abs(angle)));

                idle();
                sleep(50);
            }
        }

        frontRight.setPower(0.0);
        frontLeft.setPower(0.0);
        backRight.setPower(0.0);
        backLeft.setPower(0.0);
    }

    @Override public void runOpMode() throws InterruptedException
    {
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backRight =hardwareMap.get(DcMotor.class, "backRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);


        gyro = hardwareMap.gyroSensor.get("gyro");

        gyro.calibrate();

        sleep(1000);

        while(gyro.isCalibrating())
        {
            idle();
            sleep(50);
        }

        waitForStart();

        resetGyroHeading();

        turn(1.0, 90);
        sleep(5000);
        turn(1.0,  -90);
    }
}