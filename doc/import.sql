BEGIN;

INSERT INTO BASELINE(NAME) VALUES
    ('java'),
    ('java-devel'),
    ('eclipse');

INSERT INTO BASELINE_PKGS(BASELINE_NAME, PKGS) VALUES
    ('java', 'java-1.8.0-openjdk'),
    ('java-devel', 'java-1.8.0-openjdk-devel'),
    ('eclipse', 'eclipse-platform'), ('eclipse', 'java-1.8.0-openjdk-devel');

INSERT INTO COLLECTION(NAME, LOCATION) VALUES
    ('f21', 'https://kojipkgs.fedoraproject.org/repos/f21-build/latest/{arch}/'),
    ('f22', 'https://kojipkgs.fedoraproject.org/repos/f22-build/latest/{arch}/'),
    ('f23', 'https://kojipkgs.fedoraproject.org/repos/f23-build/latest/{arch}/'),
    ('f24', 'https://kojipkgs.fedoraproject.org/repos/f24-build/latest/{arch}/');

INSERT INTO PACKAGE(NAME, UPSTREAM, UPSTREAMDOWNLOADSIZE, UPSTREAMINSTALLSIZE, UPSTREAMVERSION, BASELINE_NAME) VALUES
    ('maven', 'http://maven.apache.org/', 8042383, 9533094, '3.3.3', 'java-devel'),
    ('ant', 'http://ant.apache.org/', 4351965, 37939729, '1.9.4', 'java-devel'),
    ('eclipse-jdt', 'https://www.eclipse.org/', 160990943, 182863820, '4.4.2', 'java-devel'),
    ('jenkins', 'https://jenkins-ci.org/', 63121651, 73292627, '1.615', 'java-devel'),
    ('gradle', 'http://gradle.org/', 45383133, 51291589, '2.4', 'java-devel'),
    ('groovy', 'http://www.groovy-lang.org/', 31968599, 35756588, '2.4.3', 'java-devel');

INSERT INTO COLLECTION_PACKAGE(PACKAGES_NAME, COLLECTIONS_NAME)
    SELECT PACKAGE.NAME, COLLECTION.NAME FROM PACKAGE, COLLECTION
    WHERE PACKAGE.NAME != 'gradle' OR COLLECTION.NAME != 'f21';

COMMIT;
