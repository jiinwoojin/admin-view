package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class SocketUtil {
    /**
     * TCP 포트를 기반으로 개방 여부를 확인한다.
     * @param host String, port int
     */
    public static boolean loadIsTcpPortOpen(String host, int port){
        try {
            new Socket(host, port).close();
            return true;
        } catch (UnknownHostException e) {
            log.error("ERROR - " + e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return false;
        }
    }

    /**
     * UDP 포트를 기반으로 개방 여부를 확인한다. (Syncthing UDP 확인 대비)
     * @param port int
     */
    public static boolean loadIsUdpPortOpen(int port){
        try {
            new DatagramSocket(port).close();
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }
}
