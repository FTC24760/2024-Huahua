package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "pid test trial")
public class pidtest extends LinearOpMode {

    DcMotorEx leftRotate;
    DcMotorEx rightRotate;

    double integralSum = 0;
    double Kp = 0.028;
    double Ki = 0.2;
    double Kd = 0.0001;

    ElapsedTime timer = new ElapsedTime();
    private double lastError = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        leftRotate = hardwareMap.get(DcMotorEx.class, "leftRotate");
        rightRotate = hardwareMap.get(DcMotorEx.class , "rightRotate");

        leftRotate.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRotate.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            leftRotate.setPower(PIDControl(2500, leftRotate.getCurrentPosition()));
            rightRotate.setPower(PIDControl(-2500, rightRotate.getCurrentPosition()));

        }


    }

    public double PIDControl(double reference, double state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();
        lastError = error;

        timer.reset();

        double output = (error *Kp) + (derivative * Kd) + (integralSum * Ki);
        return output;
    }
}
