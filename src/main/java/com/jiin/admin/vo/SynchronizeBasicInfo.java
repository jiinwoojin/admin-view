package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SynchronizeBasicInfo {
    private String serverName;

    private String pgsqlOSMStatus;
    private String pgsqlBasicStatus;
    private String pgpoolStatus;
    private String syncthingStatus;

    private String withSyncDBStatus;
    private String withSyncFileStatus;

    public SynchronizeBasicInfo() {
        this.serverName = "UNKNOWN";
        this.pgsqlOSMStatus = "UNKNOWN";
        this.pgsqlBasicStatus = "UNKNOWN";
        this.pgpoolStatus = "UNKNOWN";
        this.syncthingStatus = "UNKNOWN";
        this.withSyncDBStatus = "UNKNOWN";
        this.withSyncFileStatus = "UNKNOWN";
    }

    public SynchronizeBasicInfo(String serverName, String pgsqlOSMStatus, String pgsqlBasicStatus, String pgpoolStatus, String syncthingStatus, String withSyncDBStatus, String withSyncFileStatus) {
        this.serverName = serverName;
        this.pgsqlOSMStatus = pgsqlOSMStatus;
        this.pgsqlBasicStatus = pgsqlBasicStatus;
        this.pgpoolStatus = pgpoolStatus;
        this.syncthingStatus = syncthingStatus;
        this.withSyncDBStatus = withSyncDBStatus;
        this.withSyncFileStatus = withSyncFileStatus;
    }
}
