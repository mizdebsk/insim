/*-
 * Copyright (c) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fedoraproject.insim.backend;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.fedoraproject.javadeptools.hawkey.HawkeyException;
import org.fedoraproject.javadeptools.hawkey.Sack;
import org.fedoraproject.javadeptools.librepo.YumRepo;

/**
 * @author Mikolaj Izdebski
 */
class RepoCache implements AutoCloseable {

    private static final String DIGEST_ALGORITHM = "SHA-1";

    private final String url;
    private final String arch;
    private final MessageDigest digest;
    private final HexBinaryAdapter hexAdapter;
    private Path cachePath;
    private Path repomdPath;
    private Path primaryPath;
    private Path filelistsPath;

    public RepoCache(String url, String arch) {
        this.url = url;
        this.arch = arch;

        try {
            digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            hexAdapter = new HexBinaryAdapter();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Digest algorithm " + DIGEST_ALGORITHM + " is not available", e);
        }
    }

    public Path getCachePath() throws IOException {
        if (cachePath == null) {
            cachePath = Files.createTempDirectory("insim-repo-");
            YumRepo yumRepo = new YumRepo(url.replaceAll("\\{arch\\}", arch));
            yumRepo.download(cachePath);
        }

        return cachePath;
    }

    private Path getMdFilePath(String name) throws IOException {
        Path base = getCachePath().resolve("repodata");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(base)) {
            for (Path child : stream) {
                if (child.getFileName().toString().contains(name)) {
                    return getCachePath().resolve(child);
                }
            }

            throw new IOException(name + " was not found in repository " + arch);
        }
    }

    public Path getRepomdPath() throws IOException {
        if (repomdPath == null) {
            repomdPath = getMdFilePath("repomd.xml");
        }

        return repomdPath;
    }

    public Path getPrimaryPath() throws IOException {
        if (primaryPath == null) {
            primaryPath = getMdFilePath("primary.xml");
        }

        return primaryPath;
    }

    public Path getFilelistsPath() throws IOException {
        if (filelistsPath == null) {
            filelistsPath = getMdFilePath("filelists.xml");
        }

        return filelistsPath;
    }

    public void loadIntoSack(Sack sack) throws HawkeyException, IOException {
        sack.loadRepo(arch, getRepomdPath(), getPrimaryPath(), getFilelistsPath());
    }

    private void hash(Path path) throws IOException {
        digest.update(Files.readAllBytes(path));
    }

    public String hash() throws IOException {
        hash(getRepomdPath());
        hash(getPrimaryPath());
        hash(getFilelistsPath());
        return hexAdapter.marshal(digest.digest());
    }

    @Override
    public void close() throws IOException {
        if (cachePath != null) {
            Files.delete(getRepomdPath());
            Files.delete(getPrimaryPath());
            Files.delete(getFilelistsPath());
            Files.delete(cachePath.resolve("repodata"));
            Files.delete(cachePath);
        }
    }

}
