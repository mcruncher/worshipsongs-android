git clone https://github.com/crunchersaspire/worshipsongs-db-dev.git bundle-db
echo "Copy file into assest dir"
cp -rf bundle-db/songs.sqlite app/src/main/assets/
ls -l app/src/main/assets
rm -rf bundle-db
