package com.jiin.admin.website.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

public class SystemInfoUtil {
    private static final long MB = 1024L * 1024L;
    private static final long GB = 1024L * 1024L * 1024L;

    public static Map<String, Object> systemBasicInfo(){
        // 드라이브 공간 사용량
        File cDrive = new File("/");
        System.out.println(String.format("Total space: %.2f GB", (double)cDrive.getTotalSpace() / GB));
        System.out.println(String.format("Free space: %.2f GB", (double)cDrive.getFreeSpace() / GB));
        System.out.println(String.format("Usable space: %.2f GB", (double)cDrive.getUsableSpace() / GB));

        // JVM Heap 메모리 사용량
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println(String.format("Initial memory: %.2f GB", (double) memoryMXBean.getHeapMemoryUsage().getInit() / GB));
        System.out.println(String.format("Used heap memory: %.2f GB", (double) memoryMXBean.getHeapMemoryUsage().getUsed() / GB));
        System.out.println(String.format("Max heap memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getMax() / GB));
        System.out.println(String.format("Committed memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /GB));

        // CPU 사용량
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for(Long threadID : threadMXBean.getAllThreadIds()) {
            ThreadInfo info = threadMXBean.getThreadInfo(threadID);
            System.out.println("Thread name: " + info.getThreadName());
            System.out.println("Thread State: " + info.getThreadState());
            System.out.println(String.format("CPU time: %s ns", threadMXBean.getThreadCpuTime(threadID)));
        }

        Map<String, Object> map = new HashMap<>();
        return map;
    }
}
