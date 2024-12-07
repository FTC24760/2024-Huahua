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

    // Wrist Spin
    private Servo wrist;
    private Servo updown_wrist;

    private DcMotor leftRotate;


    @Override
    public void runOpMode() throws InterruptedException {

        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");

        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");

        GobildaMecanumDrive drive = new GobildaMecanumDrive(hardwareMap);

        Pose2d currentPose = new Pose2d(0, 0, 0);

        TrajectorySequence goToBasket = drive.trajectorySequenceBuilder(currentPose)
                .strafeLeft(4)
                .back(58)
                .turn(Math.toRadians(65))
                .build();

        TrajectorySequence turnAroundAndPickUpOne = drive.trajectorySequenceBuilder(currentPose)
                .forward(6)
                .turn(Math.toRadians(65))
                .forward(21)
                .build();

        TrajectorySequence goBackToBasket = drive.trajectorySequenceBuilder(currentPose)
                .back(24)
                .turn(Math.toRadians(-65))
                .back(9)
                .build();


        waitForStart();

        if (opModeIsActive() && !isStopRequested()) {

            clawLeft.setPosition(0.3);
            clawRight.setPosition(0.7);
            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.6);
            drive.followTrajectorySequence(goToBasket);

            // GO UP

            leftRotate.setTargetPosition(100);

            leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftRotate.setPower(-1);


            leftSlide.setTargetPosition(2200);
            rightSlide.setTargetPosition(2200);

            leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            leftSlide.setPower(-1);
            rightSlide.setPower(-1);


            sleep(1300);

            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.25);
            sleep(300);

            // OPEN CLAW1
            clawLeft.setPosition(0.7);
            clawRight.setPosition(0.3);

            sleep(300);
            clawLeft.setPosition(0.3);
            clawRight.setPosition(0.7);
            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.6);

            leftRotate.setTargetPosition(0);

            leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftRotate.setPower(1);

            sleep(300);



            // GO BACK
            leftSlide.setTargetPosition(0);
            rightSlide.setTargetPosition(0);

            leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            leftSlide.setPower(1);
            rightSlide.setPower(1);

            sleep(1200);

            // Turn around!!


            drive.followTrajectorySequence(turnAroundAndPickUpOne);

            leftRotate.setTargetPosition(-2500);

            leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftRotate.setPower(-1);

            sleep(1300);

            clawLeft.setPosition(0.7);
            clawRight.setPosition(0.3);
            sleep(250);
            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.8);

            sleep(300);
            clawLeft.setPosition(0.3);
            clawRight.setPosition(0.7);

            sleep(250);

            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.4);

            sleep(300);

            leftRotate.setTargetPosition(0);

            leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftRotate.setPower(1);

            sleep(2000);
            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.6);

            // go back

            drive.followTrajectorySequence(goBackToBasket);

            // GO UP

            leftRotate.setTargetPosition(100);

            leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftRotate.setPower(-1);


            leftSlide.setTargetPosition(2200);
            rightSlide.setTargetPosition(2200);

            leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            leftSlide.setPower(-1);
            rightSlide.setPower(-1);


            while (leftSlide.isBusy()) {
            }

            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.25);
            sleep(300);

            // OPEN CLAW1
            clawLeft.setPosition(0.7);
            clawRight.setPosition(0.3);

            sleep(300);
            clawLeft.setPosition(0.3);
            clawRight.setPosition(0.7);
            wrist.setPosition(0.565);
            updown_wrist.setPosition(0.6);

            leftRotate.setTargetPosition(0);

            leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftRotate.setPower(1);


            sleep(300);



            // GO BACK
            leftSlide.setTargetPosition(0);
            rightSlide.setTargetPosition(0);

            leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            leftSlide.setPower(1);
            rightSlide.setPower(1);

            sleep(1200);
        }
    }
}