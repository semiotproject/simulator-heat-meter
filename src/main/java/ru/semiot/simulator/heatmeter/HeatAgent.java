package ru.semiot.simulator.heatmeter;

import madkit.kernel.Agent;
import org.aeonbits.owner.ConfigFactory;
import java.util.Random;
import java.util.logging.Level;

import static ru.semiot.simulator.heatmeter.SmartMetersConfig.conf;

public class HeatAgent extends Agent {

    protected TestimonialStore store = TestimonialStore.getInstance();
    protected int id = store.getNextId();
    protected Random randomGenerator = new Random();
    protected int quarters = randomGenerator.nextInt(90) + 20;

    protected void updateTestimonial() {
        store.setData(calculateTemperature(), calculateHeat(), id);
    }

    protected Double calculateTemperature() {
        return 0.0;
    }

    protected Double calculateHeat() {
        return 0.0;
    }

    @Override
    protected void activate() {
        pause((randomGenerator.nextInt((conf.getTimeToStart())) + 1) * 1000);
    }

    @Override
    protected void live() {
        while (true) {
            updateTestimonial();
            pause(conf.getMetersHeartbeat());
        }
    }

    @Override
    protected void end() {
        if (logger != null) {
            logger.log(Level.INFO, "{0} stopping..", this.getName());
        }
    }
}
