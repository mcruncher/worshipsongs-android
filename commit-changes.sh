$GRADLE_HOME/bin/gradle clean assembleRelease
git checkout origin/$GIT_BRANCH
git remote set-url origin https://cruncherapp:$GITHUB_PASSWORD@github.com/mcruncher/worshipsongs-android.git
git config user.email "github@mcruncher.com"
git config user.name "cruncherapp"
git commit -am "Updated verson name"
git push origin HEAD:$GIT_BRANCH