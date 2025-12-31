package com.trio.test;

import com.trio.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests unitaires pour Team et TeamGame
 */
public class TeamGameTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       TESTS TEAM GAME - DÉBUT          ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Tests Team
        testTeamCreation();
        testTeamWithList();
        testTeamTrioHolder();
        testTeamHasPlayer();

        // Tests TeamGame
        testTeamGameCreation();
        testPlayOrderWith4Players();
        testPlayOrderWith6Players();
        testTeamGameValidation();

        // Résumé
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           RÉSULTATS DES TESTS          ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Tests exécutés: " + testsRun);
        System.out.println("Tests réussis:  " + testsPassed);
        System.out.println("Tests échoués:  " + (testsRun - testsPassed));

        if (testsPassed == testsRun) {
            System.out.println("\n✅ TOUS LES TESTS PASSENT !");
        } else {
            System.out.println("\n❌ CERTAINS TESTS ONT ÉCHOUÉ");
        }
    }

    // ===== TESTS TEAM =====

    private static void testTeamCreation() {
        testsRun++;
        System.out.println("Test: Création d'une équipe avec 2 joueurs...");

        Player p1 = new User("Alice");
        Player p2 = new Bot("Bot1");
        Team team = new Team("Équipe A", p1, p2);

        boolean success = team.getName().equals("Équipe A")
                && team.getTeamSize() == 2
                && team.getPlayer(0) == p1
                && team.getPlayer(1) == p2;

        if (success) {
            System.out.println("  ✓ Équipe créée correctement");
            testsPassed++;
        } else {
            System.out.println("  ✗ Échec création équipe");
        }
    }

    private static void testTeamWithList() {
        testsRun++;
        System.out.println("Test: Création d'une équipe avec List<Player>...");

        List<Player> players = new ArrayList<>();
        players.add(new User("Alice"));
        players.add(new Bot("Bot1"));
        Team team = new Team("Équipe B", players);

        boolean success = team.getTeamSize() == 2
                && team.getPlayers().size() == 2;

        if (success) {
            System.out.println("  ✓ Équipe créée avec List correctement");
            testsPassed++;
        } else {
            System.out.println("  ✗ Échec création équipe avec List");
        }
    }

    private static void testTeamTrioHolder() {
        testsRun++;
        System.out.println("Test: Team implémente TrioHolder...");

        Team team = new Team("Test", new User("A"), new Bot("B"));

        // Vérifier que getTrioCount() retourne 0 au départ
        boolean initialCount = team.getTrioCount() == 0;

        // Ajouter un trio
        Deck trio = new Deck();
        trio.addCard(new Card(5, "A1", ""));
        trio.addCard(new Card(5, "A2", ""));
        trio.addCard(new Card(5, "A3", ""));
        team.addTrio(trio);

        boolean afterAdd = team.getTrioCount() == 1;

        if (initialCount && afterAdd) {
            System.out.println("  ✓ TrioHolder fonctionne correctement");
            testsPassed++;
        } else {
            System.out.println("  ✗ Échec TrioHolder");
        }
    }

    private static void testTeamHasPlayer() {
        testsRun++;
        System.out.println("Test: Team.hasPlayer()...");

        Player p1 = new User("Alice");
        Player p2 = new Bot("Bot1");
        Player p3 = new Bot("Bot2");
        Team team = new Team("Test", p1, p2);

        boolean success = team.hasPlayer(p1)
                && team.hasPlayer(p2)
                && !team.hasPlayer(p3);

        if (success) {
            System.out.println("  ✓ hasPlayer() fonctionne correctement");
            testsPassed++;
        } else {
            System.out.println("  ✗ Échec hasPlayer()");
        }
    }

    // ===== TESTS TEAMGAME =====

    private static void testTeamGameCreation() {
        testsRun++;
        System.out.println("Test: Création TeamGame avec 2 équipes...");

        List<Team> teams = createTeams(4);
        TeamGame game = new TeamGame(teams, new Deck());

        boolean success = game.getTeams().size() == 2
                && game.getPlayOrder().size() == 4;

        if (success) {
            System.out.println("  ✓ TeamGame créé correctement");
            testsPassed++;
        } else {
            System.out.println("  ✗ Échec création TeamGame");
        }
    }

    private static void testPlayOrderWith4Players() {
        testsRun++;
        System.out.println("Test: Ordre de jeu avec 4 joueurs (2 équipes)...");

        Player p1 = new User("A1");
        Player p2 = new Bot("A2");
        Player p3 = new Bot("B1");
        Player p4 = new Bot("B2");

        Team teamA = new Team("Équipe A", p1, p2);
        Team teamB = new Team("Équipe B", p3, p4);

        List<Team> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        TeamGame game = new TeamGame(teams, new Deck());
        List<Player> order = game.getPlayOrder();

        // Ordre attendu: A1, B1, A2, B2
        boolean success = order.size() == 4
                && order.get(0) == p1 // A1
                && order.get(1) == p3 // B1
                && order.get(2) == p2 // A2
                && order.get(3) == p4; // B2

        System.out.println("  Ordre obtenu: " + orderToString(order));
        System.out.println("  Ordre attendu: [A1, B1, A2, B2]");

        if (success) {
            System.out.println("  ✓ Ordre de jeu correct pour 4 joueurs");
            testsPassed++;
        } else {
            System.out.println("  ✗ Ordre de jeu incorrect");
        }
    }

    private static void testPlayOrderWith6Players() {
        testsRun++;
        System.out.println("Test: Ordre de jeu avec 6 joueurs (3 équipes)...");

        Player p1 = new User("A1");
        Player p2 = new Bot("A2");
        Player p3 = new Bot("B1");
        Player p4 = new Bot("B2");
        Player p5 = new Bot("C1");
        Player p6 = new Bot("C2");

        Team teamA = new Team("Équipe A", p1, p2);
        Team teamB = new Team("Équipe B", p3, p4);
        Team teamC = new Team("Équipe C", p5, p6);

        List<Team> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);
        teams.add(teamC);

        TeamGame game = new TeamGame(teams, new Deck());
        List<Player> order = game.getPlayOrder();

        // Ordre attendu: A1, B1, C1, A2, B2, C2
        boolean success = order.size() == 6
                && order.get(0) == p1 // A1
                && order.get(1) == p3 // B1
                && order.get(2) == p5 // C1
                && order.get(3) == p2 // A2
                && order.get(4) == p4 // B2
                && order.get(5) == p6; // C2

        System.out.println("  Ordre obtenu: " + orderToString(order));
        System.out.println("  Ordre attendu: [A1, B1, C1, A2, B2, C2]");

        if (success) {
            System.out.println("  ✓ Ordre de jeu correct pour 6 joueurs");
            testsPassed++;
        } else {
            System.out.println("  ✗ Ordre de jeu incorrect");
        }
    }

    private static void testTeamGameValidation() {
        testsRun++;
        System.out.println("Test: Validation nombre d'équipes (doit rejeter 1 ou 4+ équipes)...");

        boolean exceptionFor1Team = false;
        boolean exceptionFor4Teams = false;

        // Test avec 1 équipe (doit échouer)
        try {
            List<Team> oneTeam = new ArrayList<>();
            oneTeam.add(new Team("A", new User("A1"), new Bot("A2")));
            new TeamGame(oneTeam, new Deck());
        } catch (IllegalArgumentException e) {
            exceptionFor1Team = true;
        }

        // Test avec 4 équipes (doit échouer)
        try {
            List<Team> fourTeams = new ArrayList<>();
            fourTeams.add(new Team("A", new User("A1"), new Bot("A2")));
            fourTeams.add(new Team("B", new Bot("B1"), new Bot("B2")));
            fourTeams.add(new Team("C", new Bot("C1"), new Bot("C2")));
            fourTeams.add(new Team("D", new Bot("D1"), new Bot("D2")));
            new TeamGame(fourTeams, new Deck());
        } catch (IllegalArgumentException e) {
            exceptionFor4Teams = true;
        }

        if (exceptionFor1Team && exceptionFor4Teams) {
            System.out.println("  ✓ Validation correcte (rejette 1 et 4+ équipes)");
            testsPassed++;
        } else {
            System.out.println("  ✗ Validation incorrecte");
            System.out.println("    Exception pour 1 équipe: " + exceptionFor1Team);
            System.out.println("    Exception pour 4 équipes: " + exceptionFor4Teams);
        }
    }

    // ===== HELPERS =====

    private static List<Team> createTeams(int nbPlayers) {
        List<Team> teams = new ArrayList<>();
        int nbTeams = nbPlayers / 2;

        for (int i = 0; i < nbTeams; i++) {
            Player p1 = (i == 0) ? new User("User") : new Bot("Bot" + (i * 2 - 1));
            Player p2 = new Bot("Bot" + (i * 2));
            teams.add(new Team("Équipe " + (char) ('A' + i), p1, p2));
        }

        return teams;
    }

    private static String orderToString(List<Player> order) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < order.size(); i++) {
            sb.append(order.get(i).getPseudo());
            if (i < order.size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
