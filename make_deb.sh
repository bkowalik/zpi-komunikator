#!/bin/sh

# skrypt zakłada, że "bower install" zostało wykonane
# i wszystkie zależności są już ściągnięte


pushd client

zip -r my_application.nw *
rm -f devcom_client
rm -f my_application.nw
cat /usr/bin/nw my_application.nw > devcom_client

popd

mkdir devcom
pushd devcom

mkdir DEBIAN
mkdir -p opt/devcom
mkdir -p usr/bin

cp ../client/devcom_client opt/devcom/devcom
chmod +x opt/devcom/devcom

# required on debian-like
cp ../libffmpegsumo.so opt/devcom/

cp /usr/lib/node-webkit/nw.pak opt/devcom/nw.pak
cp /usr/lib/node-webkit/icudtl.dat opt/devcom/icudtl.dat

cat > usr/bin/devcom <<EOF
#!/bin/sh

/opt/devcom/devcom
EOF

chmod +x usr/bin/devcom

cat > DEBIAN/control <<EOF
Source: devcom
Package: devcom
Version: 0.1
Section: misc
Priority: optional
Architecture: all
Maintainer: mgw
Description: Developers' communicator
 stub
EOF

popd

dpkg-deb -b devcom

