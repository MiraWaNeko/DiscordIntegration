import { spawn } from 'child_process';
import { createReadStream, createWriteStream, open, readFile, writeFile } from 'fs';
import { get } from 'http';
import { basename, join } from 'path';
import { exit } from 'process';

import { lte, minor } from 'semver';

const buildLibsDirectory = join(__dirname, '..', '..', 'build', 'libs');
const serverDirectory = join(__dirname, '..', '..', 'run');

let minecraftVersion = null;
let minecraftSemver = null;
let forgeVersion = null;
let modVersion = null;

let installerFilename = null;
let installerFilepath = null;
let jarFilename = null;
let jarFilepath = null;
let modFilename = null;
let modFilepath = null;

let minecraftServer = null;
let minecraftServerTimeout = null;

function getVersions() {
    return new Promise((resolve, reject) => {
        readFile('../build.gradle', 'UTF-8', (err, data) => {
            if (err != null) {
                reject(err);

                return;
            }

            data
                .split('\n')
                .forEach((line, i) => {
                    if (line.startsWith('def')) {
                        const varName = line.split(' ')[1];
                        const varValue = line.split('\'')[1];

                        if (varName === 'mcVersion') {
                            minecraftVersion = varValue;
                        } else if (varName === 'forgeVersion') {
                            forgeVersion = varValue;
                        } else if (varName === 'modVersion') {
                            modVersion = varValue;
                        }
                    }
                });

            if (minecraftVersion == null || forgeVersion == null) {
                reject('Could not find Minecraft and/or Forge version');

                return;
            }

            if (modVersion == null) {
                reject('Could not find mod version');

                return;
            }

            minecraftSemver = minecraftVersion;

            if (minecraftVersion.split('.').length === 2) {
                minecraftSemver += '.0';
            }

            if (lte(minecraftSemver, '1.7.10')) {
                forgeVersion = `${minecraftVersion}-${forgeVersion}-${minecraftVersion}`;
            } else {
                forgeVersion = `${minecraftVersion}-${forgeVersion}`;
            }

            jarFilename = `forge-${forgeVersion}-universal.jar`;
            jarFilepath = join(serverDirectory, jarFilename);
            installerFilename = `forge-${forgeVersion}-installer.jar`;
            installerFilepath = join(serverDirectory, installerFilename);
            modFilename = `DiscordIntegration-mc${minecraftVersion}-${modVersion}.jar`;
            modFilepath = join(buildLibsDirectory, modFilename);

            resolve();
        });
    });
}

function installForge() {
    return new Promise
        ((resolve, reject) => {
            open(jarFilepath, 'wx', (err, fd) => {
                if (err && err.code === 'EEXIST') {
                    // Forge already installed
                    resolve(false);

                    return;
                }

                // Downloading Forge installer
                const installerFilestream = createWriteStream(installerFilepath);
                get(
                    // tslint:disable-next-line:no-http-string
                    `http://files.minecraftforge.net/maven/net/minecraftforge/forge/${forgeVersion}/${installerFilename}`,
                    response => {
                        response.pipe(installerFilestream);
                        response.on('end', () => resolve(true));
                    },
                );
            });
        })
        .then(install => {
            if (!install) {
                return;
            }

            return new Promise((resolve, reject) => {
                // Installing Forge
                const installer = spawn(
                    'java',
                    [
                        '-jar',
                        installerFilename,
                        '--installServer',
                    ],
                    {
                        cwd: serverDirectory,
                    },
                );

                installer.on('close', code => {
                    if (code !== 0) {
                        reject('Installer failed');

                        return;
                    }

                    // Forge installed
                    resolve();
                });
            });
        });
}

function acceptEULA() {
    return new Promise((resolve, reject) => {
        writeFile(
            join(serverDirectory, 'eula.txt'),
            'eula=true',
            err => {
                if (err != null) {
                    return reject(err);
                }
                resolve();
            },
        );
    });
}

function writeServerProperties() {
    return new Promise
        ((resolve, reject) => {
            writeFile(
                join(serverDirectory, 'server.properties'),
                `allow-nether=false
server-port=${25565 + minor(minecraftSemver)}
spawn-npcs=false
white-list=true
spawn-animals=false
snooper-enabled=false
online-mode=true
max-players=1
spawn-monsters=false
generate-structures=false`,
                err => {
                    if (err != null) {
                        return reject(err);
                    }
                    resolve();
                },
            );
        });
}

function addMod(filePath) {
    return new Promise((resolve, reject) => {
        let errored = false;
        const modReadStream = createReadStream(filePath);
        modReadStream.on('error', () => {
            errored = true;
            reject(`Could not read mod file : ${basename(filePath)}`);
        });
        const modWriteStream = createWriteStream(join(serverDirectory, 'mods', basename(filePath)));
        modReadStream.on('error', () => {
            errored = true;
            reject(`Could not write mod file : ${basename(filePath)}`);
        });
        modWriteStream.on('close', () => {
            if (!errored) {
                resolve();
            }
        });
        modReadStream.pipe(modWriteStream);
    });
}

function runServer() {
    return new Promise((resolve, reject) => {
        minecraftServer = spawn(
            'java',
            [
                '-jar',
                jarFilename,
                '--',
                'nogui',
            ],
            {
                cwd: serverDirectory,
            },
        );

        let errored = true;

        minecraftServerTimeout = setTimeout(
            () => {
                minecraftServer.kill();
            },
            3e4,
        );

        minecraftServer.stdout.on('data', data => {
            console.log(data.toString().trim());
            if (data.toString().split(': Done (').length > 1) {
                errored = false;
                clearTimeout(minecraftServerTimeout);
                minecraftServer.stdin.write('stop\n');
            }
            if (data.toString().split('Fatal errors were detected during the transition').length > 1) {
                errored = true;
            }
        });

        minecraftServer.on('close', code => {
            if (code !== 0 || errored) {
                return reject('Minecraft crashed or took to long');
            }

            resolve();
        });
    });
}

getVersions()
    .then(installForge)
    .then(acceptEULA)
    .then(writeServerProperties)
    .then(() => addMod(modFilepath))
    .then(runServer)
    .catch(err => {
        console.error(err);
        exit(1);
    });
