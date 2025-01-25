package org.firstinspires.ftc.teamcode;

public class PIDFController {
    private double kP, kI, kD, kF;
    private double previousError = 0;
    private double integralSum = 0;
    private long lastTimestamp = 0;

    // Constructor
    public PIDFController(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public double calculate(double currentPosition, double targetPosition) {
        // Calculate error
        double error = targetPosition - currentPosition;

        // Get current timestamp
        long currentTimestamp = System.currentTimeMillis();

        // Calculate delta time
        double deltaTime = (lastTimestamp == 0) ? 0 : (currentTimestamp - lastTimestamp) / 1000.0;

        // Proportional term
        double pTerm = kP * error;

        // Integral term
        if (deltaTime > 0) {
            integralSum += error * deltaTime;
        }
        double iTerm = kI * integralSum;

        // Derivative term
        double derivative = (deltaTime > 0) ? (error - previousError) / deltaTime : 0;
        double dTerm = kD * derivative;

        // Feedforward term
        double fTerm = kF * targetPosition;

        // Save the error and timestamp for the next cycle
        previousError = error;
        lastTimestamp = currentTimestamp;

        // Return the sum of all terms
        return pTerm + iTerm + dTerm + fTerm;
    }

    // Method to reset the integral and previous values
    public void reset() {
        integralSum = 0;
        previousError = 0;
        lastTimestamp = 0;
    }



}