package org.firstinspires.ftc.teamcode.auto;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.roadrunner.PinpointDrive;

import java.lang.Math;

@Config
@Autonomous(name = "Autonomous 2024-2025", group = "Autonomous")
public class AutonomousCode extends LinearOpMode {

    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(12, 60, Math.toRadians(180));
        PinpointDrive drive = new PinpointDrive(hardwareMap, initialPose);


        Action goToBasket = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(new Vector2d(60, 60), Math.toRadians(45))
                .build();


        waitForStart();

        if (isStopRequested()) return;


        Actions.runBlocking(
                new SequentialAction(
                        goToBasket
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