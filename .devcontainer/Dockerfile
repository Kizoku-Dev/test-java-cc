# Use the [Choice] comment to indicate option arguments that should appear in VS Code UX. Use a comma separated list.
#
# [Choice] Debian OS version: bullseye, buster
ARG VARIANT="24.04"
FROM ubuntu:${VARIANT}

# Use the [Option] comment to specify true/false arguments that should appear in VS Code UX
#
# [Option] Install zsh
ARG INSTALL_ZSH="true"
# [Option] Upgrade OS packages to their latest versions
ARG UPGRADE_PACKAGES="true"
# Install needed packages and setup non-root user. Use a separate RUN statement to add your own dependencies.
ARG USERNAME=dev
ARG USER_UID=1000
ARG USER_GID=$USER_UID
# Install needed packages and setup non-root user. Use a separate RUN statement to add your own dependencies.

RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
    git git-lfs curl \
    bash-completion zsh \
    sudo \
    && rm -rf /var/lib/apt/lists/*

RUN userdel ubuntu && mv /home/ubuntu "/home/${USERNAME}"
RUN groupadd -g "${USER_GID}" "${USERNAME}"
RUN useradd "${USERNAME}" -u "${USER_UID}" -g "${USER_GID}" -m -s /bin/zsh
RUN usermod -a -G sudo ${USERNAME}
RUN echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers
COPY .zshrc "/home/${USERNAME}/.zshrc"

#[Optional] Uncomment this section to install additional OS packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
     && apt-get -y install --no-install-recommends \
     openjdk-21-jdk maven \
     postgresql-client \
     && rm -rf /var/lib/apt/lists/*

USER ${USER_UID}
WORKDIR /workspace
