/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.compassmate.reports.model;

/**
 *
 * @author baasanbatpurevjal
 */
public class FuelReport extends BaseReport {

    public FuelReport() {
    }

    private double firstFuelVelocity;

    public void setFirstFuelVelocity(double firstFuelVelocity) {
        this.firstFuelVelocity = firstFuelVelocity;
    }

    public double getFirstFuelVelocity() {
        return this.firstFuelVelocity;
    }

    private double lastFuelVelocity;

    public void setLastFuelVelocity(double lastFuelVelocity) {
        this.lastFuelVelocity = lastFuelVelocity;
    }

    public double getLastFuelVelocity() {
        return this.lastFuelVelocity;
    }
}
