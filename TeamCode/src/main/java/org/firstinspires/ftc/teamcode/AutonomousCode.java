package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceRunner;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import org.firstinspires.ftc.teamcode.drive.GobildaMecanumDrive;


@Config
@Autonomous(group = "2024 AUTO CODE")
public class AutonomousCode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        GobildaMecanumDrive drive = new GobildaMecanumDrive(hardwareMap);

        TrajectorySequence trajectory0 = drive.trajectorySequenceBuilder(new Pose2d(-12.94, -61.19, Math.toRadians(152.06)))
                .splineTo(new Vector2d(-60.74, -60.28), Math.toRadians(224.41))
                .build();



        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            drive.followTrajectorySequence(trajectory0);
        }
    }
}