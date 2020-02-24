package com.jiin.admin.website.gis;

import java.util.Map;

public interface StatusService {
    Map<String, String> centerStatusCheck();
    Map<String, String> centerSynchronizeCheck();
}
