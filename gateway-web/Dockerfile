FROM nginx:1.18
COPY ./dist /var/www/html

RUN rm /etc/nginx/conf.d/default.conf
COPY ./custom.conf /etc/nginx/conf.d/ 
RUN  cat  /etc/nginx/conf.d/custom.conf


COPY ./custom.sh /var/www/html/ 
RUN chmod 777 /var/www/html/custom.sh
RUN  cat  /var/www/html/custom.sh

RUN  echo  $EZML_GATEWAY_API_URL

EXPOSE 9091
CMD [ "sh" ,"-c" ,"/var/www/html/custom.sh" ]  