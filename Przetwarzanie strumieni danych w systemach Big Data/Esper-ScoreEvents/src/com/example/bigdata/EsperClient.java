package com.example.bigdata;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import net.datafaker.Faker;

import net.datafaker.formats.Format;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EsperClient {
    private static int randomScore(String position) {
        double[] probabilities = switch (position) {
            case "Goalkeeper" -> new double[]{0.99, 0.002, 0.002, 0.002, 0.002, 0.002};
            case "Left Back", "Right Back", "Center Back" -> new double[]{0.8, 0.1, 0.05, 0.02, 0.02, 0.01};
            case "Defensive Midfielder", "Central Midfielder", "Attacking Midfielder" -> new double[]{0.4, 0.2, 0.15, 0.1, 0.1, 0.05};
            case "Left Winger", "Right Winger", "Center Forward", "Second Striker", "Forward" -> new double[]{0.15, 0.3, 0.2, 0.15, 0.15, 0.05};
            default -> new double[]{0.2, 0.2, 0.2, 0.2, 0.2, 0};
        };



        double rand = Math.random();
        double cumulativeProbability = 0.0;
        for (int score = 0; score < probabilities.length; score++) {
            cumulativeProbability += probabilities[score];
            if (rand <= cumulativeProbability) {
                return score;
            }
        }
        // Return 0 if the probabilities list is empty or the random number is greater than 1
        return 0;
    }

    private static String xgGenerator(String position) {
        String xG="";
        xG = switch (position) {
            case "Goalkeeper" -> String.format(Locale.US, "%.2f", new Random().nextFloat()*0.01);
            case "Left Back", "Right Back", "Center Back" -> String.format(Locale.US, "%.2f", new Random().nextFloat()*0.4);
            case "Defensive Midfielder", "Central Midfielder", "Attacking Midfielder" -> String.format(Locale.US, "%.2f", new Random().nextFloat());
            case "Left Winger", "Right Winger", "Center Forward", "Second Striker", "Forward" -> String.format(Locale.US, "%.2f", new Random().nextFloat()*1.5);
            default -> String.format(Locale.US, "%.2f", new Random().nextFloat());
        };

        return xG;
    }

    public static void main(String[] args) throws InterruptedException {
        int noOfRecordsPerSec;
        int howLongInSec;
        if (args.length < 2) {
            noOfRecordsPerSec = 20;
            howLongInSec = 30;
        } else {
            noOfRecordsPerSec = Integer.parseInt(args[0]);
            howLongInSec = Integer.parseInt(args[1]);
        }

        Configuration config = new Configuration();
        CompilerArguments compilerArgs = new CompilerArguments(config);

        // Compile the EPL statement
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        EPCompiled epCompiled;
        try {
            epCompiled = compiler.compile("""
                    @public @buseventtype create json schema ScoreEvent(position string, team string,player string, goals int, xG float, ts string);
                    @name('result') @name('result') SELECT goals,position, player, team,xG
                                    FROM ScoreEvent.win:time(10 sec)
                                    """, compilerArgs);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        // Connect to the EPRuntime server and deploy the statement
        EPRuntime runtime = EPRuntimeProvider.getRuntime("http://localhost:port", config);
        EPDeployment deployment;
        try {
            deployment = runtime.getDeploymentService().deploy(epCompiled);
        }
        catch (EPDeployException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        EPStatement resultStatement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "result");

        // Add a listener to the statement to handle incoming events
        resultStatement.addListener( (newData, oldData, stmt, runTime) -> {
            for (EventBean eventBean : newData) {
                System.out.printf("R: %s%n", eventBean.getUnderlying());
            }
        });
        resultStatement.addListener((newData, oldData, stmt, runtime1) -> {
            for (EventBean eventBean : newData) {
                float xG = (float) eventBean.get("xG");
                int goals = (int) eventBean.get("goals");
                if (Math.abs(xG - goals) > 1) {
                    System.out.printf("Anomaly detected: xG = %.2f, goals = %d%n", xG, goals);
                }
                if (goals>4) {
                    System.out.printf("Scored more than 4 goals (%d)%n", goals);
                }
            }
        });



        Faker faker = new Faker();
        String record;

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + (1000L * howLongInSec)) {
            for (int i = 0; i < noOfRecordsPerSec; i++) {
                String position=faker.football().positions();
                Timestamp timestamp = faker.date().past(30, TimeUnit.SECONDS);
                record = Format.toJson()
                        .set("position", () -> position)
                        .set("team", () -> faker.football().teams())
                        .set("player", () -> faker.football().players())
                        .set("goals", () -> randomScore(position))
                        .set("xG", () -> xgGenerator(position))
                        .set("ts", timestamp::toString)
                        .build().generate();
                runtime.getEventService().sendEventJson(record, "ScoreEvent");
            }
            waitToEpoch();
        }
    }

    static void waitToEpoch() throws InterruptedException {
        long millis = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(millis) ;
        Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;
        long millis2 = instantTrunc.toEpochMilli() ;
        TimeUnit.MILLISECONDS.sleep(millis2+1000-millis);
    }
}

