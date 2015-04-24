package com.connectedworldservices.nectr.v2.api.rest.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

import com.connectedworldservices.nectr.v2.api.rest.support.NeCTRv2Utils;

public abstract class AbstractSCMService {

    protected File basedir;

    @Value("${test.data.git.repo.uri:}")
    protected String uri;

    protected String username;

    protected String password;

    private String[] searchPaths = new String[0];

    public AbstractSCMService() {
        this.basedir = createBaseDir();
    }

    private File createBaseDir() {
        try {
            return NeCTRv2Utils.createTempDirectory("nectr-v2-git-");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create temp dir", e);
        }
    }

    public void setUri(String uri) {
        String withoutTraillingSlashUri = uri;
        while (withoutTraillingSlashUri.endsWith("/")) {
            withoutTraillingSlashUri = withoutTraillingSlashUri.substring(0, uri.length() - 1);
        }
        this.uri = withoutTraillingSlashUri;
    }

    public String getUri() {
        return uri;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir.getAbsoluteFile();
    }

    public File getBasedir() {
        return basedir;
    }

    public void setSearchPaths(String... searchPaths) {
        this.searchPaths = searchPaths;
    }

    public String[] getSearchPaths() {
        return searchPaths;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected File getWorkingDirectory() {
        if (uri.startsWith("file:")) {
            try {
                return new UrlResource(StringUtils.cleanPath(uri)).getFile();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot convert uri to file: " + uri);
            }
        }
        return basedir;
    }

    protected String[] getSearchLocations(File dir) {
        List<String> locations = new ArrayList<String>();
        locations.add(dir.toURI().toString());
        for (String path : searchPaths) {
            File file = new File(getWorkingDirectory(), path);
            if (file.isDirectory()) {
                locations.add(file.toURI().toString());
            }
        }
        return locations.toArray(new String[0]);
    }
}