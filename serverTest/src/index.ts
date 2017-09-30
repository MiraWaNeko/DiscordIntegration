import { readFile } from 'fs';
import { join, resolve as pathResolve } from 'path';
import { exit } from 'process';

import { CIMinecraftForgeServerTest } from 'ci-minecraft-forge-server-test';

const buildLibsDirectory = pathResolve('..', 'build', 'libs');
const serverDirectory = pathResolve('..', 'run');

let minecraftVersion = null;
let forgeVersion = null;

let modFilepath = null;

function getVersions() {
    return new Promise((resolve, reject) => {
        readFile(pathResolve('..', 'build.gradle'), 'UTF-8', (err, data) => {
            if (err != null) {
                reject(err);

                return;
            }

            let modVersion = null;

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

            modFilepath = join(buildLibsDirectory, `DiscordIntegration-mc${minecraftVersion}-${modVersion}.jar`);

            resolve();
        });
    });
}

const tester = new CIMinecraftForgeServerTest();
getVersions()
    .then(() => {
        tester
            .setVersions(minecraftVersion, forgeVersion)
            .acceptEULA()
            .useServerProperties()
            .setServerDirectory(serverDirectory)
            .addLocalMod(modFilepath)
            .addCommand("/say Hello from " + minecraftVersion);

        return tester.installForge()
            .then(() => tester.runServer());
    })
    .catch(err => {
        console.error(err);
        exit(1);
    });
