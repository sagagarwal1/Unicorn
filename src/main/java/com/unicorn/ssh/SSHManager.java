package com.unicorn.ssh;

import com.jcraft.jsch.*;
import com.unicorn.base.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class SSHManager {
    private JSch jsch;

    public SSHManager() {
        /**
         * Create a new Jsch object
         * This object will execute shell commands or scripts on server
         */
        jsch = new JSch();
    }

    public Session openSSHSession(String userName, String host, int port, String keyToConnect) {
        Session session = null;
        try {
            jsch.addIdentity(keyToConnect);
            session = jsch.getSession(userName, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            if (session.isConnected()) {
                System.out.println("Successfully connected!");
            }
        } catch (Exception e) {
            Logger.assertFail("Exception while creation ssh connection", e);
        }
        return session;
    }

    public void closeSSHConnection(Session session) {
        if (session != null)
            session.disconnect();
    }

    public void listTheDir(Session session, String dirPath) {
        Channel channel = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            Vector<ChannelSftp.LsEntry> directoryEntries = sftpChannel.ls(dirPath);
            for (ChannelSftp.LsEntry file : directoryEntries) {
                System.out.println(file.getFilename());
            }
        } catch (JSchException | SftpException e) {
            Logger.assertFail("Exception while listing remote dir", e);
        } finally {
            channel.disconnect();
        }
    }

    /**
     * Copy file on remote machine
     *
     * @param session
     * @param sourcePath
     * @param destinationPath
     */
    public void copyFile(Session session, String sourcePath, String destinationPath) {
        ChannelSftp sftpChannel = null;
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.put(sourcePath, destinationPath);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sftpChannel.disconnect();
        }
    }

    /**
     * Execute file on remote machine
     *
     * @param session
     * @param scriptFileName
     * @param waitTillMsgDisplay
     */
    public void executeFile(Session session, String scriptFileName, String waitTillMsgDisplay) {
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand("sh " + scriptFileName);

            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains(waitTillMsgDisplay)) {
                    System.out.println(line);
                    break;
                }
                Logger.info(line);
            }

            int exitStatus = channelExec.getExitStatus();
            channelExec.disconnect();

            if (exitStatus < 0) {
                System.out.println("Done, but exit status not set!");
            } else if (exitStatus > 0) {
                System.out.println("Done, but with error!");
            } else {
                System.out.println("Done!");
            }

        } catch (Exception e) {
            Logger.assertFail("Exception while execution file: ", e);
        } finally {
            if (channelExec.isConnected())
                channelExec.disconnect();
        }
    }

}
