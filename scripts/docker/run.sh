docker run --detach \
--volume $(pwd)/data:/evolution_bot/data \
--volume $(pwd)/logs:/evolution_bot/logs \
--volume ~/.si:/.si \
wcht_papaya/evolution_bot:1.0.0