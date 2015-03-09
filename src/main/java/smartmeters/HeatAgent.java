package smartmeters;

import madkit.kernel.Agent;
import org.aeonbits.owner.ConfigFactory;
import java.util.Random;

/**
 * Created by nikolay on 19.02.15.
 */

public class HeatAgent extends Agent {

    protected TestimonialStore store             = TestimonialStore.getInstance();
    protected int id                             = store.getNextId();
    protected Random randomGenerator             = new Random();
    protected int quarters                       = randomGenerator.nextInt(90) + 20;
    protected SmartMetersConfig simulationConfig = ConfigFactory.create(SmartMetersConfig.class);

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
        pause(randomGenerator.nextInt((simulationConfig.time_to_start()) + 1));
    }

    @Override
    protected void live() {
        while (true) {
            updateTestimonial();
            pause(simulationConfig.meters_heartbeat());
        }
    }

    @Override
    protected void end() {
        if (logger != null) {
            logger.info(this.getName() + " stopping..");
        }
    }
}