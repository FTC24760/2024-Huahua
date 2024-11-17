package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "old revamped")
public class OldIntoTheDeepRevamped extends LinearOpMode {
    // Mecanum Drive
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;

    // Slide
    private DcMotor leftSlide;
    private DcMotor rightSlide;

    // Top Arm
    private DcMotor topArm;

    // Bottom Arm
    private DcMotor bottomArm;

    // Wrist Spin
    private Servo wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;

    @Override
    public void runOpMode() {
        // Drive
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        float driveY;
        double driveX;
        float driveRX;
        double driveDenominator;



        // Slide
        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        float powerUp;
        float powerDown;

        // Top Arm
        topArm = hardwareMap.get(DcMotor.class, "topArm");

        // Bottom Arm
        bottomArm = hardwareMap.get(DcMotor.class, "bottomArm");

        // Wrist & Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        boolean clawOpen = false;
        boolean xPressed = false;


        waitForStart();
        while (opModeIsActive()) {
            // Drive
            float driveDampen = 0.4f;

            driveY = -gamepad1.left_stick_y * driveDampen;
            driveX = gamepad1.left_stick_x * driveDampen;
            driveRX = -gamepad1.right_stick_x * driveDampen;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            telemetry.addData("Drive Controls", "Left and Right Joysticks");


            // Slide
            if (gamepad1.a) {
                leftSlide.setPower(0.5);
                rightSlide.setPower(0.5);
            } else {
                leftSlide.setPower(-0.3);
                rightSlide.setPower(-0.3);
            }

            telemetry.addData("Slide", "a/UP - let go of a/DOWN");


            // Top Arm
            if (gamepad1.right_trigger > 0) {
                topArm.setPower(-0.5);  // Move forward
            } else if (gamepad1.right_bumper) {
                topArm.setPower(0.5);  // Move backward
            } else {
                topArm.setPower(0.0);  // Stop
            }

            telemetry.addData("Top Arm", "Right Trigger/Up - Right Bumper/Down");

            // Bottom Arm
            if (gamepad1.left_trigger > 0) {
                bottomArm.setPower(0.5);
            } else if (gamepad1.left_bumper) {
                bottomArm.setPower(-0.5);
            } else {
                bottomArm.setPower(0.0);
            }

            telemetry.addData("Bottom Arm", "Left trigger/Up - Left Bumper/Down");

            // Wrist Spinning
            if (gamepad1.dpad_left) {
                wrist.setPosition(1.0);
            } else if (gamepad1.dpad_right) {
                wrist.setPosition(0.0);
            }

            telemetry.addData("Wrist Spin", "DPad Left/Right");

            if (gamepad1.x && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad1.x) {
                xPressed = false;
            }

            // Claw
            if (clawOpen) {
                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.3);
            } else {
                clawLeft.setPosition(0.3);
                clawRight.setPosition(0.7);
            }

            telemetry.addData("Claw", "X toggle");

            // Add all telemetry
            telemetry.update();
        }
    }


}
