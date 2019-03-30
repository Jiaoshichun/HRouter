package com.heng.hroutercomiler;

import com.heng.routerannotation.HRouterConfig;

public class BasicConfigurations {
    public String baseUrl;
    public String pack = "com.heng.hrouterapi.rule";

    public BasicConfigurations(HRouterConfig config) {
        if (config == null) return;

        if (!Utils.isEmpty(config.pack())) {
            this.pack = config.pack();
        }
        this.baseUrl = parseBaseUrl(config);
    }

    private String parseBaseUrl(HRouterConfig config) {
        if (!Utils.isEmpty(config.baseUrl())) {
            return config.baseUrl();
        }
        return "";
    }
}
