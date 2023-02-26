package me.youhavetrouble.veto;

import me.youhavetrouble.jankwebserver.JankWebServer;
import me.youhavetrouble.veto.endpoint.phonenumber.PhoneNumberEndpoint;
import me.youhavetrouble.veto.endpoint.vatnumber.VatNumberEndpoint;
import me.youhavetrouble.veto.endpoint.vin.VinEndpoint;

import java.io.IOException;
import java.util.logging.Logger;

public class Veto {

    public static Logger logger = Logger.getLogger("Veto");

    private static int port = 80;
    private static int threads = 16;

    public static void main(String[] args) throws IOException {

        for (String arg : args) {
            if (arg.startsWith("port=")) {
                arg = arg.replaceFirst("port=", "");
                try {
                    port = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    logger.severe(String.format("Could not parse port number from arg port=%s", arg));
                    System.exit(1);
                }
            }
            if (arg.startsWith("threads=")) {
                arg = arg.replaceFirst("threads=", "");
                try {
                    threads = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    logger.severe(String.format("Could not parse thread count from arg threads=%s", arg));
                    System.exit(1);
                }
            }
        }

        JankWebServer webServer = JankWebServer.create(port, threads);

        webServer.registerEndpoint(new PhoneNumberEndpoint());
        webServer.registerEndpoint(new VatNumberEndpoint());
        webServer.registerEndpoint(new VinEndpoint());

        webServer.start();

    }

}
