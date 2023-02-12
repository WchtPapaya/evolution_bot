if [ -z $1 ]
    then
        echo "No jar file name supplied"
        exit 1
fi

cp $1 ./evolution_bot.jar

docker image build \
 --tag wcht_papaya/evolution_bot:1.0.0 \
 ./

 rm evolution_bot.jar
 
 echo "image build for evolution bot completed"