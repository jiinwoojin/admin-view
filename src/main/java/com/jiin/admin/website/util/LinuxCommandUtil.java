package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class LinuxCommandUtil {
    /**
     * 리눅스 기반 커멘드 내용 메소드
     * @param linuxCommand String
     * @throws IOException
     * @note 윈도우 기반 커멘드 내용 획득 메소드 추가 필요
     */
    public static String fetchShellContextByLinuxCommand(String linuxCommand) {
        StringBuffer sb = new StringBuffer();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", linuxCommand});
        } catch (IOException e) {
            log.error("타 운영체제 기반으로 작동을 시도했습니다. 다시 시도 바랍니다.");
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
                sb.append(line);
            } catch (IOException e) {
                log.error("명령어 결과를 가져오지 못하고 있습니다. 다시 확인 바랍니다.");
                return null;
            }
        }
        return sb.toString();
    }
}
