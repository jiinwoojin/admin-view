package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@Slf4j
public class SocketUtil {
    /**
     * 포트를 기반으로 개방 여부를 확인한다.
     * @param host String, port int
     */
    public static boolean loadIsPortOpen(String host, int port){
        try{
            new Socket(host, port).close();
            return true;
        } catch (UnknownHostException e) {
            log.error(String.format("HOST : %s 로 접근 불가입니다. PING 테스트를 다시 하세요.", host));
            return false;
        } catch (IOException e) {
            log.error(String.format("HOST : %s / PORT : %d 로 접근 불가입니다. 포트 방화벽을 다시 확인하세요.", host, port));
            return false;
        }
    }
}