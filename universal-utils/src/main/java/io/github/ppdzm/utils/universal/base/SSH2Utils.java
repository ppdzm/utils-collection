package io.github.ppdzm.utils.universal.base;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Created by Stuart Alex on 2021/6/11.
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class SSH2Utils {

    public static void executeShellOnRemoteServer(String remoteHost, String user, String password, String command) throws IOException {
        Connection connection = new Connection(remoteHost);
        connection.connect();
        boolean isAuthenticated = (user == null && password == null) || connection.authenticateWithPassword(user, password);
        if (isAuthenticated) {
            Session session = connection.openSession();
            session.execCommand(command);
            System.out.println("Following message is standard out:");
            handleInputStream(session.getStdout());
            System.out.println("Following message is standard err:");
            handleInputStream(session.getStderr());
            session.waitForCondition(ChannelCondition.EXIT_STATUS, 24 * 60 * 60);
            session.close();
        }
        connection.close();
    }

    private static void handleInputStream(InputStream inputStream) throws IOException {
        StreamGobbler gobbler = new StreamGobbler(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(gobbler));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            System.out.println(line);
        }
    }
}
