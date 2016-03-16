BEGIN;

INSERT INTO COLLECTION(NAME, LOCATION) VALUES
    ('f22', 'https://kojipkgs.fedoraproject.org/repos/f22-build/latest/{arch}/'),
    ('f23', 'https://kojipkgs.fedoraproject.org/repos/f23-build/latest/{arch}/'),
    ('f24', 'https://kojipkgs.fedoraproject.org/repos/f24-build/latest/{arch}/'),
    ('f25', 'https://kojipkgs.fedoraproject.org/repos/f21-build/latest/{arch}/');

INSERT INTO MODULE(NAME) VALUES
    ('java-headless'),
    ('java'),
    ('java-devel'),
    ('maven'),
    ('ant'),
    ('eclipse-jdt'),
    ('jenkins'),
    ('gradle'),
    ('groovy'),
    ('freemind'),
    ('maven-local'),
    ('gradle-local'),
    ('core'),
    ('buildsys-build');

INSERT INTO MODULE_RPMS(MODULE_NAME, RPMS) VALUES
    ('core', 'audit'),
    ('core', 'basesystem'),
    ('core', 'bash'),
    ('core', 'coreutils'),
    ('core', 'cronie'),
    ('core', 'curl'),
    ('core', 'dhcp-client'),
    ('core', 'dnf'),
    ('core', 'dnf-yum'),
    ('core', 'e2fsprogs'),
    ('core', 'filesystem'),
    ('core', 'glibc'),
    ('core', 'grubby'),
    ('core', 'hostname'),
    ('core', 'initscripts'),
    ('core', 'iproute'),
    ('core', 'iputils'),
    ('core', 'kbd'),
    ('core', 'less'),
    ('core', 'man-db'),
    ('core', 'ncurses'),
    ('core', 'openssh-clients'),
    ('core', 'openssh-server'),
    ('core', 'parted'),
    ('core', 'passwd'),
    ('core', 'plymouth'),
    ('core', 'policycoreutils'),
    ('core', 'procps-ng'),
    ('core', 'rootfiles'),
    ('core', 'rpm'),
    ('core', 'selinux-policy-targeted'),
    ('core', 'setup'),
    ('core', 'shadow-utils'),
    ('core', 'sudo'),
    ('core', 'systemd'),
    ('core', 'systemd-udev'),
    ('core', 'util-linux'),
    ('core', 'vim-minimal'),

    ('buildsys-build', 'bash'), ('buildsys-build', 'bzip2'), ('buildsys-build', 'coreutils'), ('buildsys-build', 'cpio'),
    ('buildsys-build', 'diffutils'), ('buildsys-build', 'fedora-release'), ('buildsys-build', 'findutils'), ('buildsys-build', 'gawk'),
    ('buildsys-build', 'gcc'), ('buildsys-build', 'gcc-c++'), ('buildsys-build', 'grep'), ('buildsys-build', 'gzip'),
    ('buildsys-build', 'info'), ('buildsys-build', 'make'), ('buildsys-build', 'patch'), ('buildsys-build', 'redhat-rpm-config'),
    ('buildsys-build', 'rpm-build'), ('buildsys-build', 'sed'), ('buildsys-build', 'shadow-utils'), ('buildsys-build', 'tar'),
    ('buildsys-build', 'unzip'), ('buildsys-build', 'util-linux'), ('buildsys-build', 'which'), ('buildsys-build', 'xz'),

    ('java-headless', 'java-1.8.0-openjdk-headless'),
    ('java', 'java-1.8.0-openjdk'),
    ('java-devel', 'java-1.8.0-openjdk-devel');

INSERT INTO MODULE_MODULE(MODULE_NAME, PARENTS_NAME) VALUES
    ('java-headless', 'core'),
    ('java', 'core'),
    ('java-devel', 'core'),
    ('maven', 'java-devel'),
    ('ant', 'java-devel'),
    ('eclipse-jdt', 'java'),
    ('jenkins', 'java-headless'),
    ('gradle', 'java-devel'),
    ('groovy', 'java-devel'),
    ('freemind', 'java'),
    ('maven-local', 'buildsys-build'),
    ('gradle-local', 'buildsys-build');

INSERT INTO COLLECTION_MODULE(MODULES_NAME, COLLECTIONS_NAME)
    SELECT MODULE.NAME, COLLECTION.NAME FROM MODULE, COLLECTION;

COMMIT;
