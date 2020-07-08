echo  $EZML_GATEWAY_API_URL

rm /var/www/html/static/config.js
touch /var/www/html/static/config.js
echo 'window.API_URI = "'$EZML_GATEWAY_API_URL'"; ' >> /var/www/html/static/config.js
echo 'console.log("window.API_URI=", window.API_URI);' >> /var/www/html/static/config.js

cat /var/www/html/static/config.js

nginx -g 'daemon off;'