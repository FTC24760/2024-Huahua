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

    // Wall servo
    private Servo wallServo;

    // ---Positions---

    // Starting
    public static Pose2d initialPose = new Pose2d(0, 0, Math.toRadians(180));

    // Basket 1 (preload)
    public static Vector2d BASKET1_POSE = new Vector2d(44, -6);
    public static double BASKET1_TANGENT = Math.toRadians(45);

    // Far sample pickup (2)
    public static Vector2d FAR_SAMPLE_POSE = new Vector2d(-18, 2);
    public static double FAR_SAMPLE_TANGENT = Math.toRadians(205);

    // Basket 2 (deposit)
    public static Vector2d BASKET2_POSE = new Vector2d(13.5, -12.5);
    public static double BASKET2_TANGENT = Math.toRadians(305);

    // Middle sample pickup (3)
    public static Vector2d MIDDLE_SAMPLE_POSE = new Vector2d(-9, -9);
    public static double MIDDLE_SAMPLE_TANGENT = Math.toRadians(230);

    // Basket 3 (deposit)
    public static Vector2d BASKET3_POSE = new Vector2d(15, 0);
    public static double BASKET3_TANGENT = Math.toRadians(330);


    // RUNNING OPMODE

    @Override
    public void runOpMode() {
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

        // Wall servo
        wallServo = hardwareMap.get(Servo.class, "wallServo");


        Action goToBasket = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(BASKET1_POSE, BASKET1_TANGENT)
                .build();

        Action pickUpFarSample = drive.actionBuilder(initialPose)
                .setReversed(false)
                .splineTo(FAR_SAMPLE_POSE, FAR_SAMPLE_TANGENT)
                .build();

        Action goBackToBasket1 = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(BASKET2_POSE, BASKET2_TANGENT)
                .build();

        Action pickUpMiddleSample = drive.actionBuilder(initialPose)
                .setReversed(false)
                .splineTo(MIDDLE_SAMPLE_POSE, MIDDLE_SAMPLE_TANGENT)
                .build();

        Action goBackToBasket2 = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(BASKET3_POSE, BASKET3_TANGENT)
                .build();



        waitForStart();

        if (isStopRequested()) return;

        wallServo.setPosition(1);

        // CLOSE CLAW
        clawLeft.setPosition(0.7);
        clawRight.setPosition(0.2);


        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // GO TO BASKET
        Actions.runBlocking(new ParallelAction(
                goToBasket,
                new Action() {
                    @Override
                    public boolean run(@NotNull TelemetryPacket telemetryPacket) {
                        leftRotate.setTargetPosition(-1280);
                        leftRotate.setPower(-1);
                        if (Math.abs(leftRotate.getCurrentPosition() + 1280) < 10) {
                            leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            return false;
                        }
                        return true;
                    }
                }
        ));


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
                pickUpFarSample,
                new Action() {
                    @Override
                    public boolean run(@NotNull TelemetryPacket telemetryPacket) {
                        leftRotate.setTargetPosition(2600);
                        leftRotate.setPower(1);
                        clawLeft.setPosition(0.4);
                        clawRight.setPosition(0.5);
                        updown_wrist.setPosition(0.4);
                        wrist.setPosition(0.02);
                        if (Math.abs(leftRotate.getCurrentPosition() - 2600) < 10) {
                            return false;
                        }
                        return true;
                    }
                }
        ));

        sleep(200);

        // Close claw
        clawLeft.setPosition(0.7);
        clawRight.setPosition(0.2);
        sleep(250);

        updown_wrist.setPosition(0.5);
        wrist.setPosition(0.07);

        // -- Go back to basket
        Actions.runBlocking(new ParallelAction(
                goBackToBasket1,
                new Action() {
                    @Override
                    public boolean run(@NotNull TelemetryPacket telemetryPacket) {
                        leftRotate.setTargetPosition(-100);
                        leftRotate.setPower(-1);
                        if (Math.abs(leftRotate.getCurrentPosition() + 100) < 10) {
                            return false;
                        }
                        return true;
                    }
                }
        ));

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

        // -- Pick up ground sample 2 --

        Actions.runBlocking(new ParallelAction(
                pickUpMiddleSample,
                new Action() {
                    @Override
                    public boolean run(@NotNull TelemetryPacket telemetryPacket) {
                        leftRotate.setTargetPosition(2600);
                        leftRotate.setPower(1);
                        clawLeft.setPosition(0.4);
                        clawRight.setPosition(0.5);
                        updown_wrist.setPosition(0.405);
                        wrist.setPosition(0.02);
                        if (Math.abs(leftRotate.getCurrentPosition() - 2600) < 10) {
                            return false;
                        }
                        return true;
                    }
                }
        ));

        sleep(200);

        // Close claw
        clawLeft.setPosition(0.7);
        clawRight.setPosition(0.2);
        sleep(250);

        updown_wrist.setPosition(0.5);
        wrist.setPosition(0.07);

        // -- Go back to basket
        Actions.runBlocking(new ParallelAction(
                goBackToBasket2,
                new Action() {
                    @Override
                    public boolean run(@NotNull TelemetryPacket telemetryPacket) {
                        leftRotate.setTargetPosition(-100);
                        leftRotate.setPower(-1);
                        if (Math.abs(leftRotate.getCurrentPosition() + 100) < 10) {
                            return false;
                        }
                        return true;
                    }
                }
        ));

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





    }
}