echo "Build image started"
echo "Working dir $(pwd)"
cp ./target/*jar-with-dependencies.jar ./evolution_bot.jar

docker image build \
 --tag wcht_papaya/evolution_bot:1.0.0 \
 ./

 rm ./evolution_bot.jar
 
 echo "image build for evolution bot completed"