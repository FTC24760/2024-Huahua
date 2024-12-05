package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceRunner;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import org.firstinspires.ftc.teamcode.drive.GobildaMecanumDrive;


@Config
@Autonomous(group = "2024 AUTO CODE")
public class AutonomousCode extends LinearOpMode {

    private DcMotor leftSlide;
    private DcMotor rightSlide;

    private Servo clawLeft;
    private Servo clawRight;


    @Override
    public void runOpMode() throws InterruptedException {

        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        GobildaMecanumDrive drive = new GobildaMecanumDrive(hardwareMap);

        Pose2d currentPose = new Pose2d(0, 0, 0);

        TrajectorySequence goToBasket = drive.trajectorySequenceBuilder(currentPose)
                .splineTo(new Vector2d(4, 48), Math.toRadians(135))
                .build();

        TrajectorySequence goBack = drive.trajectorySequenceBuilder(currentPose)
                .back(12)
                .build();


        waitForStart();

        if (opModeIsActive() && !isStopRequested()) {
            drive.followTrajectorySequence(goToBasket);


            // GO UP
            leftSlide.setTargetPosition(2000);
            rightSlide.setTargetPosition(2000);

            leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            leftSlide.setPower(-1);
            rightSlide.setPower(-1);


            while (leftSlide.isBusy()) {}
            sleep(500);

            // OPEN CLAW1
            clawLeft.setPosition(0.7);
            clawRight.setPosition(0.3);

            sleep(1000);
            clawLeft.setPosition(0.3);
            clawRight.setPosition(0.7);

            sleep(500);



            // GO BACK
            leftSlide.setTargetPosition(0);
            rightSlide.setTargetPosition(0);

            leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            leftSlide.setPower(1);
            rightSlide.setPower(1);

            while (leftSlide.isBusy()) {}

            drive.followTrajectorySequence(goBack);

        }
    }
}