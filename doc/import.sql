BEGIN;

INSERT INTO BASELINE(NAME) VALUES
    ('buildsys-build'),
    ('java'),
    ('java-devel'),
    ('eclipse');

INSERT INTO BASELINE_PKGS(BASELINE_NAME, PKGS) VALUES
    ('buildsys-build', 'bash'), ('buildsys-build', 'bzip2'), ('buildsys-build', 'coreutils'), ('buildsys-build', 'cpio'),
    ('buildsys-build', 'diffutils'), ('buildsys-build', 'fedora-release'), ('buildsys-build', 'findutils'), ('buildsys-build', 'gawk'),
    ('buildsys-build', 'gcc'), ('buildsys-build', 'gcc-c++'), ('buildsys-build', 'grep'), ('buildsys-build', 'gzip'),
    ('buildsys-build', 'info'), ('buildsys-build', 'make'), ('buildsys-build', 'patch'), ('buildsys-build', 'redhat-rpm-config'),
    ('buildsys-build', 'rpm-build'), ('buildsys-build', 'sed'), ('buildsys-build', 'shadow-utils'), ('buildsys-build', 'tar'),
    ('buildsys-build', 'unzip'), ('buildsys-build', 'util-linux'), ('buildsys-build', 'which'), ('buildsys-build', 'xz'),
    ('java', 'java-1.8.0-openjdk'),
    ('java-devel', 'java-1.8.0-openjdk-devel'),
    ('eclipse', 'eclipse-platform');

INSERT INTO BASELINE_BASELINE(BASELINE_NAME, PARENTS_NAME) VALUES
    ('eclipse', 'java-devel');

INSERT INTO COLLECTION(NAME, LOCATION) VALUES
    ('f22', 'https://kojipkgs.fedoraproject.org/repos/f22-build/latest/{arch}/'),
    ('f23', 'https://kojipkgs.fedoraproject.org/repos/f23-build/latest/{arch}/'),
    ('f24', 'https://kojipkgs.fedoraproject.org/repos/f24-build/latest/{arch}/'),
    ('f25', 'https://kojipkgs.fedoraproject.org/repos/f21-build/latest/{arch}/');

INSERT INTO PACKAGE(NAME, UPSTREAM, UPSTREAMDOWNLOADSIZE, UPSTREAMINSTALLSIZE, UPSTREAMVERSION, BASELINE_NAME) VALUES
    ('maven', 'http://maven.apache.org/', 8042383, 9533094, '3.3.3', 'java-devel'),
    ('ant', 'http://ant.apache.org/', 4351965, 37939729, '1.9.4', 'java-devel'),
    ('eclipse-jdt', 'https://www.eclipse.org/', 160990943, 182863820, '4.4.2', 'java-devel'),
    ('jenkins', 'https://jenkins-ci.org/', 63121651, 73292627, '1.615', 'java-devel'),
    ('gradle', 'http://gradle.org/', 45383133, 51291589, '2.4', 'java-devel'),
    ('groovy', 'http://www.groovy-lang.org/', 31968599, 35756588, '2.4.3', 'java-devel'),
    ('freemind', 'http://sourceforge.net/projects/freemind/', 37583505, 41406322, '1.0.1', 'java'),
    ('maven-local', '', 0, 0, '0', 'buildsys-build'),
    ('gradle-local', '', 0, 0, '0', 'buildsys-build');

INSERT INTO COLLECTION_PACKAGE(PACKAGES_NAME, COLLECTIONS_NAME)
    SELECT PACKAGE.NAME, COLLECTION.NAME FROM PACKAGE, COLLECTION;

COMMIT;
