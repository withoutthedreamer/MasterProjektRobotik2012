#!/bin/bash

locDir="/tmp/simpleagent/player"
user="demo"

source killPlayerRemote.sh "$1"

if [ -z $1 ] ; then
  host1="tams66"
  host2="tams67"
  host3="134.100.13.163"

  ssh -f $user@$host1 "cd $locDir; $locDir/startPlayer.sh"
  ssh -f $user@$host3 "cd $locDir; $locDir/startPlayer.sh"
  ssh -f $user@$host2 "cd $locDir; $locDir/startPlayer.sh"
else
  host="$1"
  ssh -f $user@$host "cd $locDir; $locDir/startPlayer.sh"
  sleep 5
  if [ $host == "tams66" ] ; then
    playernav localhost:6665 localhost:6672 &
    ssh $host"_L6671"
  fi
  if [ $host == "tams67" ] ; then
    #playernav localhost:6665 localhost:6674 &
    ssh $host"_L6673"
  fi
fi


    #ssh -F "/Users/sebastian/.ssh/tams" $host"_L6673"
