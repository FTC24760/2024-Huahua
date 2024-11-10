package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="LinearSlideMotor")
public class LinearSlideMotor extends OpMode {
    private DcMotor LSlideMotor;
    private DcMotor RSlideMotor;

    @Override
    public void init() {
        LSlideMotor = hardwareMap.get(DcMotor.class, "LSlideMotor");
        RSlideMotor = hardwareMap.get(DcMotor.class, "RSlideMotor");
    }

    public void loop() {
        if (gamepad1.a) {
            LSlideMotor.setPower(0.5);
            RSlideMotor.setPower(-0.5);
        } else {
            LSlideMotor.setPower(0.0);
            RSlideMotor.setPower(0.0);
        }
    }
}