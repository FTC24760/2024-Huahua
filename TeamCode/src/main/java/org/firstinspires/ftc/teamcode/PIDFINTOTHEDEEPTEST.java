package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "schlalalalalalalalaalala")
public class PIDFINTOTHEDEEPTEST extends LinearOpMode {

    private DcMotor leftRotate;
    private DcMotor rightRotate;
    private PIDFController examplePIDF = new PIDFController(0.028, 0, 0.0001, 0.088);



    @Override
    public void runOpMode() {
        // Put all of your initialization here.
        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");
        rightRotate = hardwareMap.get(DcMotor.class, "rightRotate");

        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        waitForStart();

        int targetPosition = 2000;

        // We will use this variable to determine if we want the PIDF to run.
        boolean usePIDF = false;

        while (opModeIsActive()) {

            if (gamepad1.a ) {
                usePIDF = true;
            }

            if (usePIDF) {
                // Sets the slide motor power according to the PIDF output.
                leftRotate.setPower(examplePIDF.calculate(leftRotate.getCurrentPosition(), targetPosition));
                rightRotate.setPower(examplePIDF.calculate(rightRotate.getCurrentPosition(), targetPosition));
            }

            telemetry.addData("left rotate pos", leftRotate.getCurrentPosition());
            telemetry.addData("right rotate pos", rightRotate.getCurrentPosition());
            telemetry.update();
        }
    }
}

