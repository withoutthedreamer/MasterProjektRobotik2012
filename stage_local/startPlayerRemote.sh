#!/bin/bash

host1="tams66"
host2="tams67"
host3="134.100.13.163"

locDir="/tmp/simpleagent/player"
user="demo"

ssh -f $user@$host1 "cd $locDir; $locDir/startPlayer.sh"
ssh -f $user@$host3 "cd $locDir; $locDir/startPlayer.sh"
ssh -f $user@$host2 "cd $locDir; $locDir/startPlayer.sh"
