package com.codingame.gameengine.runner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.codingame.gameengine.runner.dto.GameResultDto;
import com.google.common.io.Files;

public class CommandLineInterface {

    public static void main(String[] args) {
        try {
            Options options = new Options();

            // Define required options
            options.addOption("h", false, "Print the help")
                    .addOption("p1", true, "Required. Player 1 command line.")
                    .addOption("p2", true, "Required. Player 2 command line.")
                    .addOption("s", false, "Server mode")
                    .addOption("l", true, "File output for logs")
                    .addOption("d", true, "Referee initial data");

            CommandLine cmd = new DefaultParser().parse(options, args);

            if (cmd.hasOption("h") || !cmd.hasOption("p1") || !cmd.hasOption("p2")) {
                new HelpFormatter().printHelp( 
                    "-p1 <player1 command line> -p2 <player2 command line> [-s -l <File output for logs> -d <seed>]", 
                    options);
                System.exit(0);
            }

            // Launch Game
            MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

            //Choose league level
            gameRunner.setLeagueLevel(3);

            //Add players
            String cmd1 = cmd.getOptionValue("p1", "../../AgiArene/AgiAreneWeb/AgiAreneWeb/Contests/SpringChallenge2022/RandomActionsBot/bin/Debug/netcoreapp3.1/Bot.exe");
            gameRunner.addAgent(cmd1, "Player1", null);
            
            String cmd2 = cmd.getOptionValue("p2", "../../AgiArene/AgiAreneWeb/AgiAreneWeb/Contests/SpringChallenge2022/RandomActionsBot/bin/Debug/netcoreapp3.1/Bot.exe");
            gameRunner.addAgent(cmd2, "Player2", null);

            if (cmd.hasOption("d")) {
                String[] parse = cmd.getOptionValue("d").split("=", 0);
                Long seed = Long.parseLong(parse[1]);
                gameRunner.setSeed(seed);
            } else {
                gameRunner.setSeed(System.currentTimeMillis());
            }

            GameResultDto result = gameRunner.gameResult;

			int playerCount = 0;

			for (int i = 1; i <= 2; ++i) {
				if (cmd.hasOption("p" + i)) {
					gameRunner.addAgent(cmd.getOptionValue("p" + i), cmd.getOptionValue("p" + i));
					playerCount += 1;
				}
			}


			if (cmd.hasOption("s")) {
				gameRunner.start();
			} else {
				Method initialize = GameRunner.class.getDeclaredMethod("initialize", Properties.class);
				initialize.setAccessible(true);
				initialize.invoke(gameRunner, new Properties());

				Method runAgents = GameRunner.class.getDeclaredMethod("runAgents");
				runAgents.setAccessible(true);
				runAgents.invoke(gameRunner);

				if (cmd.hasOption("l")) {
					Method getJSONResult = GameRunner.class.getDeclaredMethod("getJSONResult");
					getJSONResult.setAccessible(true);

					Files.asCharSink(Paths.get(cmd.getOptionValue("l")).toFile(), Charset.defaultCharset())
							.write((String) getJSONResult.invoke(gameRunner));
				}

				for (int i = 0; i < playerCount; ++i) {
					System.out.println(result.scores.get(i));
				}

				for (String line : result.uinput) {
					System.out.println(line);
				}
			}

            // We have to clean players process properly
            Field getPlayers = GameRunner.class.getDeclaredField("players");
            getPlayers.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Agent> players = (List<Agent>) getPlayers.get(gameRunner);

            if (players != null) {
                for (Agent player : players) {
                    Field getProcess = CommandLinePlayerAgent.class.getDeclaredField("process");
                    getProcess.setAccessible(true);
                    Process process = (Process) getProcess.get(player);

                    process.destroy();
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

} 