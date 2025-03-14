package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.monsterTGame.controller.*;
import at.fhtw.sampleapp.controller.EchoController;
import at.fhtw.sampleapp.controller.WeatherController;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserController());
        router.addService("/cards", new CardController());
        router.addService("/sessions", new AuthSessionController());
        router.addService("/battles", new BattleController());
        router.addService("/deck", new DeckController());
        router.addService("/packages", new PackageController());
        router.addService("/scoreboard", new LeaderboardController());
        router.addService("/stats", new PlayerStatsController());
        router.addService("/transactions", new PaymentController());
        router.addService("/transactions/packages", new PaymentController());

        return router;
    }
}
