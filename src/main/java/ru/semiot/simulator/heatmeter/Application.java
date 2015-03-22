package ru.semiot.simulator.heatmeter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aeonbits.owner.ConfigFactory;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;


import static ru.semiot.simulator.heatmeter.SmartMetersConfig.conf;

import madkit.kernel.Madkit;

public class Application implements IListener {

    private static final Map<Integer, List<CoapResource>> handlers = new HashMap<>();
    private CoapClient notifier;
    private int start_port;

    public static void main(String[] args) {
        if (args.length > 0 && !args[0].isEmpty()) {
            conf.setConfigFromFile(args[0]);
        }
        new Application();
    }

    public Application() {
        start_port = conf.getStartPort();
        notifier = new CoapClient(conf.getRegisterURI());
        TestimonialStore.getInstance().addListener(this);
        new Madkit(
                "--launchAgents",
                HeatMeterSPT943_4.class.getName() + ",false,"
                + Integer.toString(conf.getMetersCount())
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
        server.add(new DescriptionResource(_id));

        server.start();

        handlers.put(_id, Arrays.asList(temperature, heat));
        String payload = "New meter registered and available on localhost:"
                + Integer.toString(port);
        System.out.println(payload);

        notifier.post(payload, MediaTypeRegistry.TEXT_PLAIN);
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
