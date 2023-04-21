#!/bin/sh

APP_PATH=/evolution_bot

cd $APP_PATH
OPTIONS='-Dbot.properties="~/.si/bot/config.properties" -Dbot.keyfile="~/.si/jasypt.txt"'
java $OPTIONS -jar evolution_bot.jar "Minion"