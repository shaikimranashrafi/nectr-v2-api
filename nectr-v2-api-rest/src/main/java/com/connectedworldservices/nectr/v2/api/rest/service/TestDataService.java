package com.connectedworldservices.nectr.v2.api.rest.service;

import static org.springframework.util.StringUtils.hasText;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Session;

@Slf4j
@Service
public class TestDataService extends AbstractSCMService {

    private boolean initialized;

    public List<String> loadTestData(String label) {
        initialize();

        Git git = null;
        try {
            git = createGitClient();
            return loadTestTestDataInternal(label, git);
        } catch (GitAPIException e) {
            throw new IllegalStateException("Cannot clone repository", e);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load test data", e);
        } finally {
            try {
                if (git != null) {
                    git.getRepository().close();
                }
            } catch (Exception e) {
                log.warn("Could not close git repository", e);
            }
        }
    }

    private synchronized List<String> loadTestTestDataInternal(String label, Git git) throws GitAPIException, IOException {
        git.getRepository().getConfig().setString("branch", label, "merge", label);

        Ref ref = checkout(git, label);

        if (shouldPull(git, ref)) {
            pull(git, label, ref);
        }

        List<String> result = new ArrayList<>();

        Path dir = getWorkingDirectory().toPath();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{zip}")) {
            for (Path entry : stream) {
                result.add(entry.toAbsolutePath().toString());
            }
        }

        return result;
    }

    private Ref checkout(Git git, String label) throws GitAPIException {
        CheckoutCommand checkout = git.checkout();
        if (shouldTrack(git, label)) {
            trackBranch(git, checkout, label);
        } else {
            // works for tags and local branches
            checkout.setName(label);
        }
        return checkout.call();
    }

    private boolean shouldPull(Git git, Ref ref) throws GitAPIException {
        return git.status().call().isClean() && ref != null && git.getRepository().getConfig().getString("remote", "origin", "url") != null;
    }

    private boolean shouldTrack(Git git, String label) throws GitAPIException {
        return isBranch(git, label) && !isLocalBranch(git, label);
    }

    /**
     * Assumes we are on a tracking branch (should be safe)
     */
    private void pull(Git git, String label, Ref ref) {
        PullCommand pull = git.pull();
        try {
            if (hasText(username)) {
                setCredentialsProvider(pull);
            }
            pull.call();
        } catch (Exception e) { //NOSONAR
            log.warn("Could not pull remote for " + label + " (current ref=" + ref + "), remote: " + git.getRepository().getConfig().getString("remote", "origin", "url"));
        }
    }

    private Git createGitClient() throws IOException, GitAPIException {
        if (new File(basedir, ".git").exists()) {
            return openGitRepository();
        } else {
            return copyRepository();
        }
    }

    private Git copyRepository() throws IOException, GitAPIException {
        deleteBaseDirIfExists();
        Assert.state(basedir.mkdirs(), "Could not create basedir: " + basedir);
        if (uri.startsWith("file:")) {
            return copyFromLocalRepository();
        } else {
            return cloneToBasedir();
        }
    }

    private Git openGitRepository() throws IOException {
        Git git = Git.open(getWorkingDirectory());
        tryFetch(git);
        return git;
    }

    private Git copyFromLocalRepository() throws IOException {
        Git git;
        File remote = new UrlResource(StringUtils.cleanPath(uri)).getFile();
        Assert.state(remote.isDirectory(), "No directory at " + uri);
        File gitDir = new File(remote, ".git");
        Assert.state(gitDir.exists(), "No .git at " + uri);
        Assert.state(gitDir.isDirectory(), "No .git directory at " + uri);
        git = Git.open(remote);
        return git;
    }

    private Git cloneToBasedir() throws GitAPIException {
        CloneCommand clone = Git.cloneRepository().setURI(uri).setDirectory(basedir);
        if (hasText(username)) {
            setCredentialsProvider(clone);
        }
        return clone.call();
    }

    private void tryFetch(Git git) {
        try {
            FetchCommand fetch = git.fetch();
            if (hasText(username)) {
                setCredentialsProvider(fetch);
            }
            fetch.call();
        } catch (Exception e) { //NOSONAR
            log.warn("Remote repository not available");
        }
    }

    private void deleteBaseDirIfExists() {
        if (basedir.exists()) {
            try {
                FileUtils.delete(basedir, FileUtils.RECURSIVE);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialize base directory", e);
            }
        }
    }

    private void initialize() {
        if (uri.startsWith("file:") && !initialized) {
            SshSessionFactory.setInstance(new JschConfigSessionFactory() {
                @Override
                protected void configure(Host hc, Session session) {
                    session.setConfig("StrictHostKeyChecking", "no");
                }
            });
            initialized = true;
        }
    }

    private void setCredentialsProvider(TransportCommand<?, ?> cmd) {
        cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
    }

    private void trackBranch(Git git, CheckoutCommand checkout, String label) {
        checkout.setCreateBranch(true).setName(label).setUpstreamMode(SetupUpstreamMode.TRACK).setStartPoint("origin/" + label);
    }

    private boolean isBranch(Git git, String label) throws GitAPIException {
        return containsBranch(git, label, ListMode.ALL);
    }

    private boolean isLocalBranch(Git git, String label) throws GitAPIException {
        return containsBranch(git, label, null);
    }

    private boolean containsBranch(Git git, String label, ListMode listMode) throws GitAPIException {
        ListBranchCommand command = git.branchList();
        if (listMode != null) {
            command.setListMode(listMode);
        }
        List<Ref> branches = command.call();
        for (Ref ref : branches) {
            if (ref.getName().endsWith("/" + label)) {
                return true;
            }
        }
        return false;
    }
}