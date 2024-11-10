package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "DriveForwardAndReturn", group = "Autonomous")
public class DriveForwardAndReturn extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize your MecanumDrive
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        // Define the starting pose (assumed to be at the origin facing forward)
        Pose2d startPose = new Pose2d(0, 0, 0);

        Action go = drive.actionBuilder(startPose)
                .lineToX(500)
                .waitSeconds(3)
                .splineTo(new Vector2d(0, 0), Math.toRadians(180))
                .build();


        // Wait for the start command
        waitForStart();

        if (isStopRequested()) return;

        // Follow the forward trajectory
        Actions.runBlocking(go);
    }
}