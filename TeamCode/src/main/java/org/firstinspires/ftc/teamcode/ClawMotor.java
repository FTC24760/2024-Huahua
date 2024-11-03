package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Claw Motors")
public class ClawMotor extends LinearOpMode {

    private DcMotor clawmotor1;
    private DcMotor clawmotor2;

    @Override
    public void runOpMode() {
        clawmotor1 = hardwareMap.get(DcMotor.class, "clawmotor1"); //on port 0, the motor at the very top
        clawmotor2 = hardwareMap.get(DcMotor.class, "clawmotor2"); //on port 3, the second motor

        clawmotor1.setDirection(DcMotor.Direction.FORWARD);
        clawmotor2.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {
            if(gamepad1.b) {
                clawmotor1.setTargetPosition(5000);
                clawmotor2.setTargetPosition(-5000);

                clawmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                clawmotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                clawmotor1.setPower(0.5);
                clawmotor2.setPower(0.5);

            } else {
                clawmotor1.setTargetPosition(0);
                clawmotor2.setTargetPosition(0);

                clawmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                clawmotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                clawmotor1.setPower(0.5);
                clawmotor2.setPower(0.5);
            }
        }
    }
}