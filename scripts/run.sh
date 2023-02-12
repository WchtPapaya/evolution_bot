docker run --detach \
-e BOT_NAME=${1} \
-e TELEGRAM_KEY=${2} \
-e DISCORD_KEY=${3} \
--volume $(pwd)/data:/evolution_bot/data \
--volume $(pwd)/logs:/evolution_bot/logs \
wcht_papaya/evolution_bot:1.0.0