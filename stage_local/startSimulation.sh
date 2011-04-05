#!/bin/bash
# 2011-03-30 Sebastian Rockel

killall player
killall player
killall player
killall playernav


player ~/robotcolla/SimpleAgent/player/uhh1.cfg &
sleep 2
cd ~/robotcolla/SimpleAgent/player/
./startPlanner.sh &
