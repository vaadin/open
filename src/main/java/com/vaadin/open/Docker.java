package com.vaadin.open;

import java.io.File;

// Port of https://github.com/sindresorhus/is-docker/blob/main/index.js
public class Docker {

    private static Boolean docker = null;

    public static boolean isDocker() {
        if (docker == null) {
            docker = hasDockerEnv() || hasDockerCGroup();
        }
        return docker;
    }

    private static boolean hasDockerEnv() {
        return new File("/.dockerenv").exists();
    }

    private static boolean hasDockerCGroup() {
        File cgroup = new File("/proc/self/cgroup");
        try  {
            return FileUtil.readFile(cgroup).contains("docker");
        } catch (Throwable t) {
            return false;
        }
    }

}
