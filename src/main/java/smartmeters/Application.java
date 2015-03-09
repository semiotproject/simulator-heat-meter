package smartmeters;

import java.util.*;

import org.aeonbits.owner.ConfigFactory;
import org.eclipse.californium.core.CoapResource;

import madkit.kernel.Madkit;

/**
 * Created by nikolay on 19.02.15.
 */

public class Application implements IListener {

    private SmartMetersConfig simulationConfig  = ConfigFactory.create(SmartMetersConfig.class);
    private int start_port                      = simulationConfig.start_port();

    private static Map<Integer, List<CoapResource>> handlers = new HashMap<Integer, List<CoapResource>>();

    public static void main(String[] args) {
        new Application();
    }

    public Application() {
        TestimonialStore.getInstance().addListener(this);
        new Madkit(
                "--launchAgents",
                HeatMeterSPT943_4.class.getName() + ",false," + Integer.toString(simulationConfig.meters_count())
        );
    }

    @Override
    public void onCreated(int _id) {
        int port = start_port++;
        Server server = new Server(port);

        CoapResource temperature = new TemperatureResource(port, _id);
        CoapResource heat = new HeatResource(port, _id);

        server.add(temperature);
        server.add(heat);

        server.start();

        handlers.put(_id, Arrays.asList(temperature, heat));
        System.out.println("New meter registered and available on localhost:" + Integer.toString(port));
    }

    @Override
    public void onUpdated(int id, Testimonial t) {
        System.out.println("Meter with id " + id + " updated; heat = " + t.getHeat());
        if (handlers.containsKey(id)) {
            for (CoapResource resource : handlers.get(id)) {
                resource.changed();
            }
        }
    }



}
