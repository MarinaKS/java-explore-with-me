## Explore-with-me
Многомодульный сервис - афиша мероприятий.
Он позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них. 

API разделена на три части:
- публичная: /;
- закрытая: /users;
- административная: /admin;

Также реализован сбор статистики.
## Как запустить локально
```bash
git clone git@github.com:MarinaKS/java-explore-with-me.git
cd java-explore-with-me
mvn clean install
docker-compose up -d
```
   
