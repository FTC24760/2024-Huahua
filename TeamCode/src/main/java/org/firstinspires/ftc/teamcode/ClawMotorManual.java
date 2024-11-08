package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name = "Manual Claw Motor")
public class ClawMotorManual extends LinearOpMode{
    private DcMotor clawmotor1;
    private DcMotor clawmotor2;

    @Override
    public void runOpMode() {
        clawmotor1 = hardwareMap.get(DcMotor.class, "clawmotor1");
        clawmotor2 = hardwareMap.get(DcMotor.class, "clawmotor2");

        clawmotor1.setDirection(DcMotor.Direction.FORWARD);
        clawmotor2.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {
            double power1 = -gamepad1.left_stick_y;
            double power2 = -gamepad1.right_stick_y;

            clawmotor1.setPower(power1);
            clawmotor2.setPower(power2);

        }
        clawmotor1.setPower(0);
        clawmotor2.setPower(0);
    }
}
