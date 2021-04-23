package io.github.ppdzm.utils.universal.base

import java.io.{BufferedReader, InputStream, InputStreamReader}

import ch.ethz.ssh2.{ChannelCondition, Connection, StreamGobbler}
import io.github.ppdzm.utils.universal.feature.LoanPattern

/**
 * @author Created by Stuart Alex on 2021/4/19.
 */
object SSH2Utils {

    def executeShellOnRemoteServer(remoteHost: String, user: String, password: String, command: String): Unit = {
        LoanPattern.using(new Connection(remoteHost)) {
            connection =>
                connection.connect()
                val isAuthenticated = (user == null && password == null) || connection.authenticateWithPassword(user, password)
                if (isAuthenticated) {
                    val session = connection.openSession
                    session.execCommand(command)
                    System.out.println("Following message is standard out:")
                    handleInputStream(session.getStdout)
                    System.out.println("Following message is standard err:")
                    handleInputStream(session.getStderr)
                    session.waitForCondition(ChannelCondition.EXIT_STATUS, 24 * 60 * 60)
                    session.close()
                }
        }
    }

    private def handleInputStream(inputStream: InputStream): Unit = {
        val gobbler = new StreamGobbler(inputStream)
        val reader = new BufferedReader(new InputStreamReader(gobbler))
        while (true) {
            val line = reader.readLine
            if (line == null)
                return
            System.out.println(line)
        }
    }

}
