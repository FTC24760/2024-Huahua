package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Config
@TeleOp
public class PIDF_ARM_TUNE extends OpMode {
    private PIDController controller;
    public static double p = 0, i = 0, d= 0;
    public static double f=0;

    public static int target = 0;
    private final double ticks_in_degrees = 700 / 180.0;

    private DcMotor leftRotate;

    @Override
    public void init() {
        controller = new PIDController(p, i, d);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");
    }

    @Override
    public void loop() {
        controller.setPID(p,i,d);
        int armPos = leftRotate.getCurrentPosition();
        double pid = controller.calculate(armPos, target);
        double ff = Math.cos(Math.toRadians(target / ticks_in_degrees)) * f;

        double power = pid + ff;

        leftRotate.setPower(power);

        telemetry.addData("pos: ", armPos);
        telemetry.addData("target: ", target);
        telemetry.update();
    }
}


