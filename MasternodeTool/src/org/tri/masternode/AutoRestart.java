package org.tri.masternode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jcabi.ssh.Shell;
import com.jcabi.ssh.Shell.Plain;
import com.jcabi.ssh.SshByPassword;
public class AutoRestart
{
    
    public static class CoinShell {
        private Plain plain;
        private String coinCommandPrefix;
        private String explorerApiUrl;
        private String coinName;
        public CoinShell(Plain plain, String coinCommandPrefix, String explorerApiUrl)
        {
            this.plain = plain;
            this.coinCommandPrefix = coinCommandPrefix;
            this.explorerApiUrl = explorerApiUrl;
        } 
        
        public CoinShell(String config) throws FileNotFoundException, IOException {
            Properties prop = new Properties();
            prop.load(new FileInputStream(config));
            String host = prop.getProperty("host");
            int port = Integer.parseInt(prop.getProperty("port", "22"));
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            coinCommandPrefix = prop.getProperty("coinCommandPrefix");
            explorerApiUrl = prop.getProperty("explorerAPIUrl");
            coinName = prop.getProperty("coinName");
            //Shell shell = new SshByPassword("107.172.234.19", 22, "root", "ubuntu7721");
            Shell shell = new SshByPassword(host, port, username, password);
            plain = new Plain(shell);
        }
        
        public Boolean checkSync(Integer blockcount) throws IOException {
            URL url = new URL(explorerApiUrl+"getblockcount");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String explorerResult = rd.readLine().trim();
            Integer explorerBlockcount;
            try {
                explorerBlockcount = Integer.parseInt(explorerResult);
            } catch (Exception e) {
                return null;
            }
            System.out.println("explorer count: " + explorerBlockcount);
            return explorerBlockcount <= (blockcount + 10);
        }
        
        public Integer isCoindRunning() throws IOException {
            String blockcount = plain.exec(coinCommandPrefix + "-cli getblockcount").trim();
            System.out.println(blockcount);
            try {
                return Integer.parseInt(blockcount);
            } catch (Exception e) {
                return null;
            }
        }

        public boolean restartDaemon() throws IOException
        {
            String execResult = plain.exec(coinCommandPrefix + "d -daemon");
            String expectedResult = "server starting";
            if(execResult.contains(expectedResult)) {
                return true;
            }
            System.err.println("Error: " + execResult);
            return false;
        }

        public boolean reindex() throws IOException
        {
            String execResult = plain.exec(coinCommandPrefix + "d -daemon -reindex");
            String expectedResult = "server starting";
            if(execResult.contains(expectedResult)) {
                return true;
            }
            System.err.println("Error: " + execResult);
            return false;
        }
        
        public void exit() throws IOException {
            plain.exec("exit");
        }
        
    }
    
    public static List<String> loadConfigFiles(String file) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        List<String> fileList = new ArrayList<String>();
        String fileName;
        while((fileName = rd.readLine()) != null) {
            fileName = fileName.trim();
            if(!fileName.isEmpty()) {
                fileList.add(fileName);
            }
        }
        rd.close();
        return fileList;
    }
    
    public static void checkCoinServers(String file) throws IOException {
        List<String> configFiles = loadConfigFiles(file);
        for(String configFile : configFiles) {
            CoinShell coinShell = new CoinShell(configFile);
            Integer blockcount = coinShell.isCoindRunning();
            if(blockcount == null) {
                System.err.println(coinShell.coinName + " is down. restarting daemon");
                coinShell.restartDaemon();
            } else {
                Boolean checkSync = coinShell.checkSync(blockcount);
                if(checkSync == null) {
                    System.err.println(coinShell.explorerApiUrl + " is down");
                } else if(checkSync) {
                    System.out.println(coinShell.coinName + " is synced and running");
                } else {
                    System.err.println(coinShell.coinName + " node is not synced. restarting and reindexing");
                    coinShell.reindex();
                }
            }
            coinShell.exit();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        if(args.length < 1) {
            System.err.println("USAGE: AutoRestart path/coin-config-list.txt");
            return;
        }
        int sleep;
        try {
            if(args.length > 2) {
             sleep = Integer.parseInt(args[1])*60000;   
            } else {
                sleep = 600000;
            }
        } catch (Exception e) {
            sleep = 600000;
        }
        while(true) {
            System.out.println("file: " + args[0]);
            checkCoinServers(args[0]);
            Thread.sleep(600000);
        }
    }

}
