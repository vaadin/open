package com.vaadin.open;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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
        Path cgroup = Path.of("/proc/self/cgroup");
        try  {
            return Files.readString(cgroup).contains("docker");
        } catch (Throwable t) {
            return false;
        }
    }

}
