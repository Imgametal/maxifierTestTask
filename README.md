# maxifierTestTask - gateway cache

предполагаемый формат внешнего сервиса - поставщика идентификаторов -  список идентификаторов в формате json.

формат предоставляемого gateway - POST запрос с path parameter, для какой последовательности вернуть валидный идентификатор.
пример - curl -XPOST localhost:8080/api/v1/getId/fooPK
где fooPK - id последовательности, для которой нужно получить Id.
 
 Как бороться с потерей данных и статистики.
 Можно, конечно прикрутить внешнее хранилище - можно конечно сбрасывать всю статистику и полученые id на диск, читать из памяти. При старте загружать состояние с диска. можно скидывать в бд, которая будет это делать за вас.
 Но лучшим решением будет заскалировать сервис, распределяя запросы по репликам по consistent_hash.
возможные идеи см в todo

Параметры сервиса
cache.storeHitsTimeSeconds=3600 # время хранения статистики попаданий.
cache.storeMissTimeoutSeconds=60 # время хранния статистики промахов
cache.reserveThreshold=0.2 # порог срабатывания, когда надо продолжать обогощать кеш. Например, Если количество доступных ID стало меньше, чем 20% от количества попаданий в кеш за последний час(3600с) то надо подогреть кеш. loadFactor наизнанку. 
cache.futureReservation=1.5 # коэффициент запаса. Используется при подсчете необходимого количества ID для запроса во внешний поставщик идентификаторов.
cache.startShiftIdsCount=20 # используется для сглаживания пиков на начальном этапе работы с новым sequenceId, по которому еще нет запрошеных Id. При первой попытке получить идентификатор по такому sequenceId, запрашиваем сразу не менее 20 Id.

