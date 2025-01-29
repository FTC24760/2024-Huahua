package org.firstinspires.ftc.teamcode;
import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.lang.Math;

@Config
@Autonomous(name = "AUTO CODE 2024", group = "Autonomous")
public class AutonomousCode extends LinearOpMode {

    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(0, 0, Math.toRadians(0));
        PinpointDrive drive = new PinpointDrive(hardwareMap, initialPose);


        TrajectoryActionBuilder goToBasket = drive.actionBuilder(initialPose)
                .lineToX(50);


        waitForStart();

        if (isStopRequested()) return;


        Actions.runBlocking(
                new SequentialAction(
                        goToBasket.build()
                )
        );

        while (opModeIsActive() && !isStopRequested()) {
            drive.updatePoseEstimate();
            telemetry.addData("X", drive.pose.position.x);
            telemetry.addData("Y", drive.pose.position.y);
            telemetry.addData("Heading", drive.pose.heading);
            telemetry.update();
        }
    }
}