#!/bin/bash

host1="tams66"
host2="tams67"
host3="134.100.13.163"

locDir="/tmp/simpleagent/player"
user="demo"

ssh -f $user@$host1 "killall player; killall player; killall player"
ssh -f $user@$host3 "killall player; killall player; killall player"
ssh -f $user@$host2 "killall player; killall player; killall player"
