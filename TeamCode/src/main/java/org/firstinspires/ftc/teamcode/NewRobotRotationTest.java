package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Rotation Test NEW")
public class NewRobotRotationTest extends LinearOpMode {
    // Mecanum Drive
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;

    // Slide
    private DcMotor leftSlide;
    private DcMotor rightSlide;

    // Rotating Things
    private DcMotor leftRotate;
    private DcMotor rightRotate;

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

        float inputType = 1;

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // Slide
        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        float powerUp;
        float powerDown;

        // Rotation
        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");
        rightRotate = hardwareMap.get(DcMotor.class, "rightRotate");


        // Wrist & Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        waitForStart();
        while (opModeIsActive()) {
            // Drive
            float driveDampen = 0.4f;

            driveY = gamepad1.left_stick_y * driveDampen * inputType;
            driveX = -gamepad1.left_stick_x * driveDampen * inputType;
            driveRX = -gamepad1.right_stick_x * driveDampen;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            telemetry.addData("Drive Controls", "Left and Right Joysticks");

            // Slide
            powerUp = gamepad1.right_trigger;
            powerDown = gamepad1.left_trigger;

            leftSlide.setPower(powerUp - powerDown);
            rightSlide.setPower(powerUp - powerDown);

            telemetry.addData("Slide", "RT/up - LT/down");

            // Rotations
            if (gamepad1.y) {
                leftRotate.setPower(1);
                rightRotate.setPower(1);
            } else if (gamepad1.x) {
                leftRotate.setPower(-1);
                rightRotate.setPower(-1);
            } else {
                leftRotate.setPower(0);
                rightRotate.setPower(0);
            }

            telemetry.addData("Rotation", "Y/X");

            // Wrist Spinning
            if (gamepad1.dpad_left) {
                wrist.setPosition(1.0);
            } else if (gamepad1.dpad_right) {
                wrist.setPosition(0.0);
            }

            telemetry.addData("Wrist Spin", "DPad Left/Right");

            // Claw
            if (gamepad1.right_bumper) {
                clawLeft.setPosition(0.22);
                clawRight.setPosition(0.78);
            } else if (gamepad1.left_bumper) {
                clawLeft.setPosition(1.0);
                clawRight.setPosition(0.0);
            }

            telemetry.addData("Claw", "RB/open - LB/close");

            // REVERSE drive
            if (gamepad1.dpad_up) {
                inputType = 1;
            } else if (gamepad1.dpad_down) {
                inputType = -1;
            }

            telemetry.addData("Drive Direction", "DPad Up/Down");

            // Add all telemetry
            telemetry.update();
        }
    }


}
