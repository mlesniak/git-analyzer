#!/bin/sh

cat <<EOF >/tmp/1
#!/bin/sh
exec java -jar "\$0" "\$@"
EOF

name=$(basename $1 "-all.jar")
cat /tmp/1 "$1" >"$name"
chmod a+x "$name"

rm /tmp/1