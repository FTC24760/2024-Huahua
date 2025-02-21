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
import com.qualcomm.hardware.dfrobot.HuskyLens;
import org.firstinspires.ftc.teamcode.roadrunner.PinpointDrive;
import org.jetbrains.annotations.NotNull;

import java.lang.Math;

@Config
@Autonomous(name = "AUTO ALIGN WITH VISION (WIP)", group = "Autonomous")
public class AutonomousAutoAlignWithVision extends LinearOpMode {

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

    // Huskylens
    private HuskyLens rearVision;

    // Offset setup
    public static double OFFSET_X = 0;
    public static double OFFSET_Y = 0;


    @Override
    public void runOpMode() {
        // TODO: rear wall targets are 13 (blue) and 14 (red)

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

        // Huskylens (vision)
        // TODO map this on the driver station
        rearVision = hardwareMap.get(HuskyLens.class, "rearVision");

        rearVision.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);


        waitForStart();
        // Get blocks
        HuskyLens.Block tag = rearVision.blocks()[0];
        Action goToBasket = drive.actionBuilder(initialPose)
                .setReversed(true)
                .splineTo(new Vector2d(tag.x + OFFSET_X, tag.y + OFFSET_Y), Math.toRadians(45))
                .build();

        if (isStopRequested()) return;

        // GO TO BASKET
        Actions.runBlocking(goToBasket);
    }
}