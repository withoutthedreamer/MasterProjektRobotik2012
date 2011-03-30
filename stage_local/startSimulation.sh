#!/bin/bash
# 2011-03-30 Sebastian Rockel

killall player
killall player
killall player
killall playernav

player uhh1.cfg &
sleep 2
./startPlanner.sh &
