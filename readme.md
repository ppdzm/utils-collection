#GPG
- gpg --gen-key
- gpg --list-keys
- gpg --keyserver hkp://pool.sks-keyservers.net --send-keys
- gpg --export 278195317@qq.com | curl -T - https://keys.openpgp.org