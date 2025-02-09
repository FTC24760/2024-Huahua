package org.firstinspires.ftc.teamcode.auto;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.roadrunner.PinpointDrive;
import org.jetbrains.annotations.NotNull;

import java.lang.Math;

@Config
@Autonomous(name = "Autonomous 2024-2025", group = "Autonomous")
public class AutonomousCode extends LinearOpMode {

    // Slide
    private DcMotor leftSlide;
    private DcMotor rightSlide;

    // Rotate
    private DcMotor leftRotate;

    // Wrist
    private Servo wrist;
    private Servo updown_wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;


    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(12, 60, Math.toRadians(180));
        PinpointDrive drive = new PinpointDrive(hardwareMap, initialPose);

        // Slide
        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Rotate
        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");

        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Make sure rotaton motor BRAKES
        leftRotate.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wrist
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");

        // Claw
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");


        Action goToBasket = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(new Vector2d(56, 54), Math.toRadians(45))
                .build();

        Action pickUpMiddleSample = drive.actionBuilder(initialPose)
                .setReversed(false)
                .splineTo(new Vector2d(-3, 54), Math.toRadians(230))
                .build();

        Action goBackToBasket = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(new Vector2d(54, 54), Math.toRadians(45))
                .build();


        waitForStart();

        if (isStopRequested()) return;

        // CLOSE CLAW
        clawLeft.setPosition(0.7);
        clawRight.setPosition(0.2);

        // GO TO BASKET
        Actions.runBlocking(goToBasket);

        // MOVE SLIDE UP
        leftSlide.setTargetPosition(4150);
        rightSlide.setTargetPosition(4150);

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftSlide.setPower(1);
        rightSlide.setPower(1);

        // wait
        sleep(1500);

        // move rotate
        updown_wrist.setPosition(1);
        wrist.setPosition(0.64);

        // Wait a bit more
        sleep(1000);

        // Open claw
        clawLeft.setPosition(0.4);
        clawRight.setPosition(0.5);

        sleep(250);

        // Close claw
        clawLeft.setPosition(0.7);
        clawRight.setPosition(0.2);

        updown_wrist.setPosition(0.69);
        wrist.setPosition(0.05);

        // wait for a sec
        sleep(250);

        leftRotate.setTargetPosition(400);
        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftRotate.setPower(1);

        // bring slide back down
        leftSlide.setTargetPosition(0);
        rightSlide.setTargetPosition(0);

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftSlide.setPower(-1);
        rightSlide.setPower(-1);

        sleep(2500);


        // -- Pick up ground sample 1 --

        Actions.runBlocking(new ParallelAction(
                pickUpMiddleSample,
                new Action() {
                    @Override
                    public boolean run(@NotNull TelemetryPacket telemetryPacket) {
                        leftRotate.setTargetPosition(2500);
                        leftRotate.setPower(1);
                        clawLeft.setPosition(0.4);
                        clawRight.setPosition(0.5);
                        updown_wrist.setPosition(0.365);
                        wrist.setPosition(0.07);
                        return true;
                    }
                }
        ));






    }
}